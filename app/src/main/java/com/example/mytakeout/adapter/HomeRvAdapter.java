package com.example.mytakeout.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mytakeout.R;
import com.example.mytakeout.modle.net.bean.HomeBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhuwujing on 2018/8/22.
 */

public class HomeRvAdapter extends RecyclerView.Adapter<HomeRvAdapter.HomeHolder> {
    private Context mContext;
    private List<HomeBean> datas;

    public HomeRvAdapter(Context context, List<HomeBean> datas) {
        mContext = context;
        this.datas = datas;
    }

    @Override
    public HomeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeHolder(LayoutInflater.from(mContext).inflate(R.layout.fragment_home_item, parent, false));
    }

    @Override
    public void onBindViewHolder(HomeHolder holder, int position) {
        final HomeBean homeBean = datas.get(position);
        if (position % 2 == 0) {
            holder.itemRl.setBackgroundColor(mContext.getResources().getColor(R.color.color_28600a));
//            holder.mTextTv.setTextColor(mContext.getResources().getColor(R.color.black));
        } else {
            holder.itemRl.setBackgroundColor(mContext.getResources().getColor(R.color.skin_color));
        }
        if (homeBean != null) {
            holder.mTextTv.setText(homeBean.getTitle());
        }
        holder.itemRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHomeTvAdapterLisenter != null) {
                    mHomeTvAdapterLisenter.onItemClick(homeBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    public static class HomeHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_tv)
        TextView mTextTv;
        @BindView(R.id.item_rl)
        RelativeLayout itemRl;

        public HomeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface HomeTvAdapterLisenter {
        void onItemClick(HomeBean homeBean);
    }

    private HomeTvAdapterLisenter mHomeTvAdapterLisenter;

    public void setHomeTvAdapterLisenter(HomeTvAdapterLisenter homeTvAdapterLisenter) {
        mHomeTvAdapterLisenter = homeTvAdapterLisenter;
    }
}
