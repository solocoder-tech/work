package com.example.mytakeout.ui.video;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Environment;
import android.view.Surface;

import com.example.mytakeout.utils.LogUtils;
import com.example.sweeper.nativelibyuv.NativeYUV;
import com.ldvideo.JniUtils;
import com.wuwang.libyuv.Key;
import com.wuwang.libyuv.YuvUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static java.lang.Thread.sleep;

/**
 * 创建时间：2019/8/2  17:07
 * 作者：5#
 * 描述：输入文件的地址，解码成YUV或者RGB
 */
public class LdDecoder {
    private static final long DEFAULT_TIMEOUT_US = 1000 * 10;
    //要解码的为YUV420
    private final int decodeColorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar;

    private MediaCodec mCodec;
    private MediaCodec.BufferInfo bufferInfo;
    private MediaExtractor mMediaExtractor;

    private LdDecoderLisenter mLdDecoderLisenter;

    public void setLdDecoderLisenter(LdDecoderLisenter ldDecoderLisenter) {
        mLdDecoderLisenter = ldDecoderLisenter;
    }

    /**
     * 开始解码
     * 提取视频通道的数据  h264
     * 针对h264解码得到yuv数据   ok
     * yuv数据转rgb数据
     */
    public void startCodec(final Surface surface) {
        FileOutputStream yuvOutputStream = null;
        try {
            File yuvFile = new File(Environment.getExternalStorageDirectory() + "/video", "ld.yuv");
            yuvOutputStream = new FileOutputStream(yuvFile);
            mMediaExtractor = new MediaExtractor();//数据解析器
            mMediaExtractor.setDataSource(Environment.getExternalStorageDirectory() + "/video/3.mp4");
            for (int i = 0; i < mMediaExtractor.getTrackCount(); i++) {//遍历数据源音视频轨迹
                MediaFormat format = mMediaExtractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                format.setInteger(MediaFormat.KEY_COLOR_FORMAT, decodeColorFormat);
                LogUtils.e("video====" + mime);
                if (mime.startsWith("video/")) {
                    mMediaExtractor.selectTrack(i);
                    mCodec = MediaCodec.createDecoderByType(mime);
                    mCodec.configure(format, surface, null, 0);
                    break;
                }
            }
            if (mCodec == null) {
                return;
            }
            mCodec.start();
            //获取MediaCodec的输入流
            ByteBuffer[] inputBuffers = mCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = mCodec.getOutputBuffers();
            // 每个buffer的元数据包括具体范围偏移及大小 ，及有效数据中相关解码的buffer
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            boolean isEOS = false;
            long startMs = System.currentTimeMillis();
            while (!Thread.interrupted()) {//只要线程不中断
                if (!isEOS) {
                    //返回用有效输出的buffer的索引,如果没有相关buffer可用，就返回-1
                    //如果传入的timeoutUs为0,将立马返回，如果输入buffer可用，将无限期等待
                    //timeoutUs的单位是微秒
                    //dequeueInputBuffer 从输入流队列中取数据进行编码操作 设置解码等待时间，0为不等待，-1为一直等待，其余为时间单位
                    int inIndex = mCodec.dequeueInputBuffer(10000);//0.01s
                    //填充数据到输入流
                    if (inIndex >= 0) {
                        ByteBuffer buffer = inputBuffers[inIndex];
                        //把指定通道中的数据按偏移量读取到ByteBuffer中；
                        int sampleSize = mMediaExtractor.readSampleData(buffer, 0);
                        LogUtils.e("video====sampleSize===" + sampleSize);
                        if (sampleSize < 0) {
                            // dequeueOutputBuffer
                            mCodec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            isEOS = true;
                        } else {
                            //------
                            //-------
                            mCodec.queueInputBuffer(inIndex, 0, sampleSize, mMediaExtractor.getSampleTime(), 0);
                            mMediaExtractor.advance();
                        }
                    }
                }

                int outIndex = mCodec.dequeueOutputBuffer(info, DEFAULT_TIMEOUT_US);
                switch (outIndex) {
                    case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED://当buffer变化时，client必须重新指向新的buffer
                        LogUtils.e(">> output buffer changed ");
                        outputBuffers = mCodec.getOutputBuffers();
                        break;
                    case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED://当buffer的封装格式变化,须指向新的buffer格式
                        LogUtils.e(">> output buffer changed ");
                        break;
                    case MediaCodec.INFO_TRY_AGAIN_LATER://当dequeueOutputBuffer超时,会到达此case
                        LogUtils.e(">> dequeueOutputBuffer timeout ");
                        break;
                    default:
                        LogUtils.e(">> dequeueOutputBuffer default ");
                        //解码后的数据在这里输出
                        ByteBuffer outputBuffer = outputBuffers[outIndex];
                        byte[] bytes = new byte[outputBuffer.remaining()];
                        outputBuffer.get(bytes);
                        //------
                        if (bytes.length > 0) {
                            LogUtils.e("createMyBitmap====codec==" + bytes.length);//460800
                            int width = 640;
                            int height = 480;
                            byte[] rgbaData = new byte[width * height * 4];
                            YuvUtils.I420ToRgba(Key.I420_TO_RGBA, bytes, rgbaData, width, height);
                            LogUtils.e("createMyBitmap====grbData==" + rgbaData.length);//307200
                            if (mLdDecoderLisenter != null) {
                                mLdDecoderLisenter.onFrame(rgbaData);
                            }
                            yuvOutputStream.write(bytes);
                        }
                        //-------
                        while (info.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
                            try {
                                sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                        mCodec.releaseOutputBuffer(outIndex, true);
                        break;
                }
                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    LogUtils.e("OutputBuffer BUFFER_FLAG_END_OF_STREAM");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 是否支持
     *
     * @param colorFormat
     * @param caps
     * @return
     */
    private boolean isColorFormatSupported(int colorFormat, MediaCodecInfo.CodecCapabilities caps) {
        for (int c : caps.colorFormats) {
            if (c == colorFormat) {
                return true;
            }
        }
        return false;
    }

    public void release() {
        if (null != mCodec) {
            mCodec.stop();
            mCodec.release();
            mCodec = null;
        }
    }

    public void geth264Data() {

    }

    public void decode(byte[] h264Data) {
        int inputBufferIndex = mCodec.dequeueInputBuffer(DEFAULT_TIMEOUT_US);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                inputBuffer = mCodec.getInputBuffer(inputBufferIndex);
            } else {
                inputBuffer = mCodec.getInputBuffers()[inputBufferIndex];
            }
            if (inputBuffer != null) {
                inputBuffer.clear();
                inputBuffer.put(h264Data, 0, h264Data.length);
                mCodec.queueInputBuffer(inputBufferIndex, 0, h264Data.length, 0, 0);
            }
        }
        int outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, DEFAULT_TIMEOUT_US);
        ByteBuffer outputBuffer;
        while (outputBufferIndex > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                outputBuffer = mCodec.getOutputBuffer(outputBufferIndex);
            } else {
                outputBuffer = mCodec.getOutputBuffers()[outputBufferIndex];
            }
            if (outputBuffer != null) {
                outputBuffer.position(0);
                outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
                byte[] yuvData = new byte[outputBuffer.remaining()];
                outputBuffer.get(yuvData);

//                if (null != onDecodeCallback) {
//                    onDecodeCallback.onFrame(yuvData);
//                }
                mCodec.releaseOutputBuffer(outputBufferIndex, false);
                outputBuffer.clear();
            }
            outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, DEFAULT_TIMEOUT_US);
        }
    }

    public byte[] NV21toRGBA(byte[] data, int width, int height) {
        int size = width * height;
        byte[] bytes = new byte[size * 4];
        int y, u, v;
        int r, g, b;
        int index;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                index = j % 2 == 0 ? j : j - 1;

                y = data[width * i + j] & 0xff;
                u = data[width * height + width * (i / 2) + index + 1] & 0xff;
                v = data[width * height + width * (i / 2) + index] & 0xff;

                r = y + (int) 1.370705f * (v - 128);
                g = y - (int) (0.698001f * (v - 128) + 0.337633f * (u - 128));
                b = y + (int) 1.732446f * (u - 128);

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);

                bytes[width * i * 4 + j * 4 + 0] = (byte) r;
                bytes[width * i * 4 + j * 4 + 1] = (byte) g;
                bytes[width * i * 4 + j * 4 + 2] = (byte) b;
                bytes[width * i * 4 + j * 4 + 3] = (byte) 255;//透明度
            }
        }
        return bytes;
    }

}
