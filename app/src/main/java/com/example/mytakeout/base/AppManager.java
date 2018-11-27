package com.example.mytakeout.base;

import android.app.Activity;
import android.os.Process;

import java.lang.ref.WeakReference;
import java.util.Stack;

/**
 * Created by zhuwujing on 2018/8/21.
 * Activity的管理
 * 单例  实现中添加,删除当前,删除所有,删除指定
 */

public class AppManager {
    /*
    懒汉式  饿汉式  双重检查锁
     */
    private static AppManager mAppManager = null;

    private AppManager() {
    }

    public static AppManager getInstance() {
        if (mAppManager == null) {
            synchronized (AppManager.class) {
                if (mAppManager == null) {
                    mAppManager = new AppManager();
                }
            }
        }
        return mAppManager;
    }

    private Stack<WeakReference<Activity>> mActivityStack = new Stack<>();

    /**
     * 添加
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (mActivityStack != null) {
            mActivityStack.add(new WeakReference<Activity>(activity));
        }
    }

    /**
     * 删除当前activity,及栈顶元素
     */
    public void finishActivity() {
        if (mActivityStack != null && mActivityStack.size() > 0) {
            Activity popActivity = mActivityStack.pop().get();
            popActivity.finish();
            mActivityStack.remove(popActivity);
        }
    }

    /**
     * 删除所有元素
     */
    public void finishAllActivity() {
        if (mActivityStack != null) {
            for (int i = mActivityStack.size() - 1; i >= 0; i--) {
                Activity activity = mActivityStack.get(i).get();
                activity.finish();
                mActivityStack.remove(i);
            }
        }
    }

    /**
     * 退出app,推出前要先关闭所有activity
     */

    public void quitApp() {
        android.os.Process.killProcess(Process.myPid());
    }

    /**
     * 删除指定元素
     */
    public void finishTargetActivity(Activity target) {
        if (mActivityStack != null) {
            for (int i = mActivityStack.size() - 1; i >= 0; i--) {
                Activity activity = mActivityStack.get(i).get();
                if (target.getClass().getName().equals(activity.getClass().getName())) {
                    activity.finish();
                    mActivityStack.remove(i);
                }
            }
        }
    }
}
