package com.example.mytakeout.base;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.example.mytakeout.utils.LogUtils;
import com.example.mytakeout.utils.UIUtils;
//import com.squareup.leakcanary.LeakCanary;
//import com.squareup.leakcanary.RefWatcher;


/**
 * Created by zhuwujing on 2018/8/4.
 */

public class BaseApplication extends Application {

    public static Context mContext;
//    private RefWatcher refWatcher;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
//        LeakCanary.install(this);

//        refWatcher = setupLeakCanary();

        boolean apkDebugable = UIUtils.isApkDebugable(this);
        LogUtils.e("apkDebugable==" + apkDebugable);
    }

//    private RefWatcher setupLeakCanary() {
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return RefWatcher.DISABLED;
//        }
//        return LeakCanary.install(this);
//    }

//    public static RefWatcher getRefWatcher(Context context) {
//        BaseApplication leakApplication = (BaseApplication) context.getApplicationContext();
//        return leakApplication.refWatcher;
//    }


}
