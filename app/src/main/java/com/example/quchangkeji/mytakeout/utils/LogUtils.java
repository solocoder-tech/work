package com.example.quchangkeji.mytakeout.utils;

import android.animation.PropertyValuesHolder;
import android.util.Log;

/**
 * Created by zhuwujing on 2018/8/7.
 */

public class LogUtils {
    private static boolean isDebugger = true;//开
//    private static boolean isDebugger = false;//关

    public static void sysout(String msg) {
        if (isDebugger) {
            System.out.println(msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isDebugger) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebugger) {
            Log.e(tag, msg);
        }
    }
}
