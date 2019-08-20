package com.example.mytakeout.ui.activity;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.mytakeout.R;
import com.example.mytakeout.base.BaseActivity;
import com.example.mytakeout.ui.video.LdDecoder;
import com.example.mytakeout.utils.DecodeCallback;
import com.example.mytakeout.utils.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoActivity extends BaseActivity implements SurfaceHolder.Callback {
    @BindView(R.id.layoutVideo)
    LinearLayout mVideoParentLL;
    @BindView(R.id.image)
    ImageView mImageView;

    private static final String SAMPLE = Environment.getExternalStorageDirectory() + "/video/2.mp4";
    private static final String TAG = MainActivity.class.getSimpleName();
    private WorkThread mWorkThread = null;
    private Bitmap mBitmap;
    private Image mImage;

    private DecodeCallback mDecodeCallback;
    private SurfaceView mSurfaceView;
    private LdDecoder mLdDecoder;

    public void setDecodeCallback(DecodeCallback decodeCallback) {
        mDecodeCallback = decodeCallback;
    }

    /**
     * YYYYYYYY UU VV
     */
    public static final int FILE_TypeI420 = 1;
    /**
     * YYYYYYYY VU VU
     */
    public static final int FILE_TypeNV21 = 2;
    public static final int FILE_TypeJPEG = 3;
    private static final boolean VERBOSE = false;
    private static final long DEFAULT_TIMEOUT_US = 10000;
    private static final int COLOR_FormatI420 = 1;
    private static final int COLOR_FormatNV21 = 2;
    private static final int decodeColorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible;

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case 111:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        mSurfaceView = new SurfaceView(this);
        /*下面设置Surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到用户面前*/
        mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceView.getHolder().addCallback(this);
        mVideoParentLL.addView(mSurfaceView);

        mLdDecoder = new LdDecoder();
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initDatas() {

    }

    @Override
    protected void initEvents() {

    }

    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mWorkThread == null) {
            mWorkThread = new WorkThread(holder.getSurface());
            mWorkThread.start();
        }
//        mLdDecoder.startCodec(holder.getSurface());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mWorkThread != null) {
            mWorkThread.interrupt();
        }
//        mLdDecoder.release();
    }

    @OnClick({R.id.image})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image:
//                startActivity(new Intent(this, LocalH264Activity.class));
                //解码
//                LdDecoder ldDecode = new LdDecoder();
//                ldDecode.initCodec();

//                ldDecode.decode();
//                ldDecode.release();
                break;
        }
    }

    private class WorkThread extends Thread {
        private MediaExtractor mMediaExtractor;
        private MediaCodec mMediaCodec;
        private Surface mSurface;

        public WorkThread(Surface surface) {
            this.mSurface = surface;
        }

        @Override
        public void run() {
//            doTesr();
            mLdDecoder.startCodec(mSurface);
        }

        private void doTesr() {
            FileOutputStream videoOutputStream = null;
            FileOutputStream yuvOutputStream = null;
            mMediaExtractor = new MediaExtractor();//数据解析器
            try {
                File videoFile = new File(Environment.getExternalStorageDirectory() + "/video", "output_video1.yuv");
                File yuvFile = new File(Environment.getExternalStorageDirectory() + "/video", "output_video2.yuv");
                videoOutputStream = new FileOutputStream(videoFile);
                yuvOutputStream = new FileOutputStream(yuvFile);
                mMediaExtractor.setDataSource(SAMPLE);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < mMediaExtractor.getTrackCount(); i++) {//遍历数据源音视频轨迹
                MediaFormat format = mMediaExtractor.getTrackFormat(i);
                Log.d(TAG, ">> format i " + i + ": " + format);
                String mime = format.getString(MediaFormat.KEY_MIME);
                Log.d(TAG, ">> mime i " + i + ": " + mime);
                if (mime.startsWith("video/")) {
                    mMediaExtractor.selectTrack(i);
                    try {
                        mMediaCodec = MediaCodec.createDecoderByType(mime);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mMediaCodec.configure(format, mSurface, null, 0);//关联surface和mediacodec
                    break;
                }
            }
            if (mMediaCodec == null) {
                return;
            }
            mMediaCodec.start();//调用start后，如果没有异常信息，就表示成功构建组件
            ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();
            // 每个buffer的元数据包括具体范围偏移及大小 ，及有效数据中相关解码的buffer
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            boolean isEOS = false;
            long startMs = System.currentTimeMillis();

            while (!Thread.interrupted()) {//只要线程不中断
                if (!isEOS) {
                    //返回用有效输出的buffer的索引,如果没有相关buffer可用，就返回-1
                    //如果传入的timeoutUs为0,将立马返回，如果输入buffer可用，将无限期等待
                    //timeoutUs的单位是微秒
                    //dequeueInputBuffer 从输入流队列中取数据进行编码操作
                    int inIndex = mMediaCodec.dequeueInputBuffer(10000);//0.01s
                    if (inIndex >= 0) {
                        ByteBuffer buffer = inputBuffers[inIndex];
                        Log.d(TAG, ">> buffer " + buffer);
                        //把指定通道中的数据按偏移量读取到ByteBuffer中；
                        int sampleSize = mMediaExtractor.readSampleData(buffer, 0);
                        Log.d(TAG, ">> sampleSize " + sampleSize);
                        if (sampleSize < 0) {
                            // dequeueOutputBuffer
                            Log.d(TAG, "InputBuffer BUFFER_FLAG_END_OF_STREAM");
                            mMediaCodec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            isEOS = true;
                        } else {
                            //------
                            handleData(mMediaCodec, videoOutputStream, inIndex, buffer, sampleSize);
                            //-------
                            mMediaCodec.queueInputBuffer(inIndex, 0, sampleSize, mMediaExtractor.getSampleTime(), 0);
                            mMediaExtractor.advance();
                        }
                    }
                }

                int outIndex = mMediaCodec.dequeueOutputBuffer(info, DEFAULT_TIMEOUT_US);
//                outIndex = test02(yuvOutputStream,info, outIndex);
                outputBuffers = test01(yuvOutputStream, outputBuffers, info, startMs, outIndex);
                // All decoded frames have been rendered, we can stop playing now
                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    Log.d(TAG, "OutputBuffer BUFFER_FLAG_END_OF_STREAM");
                    break;
                }
            }
            try {
                mMediaCodec.stop();
                mMediaCodec.release();//释放组件
                if (videoOutputStream != null) {
                    videoOutputStream.close();
                }
                if (yuvOutputStream != null) {
                    yuvOutputStream.close();
                }
                mMediaExtractor.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private int test02(FileOutputStream yuvOutputStream, MediaCodec.BufferInfo info, int outIndex) {
            ByteBuffer outputBuffer;
            while (outIndex > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    outputBuffer = mMediaCodec.getOutputBuffer(outIndex);
                } else {
                    outputBuffer = mMediaCodec.getOutputBuffers()[outIndex];
                }
                if (outputBuffer != null) {
                    outputBuffer.position(0);
                    outputBuffer.limit(info.offset + info.size);
                    byte[] yuvData = new byte[outputBuffer.remaining()];
                    outputBuffer.get(yuvData);

                    try {
                        LogUtils.e("yuvData-----" + yuvData.length);
                        yuvOutputStream.write(yuvData);//buffer 写入到 videooutputstream中
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                        if (null!=onDecodeCallback) {
//                            onDecodeCallback.onFrame(yuvData);
//                        }
                    mMediaCodec.releaseOutputBuffer(outIndex, false);
                    outputBuffer.clear();
                }
                outIndex = mMediaCodec.dequeueOutputBuffer(info, DEFAULT_TIMEOUT_US);
            }
            return outIndex;
        }

        private ByteBuffer[] test01(FileOutputStream yuvOutputStream, ByteBuffer[] outputBuffers, MediaCodec.BufferInfo info, long startMs, int outIndex) {
            switch (outIndex) {
                case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED://当buffer变化时，client必须重新指向新的buffer
                    Log.d(TAG, ">> output buffer changed ");
                    outputBuffers = mMediaCodec.getOutputBuffers();
                    break;
                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED://当buffer的封装格式变化,须指向新的buffer格式
                    Log.d(TAG, ">> output buffer changed ");
                    break;
                case MediaCodec.INFO_TRY_AGAIN_LATER://当dequeueOutputBuffer超时,会到达此case
                    Log.d(TAG, ">> dequeueOutputBuffer timeout ");
                    break;
                default:
                    ByteBuffer buffer = outputBuffers[outIndex];
//                        Image outputImage = mMediaCodec.getOutputImage(outIndex);
                    afterDecode(yuvOutputStream, buffer);
                    // We use a very simple clock to keep the video FPS, or the video
                    // playback will be too fast
                    while (info.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
                        try {
                            sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                    mMediaCodec.releaseOutputBuffer(outIndex, true);
                    break;
            }
            return outputBuffers;
        }
    }

    private void handleData(MediaCodec mediaCodec, FileOutputStream videoOutputStream, int inIndex, ByteBuffer buffer, int sampleSize) {
        ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inIndex);
        byte[] resBuffer = new byte[sampleSize];
        //输入流入队列
        try {
            inputBuffer.get(resBuffer);
            videoOutputStream.write(resBuffer);//buffer 写入到 videooutputstream中
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void afterDecode(FileOutputStream yuvOutputStream, ByteBuffer buffer) {
        try {
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            buffer.clear();
            yuvOutputStream.write(bytes);
//            byte[] bytes1 = getBytes(outputImage);
            LogUtils.e("=====" + buffer.remaining());
            LogUtils.e("=====" + bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] getBytes(Object obj) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(obj);
        out.flush();
        byte[] bytes = bout.toByteArray();
        bout.close();
        out.close();
        return bytes;
    }


    private byte[] getDataFromImage(Image image, int colorFormat) {
        if (colorFormat != COLOR_FormatI420 && colorFormat != COLOR_FormatNV21) {
            throw new IllegalArgumentException("only support COLOR_FormatI420 " + "and COLOR_FormatNV21");
        }
        if (!isImageFormatSupported(image)) {
            throw new RuntimeException("can't convert Image to byte array, format " + image.getFormat());
        }
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];
        if (VERBOSE) Log.v(TAG, "get data from " + planes.length + " planes");
        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    if (colorFormat == COLOR_FormatI420) {
                        channelOffset = width * height;
                        outputStride = 1;
                    } else if (colorFormat == COLOR_FormatNV21) {
                        channelOffset = width * height + 1;
                        outputStride = 2;
                    }
                    break;
                case 2:
                    if (colorFormat == COLOR_FormatI420) {
                        channelOffset = (int) (width * height * 1.25);
                        outputStride = 1;
                    } else if (colorFormat == COLOR_FormatNV21) {
                        channelOffset = width * height;
                        outputStride = 2;
                    }
                    break;
            }
            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();
            if (VERBOSE) {
                Log.v(TAG, "pixelStride " + pixelStride);
                Log.v(TAG, "rowStride " + rowStride);
                Log.v(TAG, "width " + width);
                Log.v(TAG, "height " + height);
                Log.v(TAG, "buffer size " + buffer.remaining());
            }
            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
            if (VERBOSE) Log.v(TAG, "Finished reading data from plane " + i);
        }
        return data;
    }


    private boolean isImageFormatSupported(Image image) {
        int format = image.getFormat();
        switch (format) {
            case ImageFormat.YUV_420_888:
            case ImageFormat.NV21:
            case ImageFormat.YV12:
                return true;
        }
        return false;
    }

}
