package com.example.mytakeout.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.mytakeout.utils.LogUtils;

/**
 * 创建时间：2019/4/12  16:21
 * 作者：5#
 * 描述：饼状图
 */
public class PieView extends View {

    private Paint mPaint;
    private int radio;
    private int width;
    private int height;
    private float[] valuses = {90, 90, 90, 80, 63, 44, 90};
    private int[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.GRAY, Color.BLACK};
    private float mStartAngle;
    private float lineLen;

    /**
     * 设置各个分区的数值
     *
     * @param valuses
     */
    public void setValuses(float[] valuses) {
        this.valuses = valuses;
        invalidate();
    }

    public PieView(Context context) {
        this(context, null);
    }

    public PieView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(12);
        mPaint.setColor(Color.RED);
//        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radio = 100;
        width = w;
        height = h;
        lineLen = radio / 4;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int cx = width / 2;
        int cy = height / 2;
//        canvas.drawCircle(cx, cy, radio, mPaint);

        if (valuses != null) {
            float total = getSum(valuses);
            RectF rectF = new RectF(0, 0, width, height);
            canvas.drawRect(rectF, mPaint);
            mPaint.setStyle(Paint.Style.FILL);
            mStartAngle = 0;
            float sweepAngle = 0;
            for (int i = 0; i <= valuses.length - 1; i++) {
                mPaint.setColor(colors[i % colors.length]);
                sweepAngle = valuses[i] / total * 360;
                canvas.drawArc(rectF, mStartAngle, sweepAngle, true, mPaint);//单位是角度
                //绘制短线
                double angle = Math.toRadians(sweepAngle / 2 + mStartAngle);
                float startPosX = 0;
                float startPosY = 0;
                float endPosX = 0;
                float endPosY = 0;
                if (angle >= 0 && angle < Math.PI / 2) {
                    startPosX = (float) (width / 2 + width / 2 * Math.cos(angle));
                    startPosY = (float) (width / 2 - width / 2 * Math.sin(angle));
                    endPosX = (float) (startPosX + lineLen * Math.cos(angle));
                    endPosY = (float) (startPosY - lineLen * Math.sin(angle));
                } else if (angle >= Math.PI / 2 && angle < Math.PI) {
                    startPosX = (float) (width / 2 - width / 2 * Math.cos(angle));
                    startPosY = (float) (width / 2 - width / 2 * Math.sin(angle));
                    endPosX = (float) (startPosX - lineLen * Math.cos(angle));
                    endPosY = (float) (startPosY - lineLen * Math.sin(angle));
                } else if (angle >= Math.PI && angle < 3 * Math.PI / 2) {
                    startPosX = (float) (width / 2 - width / 2 * Math.cos(angle));
                    startPosY = (float) (width / 2 + width / 2 * Math.sin(angle));
                    endPosX = (float) (startPosX - lineLen * Math.cos(angle));
                    endPosY = (float) (startPosY + lineLen * Math.sin(angle));
                } else {
                    startPosX = (float) (width / 2 + width / 2 * Math.cos(angle));
                    startPosY = (float) (width / 2 + width / 2 * Math.sin(angle));
                    endPosX = (float) (startPosX + lineLen * Math.cos(angle));
                    endPosY = (float) (startPosY + lineLen * Math.sin(angle));
                }
                canvas.drawLine(startPosX, startPosY, endPosX, endPosY, mPaint);

                mStartAngle += sweepAngle;
            }
        }

    }

    private float getSum(float[] valuses) {
        int sum = 0;
        for (float valus : valuses) {
            sum += valus;
        }
        return sum;
    }
}
