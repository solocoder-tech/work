package com.example.mytakeout.base;

import android.app.Application;
import android.content.Context;


/**
 * Created by zhuwujing on 2018/8/4.
 */

public class BaseApplication extends Application {

    public static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}
