package com.example.mytakeout.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.mytakeout.MyBitmapFactory;
import com.example.mytakeout.R;
import com.example.mytakeout.base.BaseFragment;
import com.example.mytakeout.ui.video.LdDecoder;
import com.example.mytakeout.ui.video.LdDecoderLisenter;
import com.example.mytakeout.ui.video.LdSurfaceView;
import com.example.mytakeout.utils.LogUtils;
import com.example.mytakeout.ui.video.VideoPlayViewGL;
import com.ldvideo.JniUtils;

import java.nio.ByteBuffer;

import butterknife.BindView;

/**
 * Created by zhuwujing on 2018/8/7.
 */

public class MoreFragment extends BaseFragment implements LdDecoderLisenter {
    @BindView(R.id.btn_anim)
    Button btnAnim;
    @BindView(R.id.rotate_iv)
    ImageView rotateIv;
    @BindView(R.id.layoutVideo)
    LinearLayout mVideoParentLL;
    @BindView(R.id.image_result)
    ImageView mImageView;
    @BindView(R.id.image_result01)
    ImageView mImageView01;
    private LdSurfaceView mLdSurfaceView;
    private LdDecoder mLdDecoder;
    private Handler mHandler;
    private VideoPlayViewGL mVideoPlayViewGL;


    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_more;
    }

    @Override
    protected void init() {
//        mLdSurfaceView = new LdSurfaceView(getActivity());
//        mVideoParentLL.addView(mLdSurfaceView);
        mVideoPlayViewGL = new VideoPlayViewGL(getActivity());
        mVideoParentLL.addView(mVideoPlayViewGL);
        mLdDecoder = new LdDecoder();
        mLdDecoder.setLdDecoderLisenter(this);
        mHandler = new Handler();
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mLdDecoder.startCodec(null);
            }
        }).start();
    }

    /**
     * 回调在子线程中
     *
     * @param rgbaData 注意数据是rgba
     */
    @Override
    public void onFrame(byte[] rgbaData) {
        LogUtils.e("======onFrame" + rgbaData.length);
        JniUtils jniUtils = new JniUtils();
        Bitmap bitmap = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888);
        ByteBuffer buffer = ByteBuffer.allocate(640 * 480 * 4);
        buffer.put(rgbaData);
        buffer.rewind();
        bitmap.copyPixelsFromBuffer(buffer);

        byte[] bytes = MyBitmapFactory.bitmap2RGB2(bitmap);

        byte[] result = jniUtils.remap(bytes, 480, 640, 240, 240);
//        final Bitmap myBitmap = MyBitmapFactory.createMyBitmap(result, 240, 240);

        mVideoPlayViewGL.DrawBitmap(result, 1, 1, 240, 240, 0);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
//                mImageView.setImageBitmap(bitmap);
//                mImageView01.setImageBitmap(myBitmap);
            }
        });
    }
}
