package com.example.mytakeout.ui.activity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.mytakeout.R;
import com.example.mytakeout.base.BaseActivity;
import com.example.mytakeout.ui.fragment.HomeFragment;
import com.example.mytakeout.ui.fragment.MeFragment;
import com.example.mytakeout.ui.fragment.MoreFragment;
import com.example.mytakeout.ui.fragment.OrderFragment;
import com.example.mytakeout.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends BaseActivity {

    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;
    @BindView(R.id.item_container)
    LinearLayout mItemContainer;

    private List<Fragment> mFragments;
    private int countExit = 0;


    @Override
    protected void initViews() {
        setCustomView(R.layout.activity_main, false);
        ButterKnife.bind(this);
        LogUtils.e("cpu====" + Build.CPU_ABI);

    }

    @Override
    protected void initDatas() {
        mFragments = new ArrayList<>();
        mFragments.add(new HomeFragment());//new 的时候没有走fragment的生命周期,在replace的时候走
        mFragments.add(new OrderFragment());
        mFragments.add(new MeFragment());
        mFragments.add(new MoreFragment());
        setLisenters();
        //初始状态
        onClickLisenter.onClick(mItemContainer.getChildAt(0));

        //申请位置权限
        MainActivityPermissionsDispatcher.requestLocationWithCheck(this);
    }

    @Override
    protected void initEvents() {

    }

    // 单个权限
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void requestLocation() {
        toast("申请权限成功");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            countExit++;
            if (countExit == 2) {
                return super.onKeyDown(keyCode, event);
            }
            toast("再按一次退出");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    countExit = 0;
                }
            }, 2000);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
