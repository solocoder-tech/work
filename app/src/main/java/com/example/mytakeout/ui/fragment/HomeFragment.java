package com.example.mytakeout.ui.fragment;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mytakeout.R;
import com.example.mytakeout.adapter.HomeRvAdapter;
import com.example.mytakeout.base.BaseFragment;
import com.example.mytakeout.base.TitleConst;
import com.example.mytakeout.modle.net.bean.HomeBean;
import com.example.mytakeout.net.HttpResponse;
import com.example.mytakeout.presenter.fragment.HomePresenterImpl;
import com.example.mytakeout.ui.activity.ViewPagerTestActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by zhuwujing on 2018/8/7.
 * 首页
 */

public class HomeFragment extends BaseFragment implements HomeFragmentView, HomeRvAdapter.HomeTvAdapterLisenter {
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
    protected @LayoutRes
    int getResLayoutId() {
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
        initItems(datas);
        HomeRvAdapter adapter = new HomeRvAdapter(getActivity(), datas);
        adapter.setHomeTvAdapterLisenter(this);
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
     * 初始化条目
     *
     * @param datas
     */
    private void initItems(List<HomeBean> datas) {
        HomeBean homeBean = datas.get(0);
        homeBean.setTitle(TitleConst.TITLE_VIEWPAGER);
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
    }

    @Override
    public void onItemClick(HomeBean homeBean) {
        Intent intent = null;
        switch (homeBean.getTitle()) {
            case TitleConst.TITLE_VIEWPAGER:
                intent = new Intent(getActivity(), ViewPagerTestActivity.class);
                intent.putExtra("title",TitleConst.TITLE_VIEWPAGER);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
