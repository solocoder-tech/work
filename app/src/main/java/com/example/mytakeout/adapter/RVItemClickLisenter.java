package com.example.mytakeout.adapter;

/**
 * RecyclerView 条目点击事件的公共接口（用于接口回调）
 * @param <T>
 */
public interface RVItemClickLisenter<T> {
    void rvItemClick(T t,int position);
}
