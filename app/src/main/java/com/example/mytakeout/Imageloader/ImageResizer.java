package com.example.mytakeout.Imageloader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.FileDescriptor;

/**
 * Created by zhuwujing on 2018/9/9.
 * 图片的压缩功能的实现
 */

public class ImageResizer {
    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //不会真正的去加载图片,只会解析图片的原始宽高信息
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        //获取采样率
        options.inSampleSize = calculateInsampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor fd, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //不会真正的去加载图片,只会解析图片的原始宽高信息
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);

        //获取采样率
        options.inSampleSize = calculateInsampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    /**
     * 计算压缩率
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public int calculateInsampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }
        //获取图片原始的宽和高
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        int inSampleSize = 1;

        if (outWidth > reqWidth || outHeight > reqHeight) {
            int halfWidth = outWidth / 2;
            int halfHeight = outHeight / 2;
            //宽和高都比要求的大
            while ((halfWidth / inSampleSize) >= reqWidth && (halfHeight / inSampleSize) >= reqHeight) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
