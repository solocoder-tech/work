package com.zhf.wifidemo.ui;

import com.zhf.wifidemo.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GifView extends View implements Runnable {
	gifOpenHelper gHelper;
	private boolean isStart = true;
	int delta;
	String title;

	Bitmap bmp;

	// construct - refer for java
	public GifView(Context context) {
		this(context, null);
		start();
	}

	// construct - refer for xml
	public GifView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 添加属性
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.gifView);
		int n = ta.getIndexCount();

		for (int i = 0; i < n; i++) {
			int attr = ta.getIndex(i);

			switch (attr) {
			case R.styleable.gifView_src:
				int id = ta.getResourceId(R.styleable.gifView_src, 0);
				setSrc(id);
				break;

			case R.styleable.gifView_delay:
				int idelta = ta.getInteger(R.styleable.gifView_delay, 1);
				setDelta(idelta);
				break;

			case R.styleable.gifView_stop:
				boolean sp = ta.getBoolean(R.styleable.gifView_stop, false);
				if (!sp) {
					stop();
				}
				break;
			}

		}

		ta.recycle();
		start();
	}

	/**
	 * 设置停止
	 * 
	 * @param stop
	 */
	public void stop() {
		isStart = false;
	}

	/**
	 * 设置启动
	 */
	public void start() {
		isStart = true;

		Thread updateTimer = new Thread(this);
		updateTimer.start();
	}

	/**
	 * 通过下票设置第几张图片显示
	 * 
	 * @param id
	 */
	public void setSrc(int id) {

		gHelper = new gifOpenHelper();
		gHelper.read(GifView.this.getResources().openRawResource(id));
		bmp = gHelper.getImage();// 得到第一张图片
	}

	public void setDelta(int is) {
		delta = is;
	}

	// to meaure its Width & Height
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		return gHelper.getWidth();
	}

	private int measureHeight(int measureSpec) {
		return gHelper.getHeigh();
	}

	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawBitmap(bmp, 0, 0, new Paint());
		bmp = gHelper.nextBitmap();

	}

	public void run() {
		// TODO Auto-generated method stub
		while (isStart) {
			try {
				this.postInvalidate();
				Thread.sleep(gHelper.nextDelay() / delta);
			} catch (Exception ex) {

			}
		}
	}

}
