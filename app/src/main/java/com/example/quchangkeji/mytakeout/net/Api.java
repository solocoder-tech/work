package com.example.quchangkeji.mytakeout.net;

import com.example.quchangkeji.mytakeout.base.AppConstant;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by zhuwujing on 2018/9/2.
 */

public interface Api {
    @GET(AppConstant.HttpCons.HOME)
    Call<HttpResponse> getHomeData();
}
