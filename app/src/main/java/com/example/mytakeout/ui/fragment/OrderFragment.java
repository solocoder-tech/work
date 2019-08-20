package com.example.mytakeout.ui.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.mytakeout.MyBitmapFactory;
import com.example.mytakeout.R;
import com.example.mytakeout.base.BaseFragment;
import com.example.mytakeout.ui.video.LdDecoder;
import com.example.mytakeout.utils.LogUtils;
import com.ldvideo.JniUtils;

import java.nio.ByteBuffer;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zhuwujing on 2018/8/7.
 */

public class OrderFragment extends BaseFragment {
    @BindView(R.id.btn_test)
    Button btnTest;
    @BindView(R.id.result_iv)
    ImageView mImageView;
    @BindView(R.id.orignal_iv)
    ImageView orignal_iv;
    @BindView(R.id.btn_video)
    Button btnVideo;

    private byte[] mByteArray;
    private int inHeight;
    private int inWidth;
    //    private int Radius = 511;
    private int Radius = 480;
    private int outHeight;
    private int outWidth;
    private float downX;
    private float downY;
    private float moveX;
    private float moveY;
    private float mAngle_x;
    private float mAngle_y;
    private JniUtils mJniUtils;

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_order;
    }

    @Override
    protected void init() {
        BitmapFactory.Options bitmapRealWidth = getBitmapRealWidth();
//        bmp.getPixels(mByteArray,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
//        mByteArray = MyBitmapFactory.getRGBFromBMP(bmp);
        mJniUtils = new JniUtils();

        Bitmap bmp = MyBitmapFactory.decodeResource(getResources(), R.drawable.test);
//        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test, options);

//        mByteArray = new byte[bmp.getWidth() * bmp.getHeight() * 3];
        mByteArray = MyBitmapFactory.bitmap2RGB2(bmp);
//        for (int i = 0; i < bmp.getHeight(); i++) {
//            for (int k = 0; k < bmp.getWidth(); k++) {
//                int pixel = bmp.getPixel(k, i);
//                mByteArray[i * bmp.getWidth() + k] = (byte) Color.red(pixel);
//                mByteArray[i * bmp.getWidth() + k + 1] = (byte) Color.green(pixel);
//                mByteArray[i * bmp.getWidth() + k + 2] = (byte) Color.blue(pixel);
//            }
//        }
//        int bytes = bmp.getByteCount();
//        ByteBuffer buf = ByteBuffer.allocate(bytes);
//        bmp.copyPixelsToBuffer(buf);
//        mByteArray = buf.array();
        Bitmap myBitmap = MyBitmapFactory.createMyBitmap(mByteArray, bmp.getWidth(), bmp.getHeight());
//        mImageView.setImageBitmap(myBitmap);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bmp.getByteCount());
//        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//        mByteArray =  outputStream.toByteArray();

//        Log.e("size", bmp.getWidth() + "");
//        Log.e("size", bmp.getHeight() + "");
//        Log.e("size", mByteArray.length + "");
//        Log.e("size", 1280 * 960 * 3 + ""); //3686400

        orignal_iv.setImageBitmap(myBitmap);

//        Bitmap myBitmap = MyBitmapFactory.createMyBitmap(mByteArray, i, outHeight);
//        mImageView.setImageBitmap(myBitmap);


//        int inHeight = srcImg.rows;
//        int inWidth = srcImg.cols;
//        inHeight = bmp.getHeight();
//        inWidth = bmp.getWidth();
        inHeight = bmp.getHeight();
        inWidth = bmp.getWidth();

        outHeight = Radius;
//        outWidth = (int) (Radius * sin(50 * PI / 180.0) * 2);
        outWidth = 640;

        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                LogUtils.e("===onTouch===");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moveX = event.getX();
                        moveY = event.getY();

                        mAngle_x += (moveX - downX) * 0.3;

                        if (Math.abs(moveY - downY) > 2 && mAngle_y + (moveY - downY) <= 10 && mAngle_y + (moveY - downY) >= -30) {
                            mAngle_y += (moveY - downY) * 0.3;
                        }

//                        float[] floats = mJniUtils.rectifyMap2(inWidth, inHeight, mAngle_y, 0, mAngle_x, outWidth, outHeight, 511);
                        LogUtils.e("imageview----------");
//                        byte[] cvremap = mJniUtils.cvremap(mByteArray, floats, inHeight, inWidth, outWidth, outHeight);

//                        Bitmap myBitmap = MyBitmapFactory.createMyBitmap(cvremap, outWidth, outHeight);
//                        mImageView.setImageBitmap(myBitmap);

                        downX = moveX;
                        downY = moveY;
                        break;

                    case MotionEvent.ACTION_UP:
                        downX = 0;
                        downY = 0;
                        break;

                }
                return true;
            }
        });
    }

    private BitmapFactory.Options getBitmapRealWidth() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test, options);
        Log.e("size", options.outWidth + "");
        Log.e("size", options.outHeight + "");
        return options;
    }

    @OnClick({R.id.btn_test, R.id.btn_video})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_test:
                //一个app中打开另一个app
//                PackageManager packageManager = getActivity().getPackageManager();
//                String targetPakage = "com.inmotion.sweeprobot.mbot";
////                if (checkPackInfo(targetPakage)) {
////                    Intent intent = packageManager.getLaunchIntentForPackage(targetPakage);
////                    startActivity(intent);
////                } else {
////                    Toast.makeText(getActivity(), "没有安装" + targetPakage, Toast.LENGTH_SHORT).show();
////                }
//                //一个app中打开另一个app的指定Activity
//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                //前提：知道要跳转应用的包名、类名
//                ComponentName componentName = new ComponentName("com.inmotion.sweeprobot.mbot", "com.inmotion.sweeprobot.mbot.Module.Homepage.HomepageActivity");
//                intent.setComponent(componentName);
//                startActivity(intent);

//                LogUtils.e("imageview----------inHeight=" + inHeight + "===inWidth==" + inWidth + "==outHeight==" + outHeight + "==outWidth==" + outWidth);
//                float[] floats = mJniUtils.rectifyMap2(inWidth, inHeight, -30, 0, 75, outWidth, outHeight, 511);
//                LogUtils.e("imageview----------");

//                startActivity(new Intent(getActivity(), VideoActivity.class));
//                startActivity(new Intent(getActivity(), GLVideoActivity.class));
                new LdDecoder().startCodec(null);
                break;
            case R.id.btn_video:
                long startTime = System.currentTimeMillis();
                byte[] cvremap = mJniUtils.remap(mByteArray, 960, 1280, 700, 640);
                LogUtils.e("time========" + (System.currentTimeMillis() - startTime));
                if (cvremap != null) {
                    Log.e("----", cvremap[0] + "");
                    Bitmap myBitmap = MyBitmapFactory.createMyBitmap(cvremap, outWidth, outHeight);
                    mImageView.setImageBitmap(myBitmap);
                }
                break;
        }
    }

    /**
     * 检查包是否存在 * * @param packname * @return
     */
    private boolean checkPackInfo(String packname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    /**
     * @方法描述 Bitmap转RGB
     */
    public static byte[] bitmap2RGB(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();  //返回可用于储存此位图像素的最小字节数

        ByteBuffer buffer = ByteBuffer.allocate(bytes); //  使用allocate()静态方法创建字节缓冲区
        bitmap.copyPixelsToBuffer(buffer); // 将位图的像素复制到指定的缓冲区

        byte[] rgba = buffer.array();
        byte[] pixels = new byte[(rgba.length / 4) * 3];

        int count = rgba.length / 4;

        //Bitmap像素点的色彩通道排列顺序是RGBA
        for (int i = 0; i < count; i++) {

            pixels[i * 3] = rgba[i * 4 + 1];        //R
            pixels[i * 3 + 1] = rgba[i * 4 + 2];    //G
            pixels[i * 3 + 2] = rgba[i * 4 + 3];       //B

        }

        return pixels;
    }


}
