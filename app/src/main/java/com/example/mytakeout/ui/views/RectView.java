package com.example.mytakeout.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.mytakeout.R;

/**
 * 一个矩阵View,重写onMeasure() ,onDraw()
 * 自定义属性
 * 275
 */
public class RectView extends View {

    private int mDefaultSize;

    public RectView(Context context) {
        this(context, null);
    }

    public RectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RectView);
        mDefaultSize = typedArray.getDimensionPixelSize(R.styleable.RectView_default_size, 100);
        typedArray.recycle();
    }

    public RectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int withSize = getMySize(mDefaultSize, widthMeasureSpec);
        int heightSize = getMySize(mDefaultSize, heightMeasureSpec);

        //小的为准
        if (withSize > heightSize) {
            withSize = heightSize;
        } else {
            heightSize = withSize;
        }
        setMeasuredDimension(withSize, heightSize);
    }

    /**
     * 计算出自定义的尺寸
     *
     * @param defaultSize
     * @param measureSpec
     * @return
     */
    private int getMySize(int defaultSize, int measureSpec) {
        int mySize = defaultSize; //真实值
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec); //参考值
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:  //父控件没有对当前控件限制,控件的大小可以随便取值
                mySize = defaultSize;
                break;
            case MeasureSpec.AT_MOST: //当前尺寸就是控件的最大尺寸
                mySize = size;
                break;
            case MeasureSpec.EXACTLY: //当前尺寸就是控件应该的尺寸
                mySize = size;
                break;
        }
        return mySize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = getMeasuredWidth() / 2;
        float cy = getMeasuredHeight() / 2;
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        canvas.drawCircle(cx, cy, cx, paint);
    }
}
