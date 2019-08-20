package com.example.mytakeout.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.mytakeout.R;

/**
 * 创建时间：2019/4/18  15:31
 * 作者：5#
 * 描述：TODO
 */
public class MyImageView extends View {

    private Bitmap mBitmap;
    private Paint mPaint;

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        mPaint = new Paint();
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Matrix matrix = new Matrix();
        matrix.postScale(2,2);
        matrix.postTranslate(100,200);
        canvas.drawBitmap(mBitmap,matrix,mPaint);
    }
}
