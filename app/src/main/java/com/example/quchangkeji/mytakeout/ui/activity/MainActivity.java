package com.example.quchangkeji.mytakeout.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.quchangkeji.mytakeout.R;
import com.example.quchangkeji.mytakeout.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;
    @BindView(R.id.item_container)
    LinearLayout mItemContainer;

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        setLisenters();
        //初始状态
        onClickLisenter.onClick(mItemContainer.getChildAt(0));
    }

    /**
     * 设置点击监听
     */
    private void setLisenters() {
        int childCount = mItemContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            mItemContainer.getChildAt(i).setOnClickListener(onClickLisenter);
        }
    }

    /**
     * 自己不可点击,其他同级别的可以点击
     * 自己的子孙View也不可点击
     */
    private View.OnClickListener onClickLisenter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int indexOfChild = mItemContainer.indexOfChild(v);
            for (int i = 0; i < mItemContainer.getChildCount(); i++) {
                if (indexOfChild == i) {
                    mItemContainer.getChildAt(i).setEnabled(false);
                    //自己的子孙View也不可点击
                    setEnable(mItemContainer.getChildAt(i), false);
                } else {
                    mItemContainer.getChildAt(i).setEnabled(true);
                    setEnable(mItemContainer.getChildAt(i), true);
                }
            }
        }
    };

    /**
     * 递归遍历子孙View
     *
     * @param childAt
     * @param b
     */
    private void setEnable(View childAt, boolean b) {
        childAt.setEnabled(b);
        if (childAt instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) childAt).getChildCount(); i++) {
                setEnable(((ViewGroup) childAt).getChildAt(i), b);
            }
        }
    }

}
