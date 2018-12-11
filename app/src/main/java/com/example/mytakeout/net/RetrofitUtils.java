package com.example.mytakeout.net;

import com.example.mytakeout.base.AppConstant;
import com.example.mytakeout.utils.LogUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zhuwujing on 2018/9/2.
 */

public class RetrofitUtils {
    private static RetrofitUtils mRetrofitUtils = new RetrofitUtils();
    private final Api mApi;
    private final long timeout = 5;

    private RetrofitUtils() {
        //日志过滤
        HttpLoggingInterceptor mHttpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String msg) {
                LogUtils.e("RetrofitUtils = " + msg);
            }
        });
        mHttpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //1.建立retrofit对象
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(mHttpLoggingInterceptor);
        builder.connectTimeout(timeout, TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                //设置Gson解析器
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(AppConstant.HttpCons.WEATHER_URL)
                .build();

        //2.获取接口
        mApi = retrofit.create(Api.class);
    }


    public static RetrofitUtils getRetrofitUtils() {
        return mRetrofitUtils;
    }

    public Api getApi() {
        return mApi;
    }


}
