package com.example.mytakeout.Imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by zhuwujing on 2018/9/9.
 */

public class ImageLoader {

    private Context mContext;

    private ImageLoader(Context context) {
        mContext = context.getApplicationContext();
        //获取当前进程可用的最大内存  单位kb
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        //缓存容量
        int cacheSize = maxMemory / 8;

        new LruCache<String, Bitmap>(cacheSize) {
            //计算缓存对象的大小
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };


    }
}
