package com.example.mytakeout.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class PortDuffXfermode extends View {

    private Paint paint;
    private Bitmap srcBitmap;
    private Bitmap desBitmap;

    public PortDuffXfermode(Context context) {
        this(context, null);
    }

    public PortDuffXfermode(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PortDuffXfermode(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        srcBitmap = makeSrc();
        desBitmap = makeDest();
    }


    /**
     * 画一个圆形图片，作为目标图片
     *
     * @return
     */
    private Bitmap makeSrc() {
        Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        canvas.drawCircle(100, 100, 100, paint);
        return bitmap;
    }

    /**
     * 创建一个正方形，作为源图片
     *
     * @return
     */
    private Bitmap makeDest() {
        return null;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.YELLOW);
        canvas.drawBitmap(srcBitmap, 200, 200, paint);
    }
}
