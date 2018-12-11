package com.example.mytakeout.ui.views;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.mytakeout.R;
import com.example.mytakeout.utils.LogUtils;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 自定义toast
 *
 * WindowManager  addView()到屏幕，removeView()从屏幕上删除View ，updateViewLayout
 * WindowManager.LayoutParams
 */
public class MyToast {
    private WindowManager.LayoutParams mParams;
    private static View mView;
    private WindowManager mWindowManager;
    private static int mDuration;
    private int longTime = 0;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    hide();
                    removeCallbacksAndMessages(100);
                    break;
            }
        }
    };

    public MyToast(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        mParams.
        mParams.format = PixelFormat.TRANSLUCENT;
//        mParams.windowAnimations = com.android.internal.R.style.Animation_Toast;
        mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mParams.setTitle("Toast");
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;


    }

    public static MyToast makeText(Context context, int duration, String msg) {
        MyToast myToast = new MyToast(context);
        mView = View.inflate(context, R.layout.toast_view, null);
        TextView msgTv = (TextView) mView.findViewById(R.id.mytoast_msg);
        msgTv.setText(msg);

        mDuration = duration;

        return myToast;
    }

    public void show() {
        if (mView.getParent() != null) {
            mWindowManager.removeView(mView);
        }
        mWindowManager.addView(mView, mParams);

        //定时器  没有延迟，每一秒种执行一次run方法
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                longTime += 1000;
                LogUtils.e("time=" + longTime);
                if (mDuration <= longTime) {
                    mHandler.sendEmptyMessage(100);
                    cancel();
                }
            }
        }, 0, 1000);
    }

    public void hide() {
        if (mView.getParent() != null) {
            mWindowManager.removeViewImmediate(mView);
            longTime = 0;
        }
    }

    public static final int TIME_2000 = 2000;
    public static final int TIME_3500 = 3500;
    public static final int TIME_4500 = 4500;
    public static final int TIME_5000 = 5000;
    public static final int TIME_8000 = 8000;

    @IntDef({TIME_2000, TIME_3500, TIME_4500, TIME_5000, TIME_8000})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }
}
