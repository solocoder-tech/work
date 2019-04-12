package com.example.mytakeout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 创建时间：2019/3/28  13:01
 * 作者：5#
 * 描述：TODO
 */
public class TouchImage extends ImageView {

    public TouchImage(Context context) {
        super(context);
    }

    public TouchImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:

        }
        return super.onTouchEvent(event);
    }
}
