package com.example.quchangkeji.mytakeout.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.quchangkeji.mytakeout.R;
import com.example.quchangkeji.mytakeout.base.BaseActivity;
import com.example.quchangkeji.mytakeout.base.SimpleFragment;
import com.example.quchangkeji.mytakeout.ui.fragment.HomeFragment;
import com.example.quchangkeji.mytakeout.ui.fragment.MeFragment;
import com.example.quchangkeji.mytakeout.ui.fragment.MoreFragment;
import com.example.quchangkeji.mytakeout.ui.fragment.OrderFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;
    @BindView(R.id.item_container)
    LinearLayout mItemContainer;

    private List<Fragment> mFragments;

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        mFragments = new ArrayList<>();
        mFragments.add(new HomeFragment());//new 的时候没有走fragment的生命周期,在replace的时候走
        mFragments.add(new OrderFragment());
        mFragments.add(new MeFragment());
        mFragments.add(new MoreFragment());
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
            changeUI(indexOfChild);
            setFragments(indexOfChild);
        }
    };

    /**
     * 根据点击Item,更改UI
     *
     * @param indexOfChild
     */
    private void changeUI(int indexOfChild) {
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

    private void setFragments(int indexOfChild) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        beginTransaction.replace(R.id.fragment_container, mFragments.get(indexOfChild));
        beginTransaction.commit();
    }

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
