package com.example.quchangkeji.mytakeout.base;

import android.app.Activity;

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

    private Stack<Activity> mActivityStack = new Stack<>();

    /**
     * 添加
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (mActivityStack != null) {
            mActivityStack.add(activity);
        }
    }

    /**
     * 删除当前activity,及栈顶元素
     *
     * @param activity
     */
    public void finishActivity(Activity activity) {
        if (mActivityStack != null && mActivityStack.size() > 0) {
            Activity popActivity = mActivityStack.pop();
            activity.finish();
        }
    }

    /**
     * 删除所有元素
     */
    public void finishAllActivity() {
        if (mActivityStack != null) {
            for (int i = mActivityStack.size() - 1; i >= 0; i--) {
                Activity activity = mActivityStack.get(i);
                activity.finish();
                mActivityStack.remove(i);
            }
        }
    }

    /**
     * 删除指定元素
     */
    public void finishTargetActivity(Activity target){
        if (mActivityStack != null){
            for (int i = mActivityStack.size() - 1; i >= 0; i--) {
                Activity activity = mActivityStack.get(i);
                if (target.getClass().getName().equals(activity.getClass().getName())){
                    activity.finish();
                    mActivityStack.remove(i);
                }
            }
        }
    }
}
