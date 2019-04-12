package com.example.mytakeout.recyclerview.base;


import android.view.View;

/**
 * C
 */
public interface ItemViewDelegate<T>
{

    int getItemViewLayoutId();

    boolean isForViewType(T item, int position);

    void convert(ViewHolder holder, T t, int position);

}
