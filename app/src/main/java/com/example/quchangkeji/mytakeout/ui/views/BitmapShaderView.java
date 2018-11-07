package com.example.quchangkeji.mytakeout.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.quchangkeji.mytakeout.R;

public class BitmapShaderView extends View {

    private Paint paint;
    private Bitmap bitmap;
    private BitmapShader bitmapShader;

    public BitmapShaderView(Context context) {
        this(context, null);
    }

    public BitmapShaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BitmapShaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_01);
        //混合模式，先做Y方向的再结果上做X方向的
        bitmapShader = new BitmapShader(bitmap, Shader.TileMode.MIRROR, Shader.TileMode.REPEAT);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float left = getWidth() / 3;
        float top = getWidth() / 3;
        float right = getWidth() * 2 / 3;
        float bottom = getWidth() * 2 / 3;

//        canvas.drawRect(left, top, right, bottom, paint);
//        canvas.drawRect(0,0,getWidth(),getHeight(),paint);


        //先把图像缩放，不然回显示不全
        Matrix matrix = new Matrix();
        float scale = (float) getWidth() / bitmap.getWidth();
        matrix.setScale(scale,scale);
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        canvas.drawCircle(getWidth()/2, getHeight()/2, 100, paint);

    }
}
