package com.example.mytakeout.presenter.fragment;

import com.example.mytakeout.net.HttpResponse;
import com.example.mytakeout.presenter.BasePresenterlmpl;
import com.example.mytakeout.ui.fragment.HomeFragmentView;
import com.example.mytakeout.utils.LogUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by zhuwujing on 2018/9/2.
 */

public class HomePresenterImpl extends BasePresenterlmpl implements HomeFragmentPresenter {

    HomeFragmentView mHomeFragmentView;

    public HomePresenterImpl(HomeFragmentView homeFragmentView) {
        mHomeFragmentView = homeFragmentView;
    }

    @Override
    public void getHomeData() {
        Call<HttpResponse> homeData = mApi.getHomeData();
        homeData.enqueue(new Callback<HttpResponse>() {
            @Override
            public void onResponse(Call<HttpResponse> call, Response<HttpResponse> response) {
                int code = response.code();
                if (code == 200) {
                    HttpResponse body = response.body();
                    mHomeFragmentView.OnSuccess(body);
                    LogUtils.sysout("body----" + body);
                } else {

                }
            }

            @Override
            public void onFailure(Call<HttpResponse> call, Throwable t) {

            }
        });
    }
}
