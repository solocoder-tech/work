package com.example.sweeper.net;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.spec.ECField;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 创建时间：2019/1/24  14:19
 * 作者：5#
 * 描述：Okhttp 请求管理类
 * 1.单例（双重检查锁）
 * 2.同步  异步
 * 3.get post:post参数json ,带表单
 */
public class RequestManager {
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private static final String BASE_URL = "https://www.firefox.com.cn/mobile";//请求接口根地址
    public static final int TYPE_GET = 0;//get请求
    public static final int TYPE_POST_JSON = 1;//post请求参数为json
    public static final int TYPE_POST_FORM = 2;//post请求参数为表单

    private static RequestManager mRequestManager;
    private OkHttpClient mOkHttpClient;

    private RequestManager(Context context) {
        mHttpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        mOkHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS)//设置写入超时时间
                .addInterceptor(mHttpLoggingInterceptor)
                .build();
    }

    HttpLoggingInterceptor mHttpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String s) {
            Log.e("okhttp", "log:=== " + s);
        }
    });

    public static RequestManager getRequestManagerInstance(Context context) {
        if (mRequestManager == null) {
            synchronized (RequestManager.class) {
                if (mRequestManager == null) {
                    return new RequestManager(context);
                }
            }
        }
        return mRequestManager;
    }

    /**
     * 同步请求统一入口
     *
     * @param actionUrl
     * @param requestType
     * @param paramsMap
     */
    public void requestSyn(String actionUrl, int requestType, HashMap<String, String> paramsMap) {
        switch (requestType) {
            case TYPE_GET:
                requestGetBySyn(actionUrl, paramsMap);
                break;
            case TYPE_POST_JSON:
                requestPostBySyn(actionUrl, paramsMap);
                break;
            case TYPE_POST_FORM:
                requestPostBySynWithForm(actionUrl, paramsMap);
                break;
        }
    }

    private void requestPostBySynWithForm(String actionUrl, HashMap<String, String> paramsMap) {
        try {
            //创建一个FormBody.Build
            FormBody.Builder builder = new FormBody.Builder();
            //追加表单信息
            for (String key : paramsMap.keySet()) {
                builder.add(key, paramsMap.get(key));
            }
            //生成表单实体对象
            FormBody formBody = builder.build();
            //补全请求地址
            String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
            //创建一个请求
            Request request = addHeaders().url(requestUrl).post(formBody).build();
            //创建一个call
            Call call = mOkHttpClient.newCall(request);
            Response response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestPostBySyn(String actionUrl, HashMap<String, String> paramsMap) {
        try {
            //处理参数
            StringBuffer tempParams = new StringBuffer();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                pos++;
            }
            //补全请求地址
            String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
            //生成参数
            String params = tempParams.toString();
            //创建一个请求实体对象
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, params);
            //创建一个请求
            Request request = addHeaders().url(requestUrl).post(requestBody).build();
            //创建一个call
            Call call = mOkHttpClient.newCall(request);
            Response response = call.execute();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param actionUrl
     * @param paramsMap
     */
    private void requestGetBySyn(String actionUrl, HashMap<String, String> paramsMap) {
        StringBuffer tempParams = new StringBuffer();
        try {
            //处理参数  URLDecode和URLEncode 完成中文和西欧字符的转换
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            //补全请求路径
            String requestUrl = String.format("%s/%s?%s", BASE_URL, actionUrl, tempParams.toString());
            Log.e("okhttp", "requestUrl: " + requestUrl);
            //创建一个请求
            Request request = addHeaders().url(requestUrl).build();
            //创建一个call
            Call call = mOkHttpClient.newCall(request);
            //执行请求
            Response response = call.execute();
            String body = response.body().toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 统一异步操作的入口
     *
     * @param actionUrl
     * @param requestType
     * @param paramsMap
     * @param <T>
     * @return
     */
    public <T> Call requestAsyn(String actionUrl, int requestType, HashMap<String, String> paramsMap, ReqCallBack<T> reqCallBack) {
        Call call = null;
        switch (requestType) {
            case TYPE_GET:
                call = requestGetAsyn(actionUrl, paramsMap, reqCallBack);
                break;
            case TYPE_POST_JSON:
                call = requestPostByAsyn(actionUrl, paramsMap, reqCallBack);
                break;
            case TYPE_POST_FORM:
                call = requestPostByAsynWithForm(actionUrl, paramsMap, reqCallBack);
                break;
        }
        return call;
    }

    private <T> Call requestPostByAsynWithForm(String actionUrl, HashMap<String, String> paramsMap, ReqCallBack<T> reqCallBack) {
        return null;
    }

    private <T> Call requestPostByAsyn(String actionUrl, HashMap<String, String> paramsMap, ReqCallBack<T> reqCallBack) {
        return null;
    }

    private <T> Call requestGetAsyn(String actionUrl, HashMap<String, String> paramsMap, ReqCallBack<T> reqCallBack) {
        return null;
    }

    /**
     * 统一为请求添加头信息
     *
     * @return
     */
    private Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "keep-alive")
                .addHeader("platform", "2")
                .addHeader("phoneModel", Build.MODEL)
                .addHeader("systemVersion", Build.VERSION.RELEASE)
                .addHeader("appVersion", "3.2.0");
        return builder;
    }

}
