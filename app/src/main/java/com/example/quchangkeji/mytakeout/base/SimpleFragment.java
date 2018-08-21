package com.example.quchangkeji.mytakeout.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.quchangkeji.mytakeout.utils.LogUtils;

/**
 * Created by zhuwujing on 2018/8/7.
 * 开发时用到的简单Fragment
 */

public class SimpleFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.sysout("onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.sysout("onCreateView");
        TextView textView = new TextView(container.getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(layoutParams);
        textView.setText(getArguments().getString("title"));
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.sysout("onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.sysout("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.sysout("onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.sysout("onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.sysout("onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.sysout("onDestroy");
    }
}
