package com.example.mytakeout.ui.fragment;

import android.app.assist.AssistStructure;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;

import com.example.mytakeout.R;
import com.example.mytakeout.anim.CustomAnimal;
import com.example.mytakeout.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zhuwujing on 2018/8/7.
 */

public class MoreFragment extends BaseFragment {
    @BindView(R.id.btn_anim)
    Button btnAnim;

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_more;
    }

    @Override
    protected void init() {

    }

    @OnClick({R.id.btn_anim})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_anim:
                CustomAnimal customAnimal = new CustomAnimal();
                customAnimal.setDuration(3000);
                btnAnim.setAnimation(customAnimal);
                break;
        }
    }

}
