package com.example.mytakeout.base;


import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mytakeout.utils.UIUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by zhuwujing on 2018/8/4.
 */

public abstract class BaseFragment extends Fragment {

    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(getResLayoutId(), null);
        mBind = ButterKnife.bind(this, inflate);
        init();
        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initDatas();
    }

    /**
     * 子类选择性实现
     */
    protected void initDatas() {

    }

    /**
     * 必须由子类实现
     *
     * @return
     */
    protected abstract @LayoutRes
    int getResLayoutId();

    protected abstract void init();

    public void toast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBind != null) {
            mBind.unbind();
        }
    }
}
