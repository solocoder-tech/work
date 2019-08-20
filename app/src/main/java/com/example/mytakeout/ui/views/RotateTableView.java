package com.example.mytakeout.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.mytakeout.R;

/**
 * 创建时间：2019/4/29  16:18
 * 作者：5#
 * 描述：TODO
 */
public class RotateTableView extends RelativeLayout {

    private float mDownX;
    private float mDownY;
    private float mDeltX;
    private ImageView mImageIv;
    private RelativeLayout mParentRl;

    public RotateTableView(Context context) {
        super(context);
    }

    public RotateTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RotateTableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        View inflate = View.inflate(context, R.layout.view_rotate_table_layout, null);
        mParentRl = (RelativeLayout) inflate.findViewById(R.id.parent_rl);
        mImageIv = (ImageView) inflate.findViewById(R.id.image_iv);

        mParentRl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
                        mImageIv.setRotation(mDeltX / 10);

                        mDownX = moveX;
                        mDownY = moveY;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });
        this.addView(inflate);
    }
}
