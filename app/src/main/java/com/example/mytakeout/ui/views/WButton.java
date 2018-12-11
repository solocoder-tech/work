package com.example.mytakeout.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import com.example.mytakeout.utils.LogUtils;

public class WButton extends Button {
    public WButton(Context context) {
        super(context);
    }

    public WButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtils.e("WButton == onTouchEvent");
        return super.onTouchEvent(event);
    }
}
