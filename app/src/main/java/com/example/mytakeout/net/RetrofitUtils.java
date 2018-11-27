package com.example.mytakeout.net;

import com.example.mytakeout.base.AppConstant;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zhuwujing on 2018/9/2.
 */

public class RetrofitUtils {
    private static RetrofitUtils mRetrofitUtils = new RetrofitUtils();

    private RetrofitUtils() {
        Retrofit retrofit = new Retrofit.Builder()
                //设置Gson解析器
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(AppConstant.HttpCons.BASE_URL)
                .build();
    }

    public static RetrofitUtils getRetrofitUtils() {
        return mRetrofitUtils;
    }

}
