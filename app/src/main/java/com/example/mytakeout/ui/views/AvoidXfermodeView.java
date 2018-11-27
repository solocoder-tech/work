package com.example.quchangkeji.mytakeout.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.mytakeout.R;


/*
   AvoidXfermode(高版本已经没有这个类了)
  1.替换图片上的目标颜色
  2.两张图片的融合
 */
public class AvoidXfermodeView extends View {

    private Paint paint;
    private Bitmap bitmap;

    public AvoidXfermodeView(Context context) {
        super(context);
    }

    public AvoidXfermodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public AvoidXfermodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        paint = new Paint();
        paint.setAntiAlias(true);
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_01);

//        avoidXfermode = new AvoidXfermode(0XFFCCD1D4, 0, AvoidXfermode.Mode.TARGET);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.YELLOW);
//        paint.setXfermode());
        canvas.drawBitmap(bitmap, 100, 200, paint);
    }
}
