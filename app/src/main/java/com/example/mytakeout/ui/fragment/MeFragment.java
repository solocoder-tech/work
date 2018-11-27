package com.example.mytakeout.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mytakeout.R;
import com.example.mytakeout.adapter.MeAdapter;
import com.example.mytakeout.adapter.RVItemClickLisenter;
import com.example.mytakeout.base.BaseFragment;
import com.example.mytakeout.ui.activity.WiFiActivty;
import com.example.mytakeout.utils.LogUtils;
import com.example.mytakeout.wifi.WifiUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by zhuwujing on 2018/8/7.
 */

public class MeFragment extends BaseFragment implements RVItemClickLisenter {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private List<String> mDatas;

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initDatas() {
        super.initDatas();
        mDatas = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mDatas.add("WIFI");
        MeAdapter adapter = new MeAdapter(mDatas, getActivity());
        adapter.setRVItemClickLisenter(this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void rvItemClick(Object o, int position) {
        switch (position) {
            case 0:
                startActivity(new Intent(getActivity(), WiFiActivty.class));
                break;
        }
    }
}
