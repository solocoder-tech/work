package com.example.quchangkeji.mytakeout.adapter;

import android.content.Context;
import android.os.Binder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quchangkeji.mytakeout.R;
import com.example.quchangkeji.mytakeout.modle.net.bean.HomeBean;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by zhuwujing on 2018/8/22.
 */

public class HomeRvAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<HomeBean> datas;

    public HomeRvAdapter(Context context, List<HomeBean> datas) {
        mContext = context;
        this.datas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeHolder(LayoutInflater.from(mContext).inflate(R.layout.fragment_home_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    public static class HomeHolder extends RecyclerView.ViewHolder {
        public HomeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
