package com.example.quchangkeji.mytakeout.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.quchangkeji.mytakeout.R;
import com.example.quchangkeji.mytakeout.adapter.HomeRvAdapter;
import com.example.quchangkeji.mytakeout.base.BaseFragment;
import com.example.quchangkeji.mytakeout.modle.net.bean.HomeBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by zhuwujing on 2018/8/7.
 */

public class HomeFragment extends BaseFragment {
    @BindView(R.id.rv_home)
    RecyclerView mRvHome;
    @BindView(R.id.srl_home)
    SwipeRefreshLayout mSrlHome;
    @BindView(R.id.home_tv_address)
    TextView mHomeTvAddress;
    @BindView(R.id.ll_title_search)
    LinearLayout mLlTitleSearch;
    @BindView(R.id.ll_title_container)
    LinearLayout mLlTitleContainer;

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void init() {
        mRvHome.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<HomeBean> datas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            datas.add(new HomeBean());
        }
        mRvHome.setAdapter(new HomeRvAdapter(getActivity(), datas));

        mSrlHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSrlHome.setRefreshing(false);
            }
        });
    }
}
