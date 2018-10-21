package com.example.quchangkeji.mytakeout.presenter;

import com.example.quchangkeji.mytakeout.base.AppConstant;
import com.example.quchangkeji.mytakeout.net.Api;
import com.example.quchangkeji.mytakeout.net.HttpResponse;
import com.example.quchangkeji.mytakeout.utils.LogUtils;

import java.io.IOException;
import java.util.MissingResourceException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zhuwujing on 2018/9/2.
 */

public class BasePresenterlmpl {

    private Retrofit mRetrofit;
    protected Api mApi;

    protected BasePresenterlmpl() {
        if (mRetrofit == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(new LogInterceptor());

            builder.addInterceptor(new LogInterceptor());
            mRetrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(AppConstant.HttpCons.BASE_URL)
                    .client(builder.build())
                    .build();
        }
        mApi = mRetrofit.create(Api.class);
    }


    /**
     * 定义一个拦截器,主要用于输出日志
     */
    private class LogInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            okhttp3.Response response = chain.proceed(request);
            HttpUrl url = request.url();
            LogUtils.sysout("url--" + url.toString());
            return chain.proceed(request);
        }
    }
}
