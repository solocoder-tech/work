package com.example.mytakeout.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mytakeout.R;
import com.example.mytakeout.utils.LogUtils;

import java.util.ArrayList;

/**
 * 创建时间：2019/8/21  21:52
 * 作者：5#
 * 描述：TODO
 */
public class VPAdapter extends PagerAdapter {
    private ArrayList<String> mDatas;
    private Context mContext;

    public VPAdapter(ArrayList<String> datas, Context context) {
        mDatas = datas;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    /**
     *
     * @param container 是ViewPager 对象
     * @param position position初始化的位置
     * @return
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        TextView textView = new TextView(mContext);
        textView.setText(mDatas.get(position));
        LogUtils.e("vp===instantiateItem==" + textView.hashCode() + "==position==" + position);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(20);//pixel
        textView.setBackgroundColor(mContext.getResources().getColor(R.color.skin_color));
        textView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
        container.addView(textView);
        return textView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        LogUtils.e("vp===destroyItem==" + object.hashCode()+ "==position==" + position);
        //这一句要删除，否则报错
//        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "这是" + position + "tab";
    }
}
