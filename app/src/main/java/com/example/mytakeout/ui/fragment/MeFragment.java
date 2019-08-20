package com.example.mytakeout.ui.fragment;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.mytakeout.R;
import com.example.mytakeout.adapter.MeAdapter;
import com.example.mytakeout.adapter.RVItemClickLisenter;
import com.example.mytakeout.anim.CustomAnimal;
import com.example.mytakeout.base.BaseFragment;
//import com.example.mytakeout.ui.activity.LeakCanaryActivity;
import com.example.mytakeout.ui.activity.RetrofitActivity;
import com.example.mytakeout.ui.activity.ToastActivity;
import com.example.mytakeout.ui.activity.ViewPagerTestActivity;
import com.example.mytakeout.ui.activity.ViewsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

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
        mDatas.add("Retrofit");
        mDatas.add("Toast");
        mDatas.add("LeakCanary");
        mDatas.add("自定义控件");
        mDatas.add("viewpage + fragment");
        MeAdapter adapter = new MeAdapter(mDatas, getActivity());
        adapter.setRVItemClickLisenter(this);
        //添加默认的分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void rvItemClick(Object o, int position) {
        switch (position) {
            case 0:
//                startActivity(new Intent(getActivity(), WiFiActivty.class));
                break;
            case 1:
                startActivity(new Intent(getActivity(), RetrofitActivity.class));
                break;
            case 2:
                startActivity(new Intent(getActivity(),ToastActivity.class));
                break;
            case 3:
//                startActivity(new Intent(getActivity(),LeakCanaryActivity.class));
                break;
            case 4:
                startActivity(new Intent(getActivity(), ViewsActivity.class));
                break;
            case 5:
                startActivity(new Intent(getActivity(), ViewPagerTestActivity.class));
                break;
        }
    }
}
