package com.example.mytakeout.ui.fragment;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.mytakeout.R;
import com.example.mytakeout.base.BaseFragment;

import butterknife.BindView;

/**
 * 创建时间：2019/5/24  17:11
 * 作者：5#
 * 描述：TODO
 */
public class CommonFragment extends BaseFragment {
    @BindView(R.id.text)
    TextView text;

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_common;
    }

    @Override
    protected void init() {
        int postion = (int) getArguments().getInt("position");
        text.setText("这是第" + postion + "个");
    }
}
