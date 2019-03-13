package com.example.sweeper.net;

/**
 * 创建时间：2019/1/24  14:57
 * 作者：5#
 * 描述：异步请求结果的回调
 */
public interface ReqCallBack<T> {
    void onSuccess(T result);

    void onFail(String errorMsg);
}
