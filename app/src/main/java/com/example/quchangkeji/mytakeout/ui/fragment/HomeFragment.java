package com.example.quchangkeji.mytakeout.ui.fragment;

import android.animation.ArgbEvaluator;
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
import com.example.quchangkeji.mytakeout.base.AppConstant;
import com.example.quchangkeji.mytakeout.base.BaseFragment;
import com.example.quchangkeji.mytakeout.modle.net.bean.HomeBean;
import com.example.quchangkeji.mytakeout.net.Api;
import com.example.quchangkeji.mytakeout.net.HttpResponse;
import com.example.quchangkeji.mytakeout.presenter.fragment.HomePresenterImpl;
import com.example.quchangkeji.mytakeout.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zhuwujing on 2018/8/7.
 */

public class HomeFragment extends BaseFragment implements HomeFragmentView{
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

    int sumY = 0;//RecyclerView滑动的y的距离

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void init() {
        mRvHome.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<HomeBean> datas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            HomeBean homeBean = new HomeBean();
            homeBean.setTitle("这是第" + i + "个条目");
            datas.add(homeBean);
        }
        HomeRvAdapter adapter = new HomeRvAdapter(getActivity(), datas);
        mRvHome.setAdapter(adapter);

        mSrlHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSrlHome.setRefreshing(false);
            }
        });
        mRvHome.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                sumY += dy;
//                LogUtils.sysout(dx + "==-----==" + dy + "---=" + sumY);
                //0--250
                ArgbEvaluator evaluator = new ArgbEvaluator();
                float fraction = 0f;
                if (sumY >= 0 && sumY <= 250) {
                    fraction = sumY / 250.0f;
                } else if (sumY > 250) {
                    fraction = 1f;
                } else {
                    fraction = 0f;
                }
                int targetColor = (int) evaluator.evaluate(fraction, 0x553190E8, 0xFF3190E8);
                mLlTitleContainer.setBackgroundColor(targetColor);
            }

            /**
             * 滚动状态发生改变时调用
             * @param recyclerView
             * @param newState
             */
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
//                        LogUtils.sysout("空闲状态");
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
//                        LogUtils.sysout("被拖拽状态");
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
//                        LogUtils.sysout("自动滚动状态");
                        break;
                }
            }
        });

    }

    /**
     * 加载数据
     */
    @Override
    protected void initDatas() {
        super.initDatas();
        HomePresenterImpl homePresenter = new HomePresenterImpl(this);
        homePresenter.getHomeData();
    }

    @Override
    public void OnSuccess(HttpResponse body) {
        LogUtils.sysout("----" + body.getData());
    }
}
