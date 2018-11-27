package com.example.mytakeout.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import retrofit2.Call;

/**
 * Created by zhuwujing on 2018/10/27.
 */

public class ChangeView extends View {

    private Paint mPaint;
    private float mDownX;
    private float mDownY;
    private float mMoveX;
    private float mMoveY;

    public ChangeView(Context context) {
        this(context, null);
    }

    public ChangeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChangeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path path = new Path();
        RectF rect = new RectF(200, 200, 400, 400);
//        new Region()
        path.addRect(rect, Path.Direction.CCW);
        if (rect.contains(mDownX, mDownY)) {
            mPaint.setColor(Color.RED);
        } else {
            mPaint.setColor(Color.BLUE);
        }
        canvas.drawPath(path, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveX = event.getX();
                mMoveY = event.getY();

                mDownX = mMoveX;
                mDownY = mMoveY;

                break;
            case MotionEvent.ACTION_UP:
                mDownY = mDownX = -1;
                break;
        }
        invalidate();
        return true;
    }
}
