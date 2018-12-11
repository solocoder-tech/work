package com.example.mytakeout.net;

import com.example.mytakeout.base.AppConstant;
import com.example.mytakeout.modle.net.bean.WeatherBean;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;

/**
 * Created by zhuwujing on 2018/9/2.
 */

public interface Api {
    @GET(AppConstant.HttpCons.HOME)
    Call<HttpResponse> getHomeData();

    @Headers("Authorization:APPCODE a1f9546c6a524f06b2e0cc1c77e03d80")
    @GET(AppConstant.HttpCons.SPOT_TO_WEATHER)
    Call<WeatherBean> getSpot2Weather(@QueryMap Map<String, String> params);
}
