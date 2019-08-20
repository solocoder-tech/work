package com.example.mytakeout.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.example.mytakeout.R;

/**
 * 创建时间：2019/4/29  14:47
 * 作者：5#
 * 描述：TODO
 */
public class RotateImageView extends ImageView {

    private Bitmap mBitmap;
    private Paint mPaint;
    private int with;
    private int height;
    private float mDownX;
    private float mDownY;
    private float mDeltX;

    public RotateImageView(Context context) {
        super(context);
    }

    public RotateImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inti();
    }

    public RotateImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void inti() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.xaunchuang);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        with = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect(200, 500, 600, 800);//4*3
        // 定义矩阵对象
        Matrix matrix = new Matrix();
        // 向左旋转45度，参数为正则向右旋转
        matrix.postRotate(mDeltX / 100);
        //bmp.getWidth(), 500分别表示重绘后的位图宽高
        canvas.drawBitmap(mBitmap, matrix, mPaint);

        canvas.save();
        //设置裁剪区域，设置完成之后Canvas会在裁剪区域绘制
        Rect srcRect = new Rect(0, 0, with, mBitmap.getHeight() / 4);
        canvas.clipRect(srcRect);
        //Canvas 擦除颜色
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();

                mDeltX += moveX - mDownX;
                float deltY = moveY - mDownY;


                mDownX = moveX;
                mDownY = moveY;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }
}
