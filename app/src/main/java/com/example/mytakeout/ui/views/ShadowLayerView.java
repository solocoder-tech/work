package com.example.mytakeout.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.mytakeout.R;


/*
   1.画四分线 FontMetris
   2.通过Piant.setShadowLayer()给文字和绘制的图形设置阴影，不能给图片设置
 */
public class ShadowLayerView extends View {

    private Paint paint;
    private float baseLineY;
    private float baseStartX;
    private Bitmap bitmap;

    public ShadowLayerView(Context context) {
        this(context, null);
    }

    public ShadowLayerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowLayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);//关闭硬件加速
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.LEFT);

        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_01);

        baseLineY = 500;
        baseStartX = 10;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画四分线
        Paint.FontMetrics mf = paint.getFontMetrics();
        float ascent = mf.ascent + baseLineY;
        float top = mf.top + baseLineY;
        float descent = mf.descent + baseLineY;
        float bottom = mf.bottom + baseLineY;
        //设置阴影
        paint.setShadowLayer(2, 10, 10, Color.GRAY);
        canvas.drawText("Hello World!", baseStartX, baseLineY, paint);
        paint.setColor(Color.RED);
        //取消阴影
        paint.clearShadowLayer();
        canvas.drawLine(baseStartX, baseLineY, getWidth(), baseLineY, paint);
        paint.setColor(Color.YELLOW);
        canvas.drawLine(baseStartX, ascent, getWidth(), ascent, paint);
        paint.setColor(Color.BLACK);
        canvas.drawLine(baseStartX, top, getWidth(), top, paint);
        paint.setColor(Color.BLUE);
        canvas.drawLine(baseStartX, descent, getWidth(), descent, paint);
        paint.setColor(Color.RED);
        canvas.drawLine(baseStartX, bottom, getWidth(), bottom, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setShadowLayer(4, 20, 30, Color.GRAY);
        canvas.drawCircle(800, 200, 50, paint);

        canvas.drawBitmap(bitmap, 200, 800, paint);

        paint.clearShadowLayer();
        paint.setStyle(Paint.Style.FILL);
        //外发光
        paint.setMaskFilter(new BlurMaskFilter(30, BlurMaskFilter.Blur.SOLID));
        canvas.drawCircle(400, 1200, 100, paint);

        //内发光
        paint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.INNER));
        canvas.drawCircle(700, 1200, 100, paint);

        //内外发光
        paint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL));
        canvas.drawCircle(400, 1600, 100, paint);

        //会将除发光部分外的其余区域透明
        paint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.OUTER));
        canvas.drawCircle(700, 1600, 100, paint);
    }
}
