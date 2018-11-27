package com.example.mytakeout.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mytakeout.R;
import com.example.mytakeout.utils.UIUtils;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeAdapter extends RecyclerView.Adapter<MeAdapter.MyViewHolder> {
    private List<String> mDatas;
    private Context mContext;

    public MeAdapter(List<String> datas, Context context) {
        mDatas = datas;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_me_fragment, null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.mTextView.setText(mDatas.get(position));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.itemParemRl.getLayoutParams();
        if (position == 0) {
            layoutParams.setMargins(0, UIUtils.getStatusBarHeight(mContext), 0, 0);
//            holder.itemParemRl.setPadding(0,UIUtils.getStatusBarHeight(mContext),0,0);
        } else {
            layoutParams.setMargins(0, 0, 0, 0);
//            holder.itemParemRl.setPadding(0,UIUtils.getStatusBarHeight(mContext),0,0);
        }
        holder.itemParemRl.setLayoutParams(layoutParams);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRVItemClickLisenter != null) {
                    mRVItemClickLisenter.rvItemClick(mDatas.get(position), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_tv)
        TextView mTextView;
        @BindView(R.id.item_parent_rl)
        RelativeLayout itemParemRl;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private RVItemClickLisenter mRVItemClickLisenter;

    public void setRVItemClickLisenter(RVItemClickLisenter RVItemClickLisenter) {
        mRVItemClickLisenter = RVItemClickLisenter;
    }
}
