package com.example.mytakeout.ui.activity;

import android.os.Bundle;

import com.example.mytakeout.R;
import com.example.mytakeout.base.BaseActivity;
import com.example.mytakeout.ui.views.PieView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 创建时间：2019/4/9  21:56
 * 作者：5#
 * 描述：TODO
 */
public class ViewsActivity extends BaseActivity {
    @BindView(R.id.pie_view)
    PieView pieView;

    @Override
    protected void initViews() {
        setCustomView(R.layout.activity_views, false, "Toast");
        ButterKnife.bind(this);

    }

    @Override
    protected void initDatas() {
        float[] values = {12,20,30,90,56,22,123};
        pieView.setValuses(values);
    }

    @Override
    protected void initEvents() {

    }

}
