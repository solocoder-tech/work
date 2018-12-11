package com.example.mytakeout.ui.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.mytakeout.R;
import com.example.mytakeout.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zhuwujing on 2018/8/7.
 */

public class OrderFragment extends BaseFragment {
    @BindView(R.id.btn_test)
    Button btnTest;

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_order;
    }

    @Override
    protected void init() {
    }

    @OnClick({R.id.btn_test})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_test:
                //一个app中打开另一个app
                PackageManager packageManager = getActivity().getPackageManager();
                String targetPakage = "com.inmotion.sweeprobot.mbot";
//                if (checkPackInfo(targetPakage)) {
//                    Intent intent = packageManager.getLaunchIntentForPackage(targetPakage);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getActivity(), "没有安装" + targetPakage, Toast.LENGTH_SHORT).show();
//                }
                //一个app中打开另一个app的指定Activity
                Intent intent = new Intent(Intent.ACTION_MAIN);
                //前提：知道要跳转应用的包名、类名
                ComponentName componentName = new ComponentName("com.inmotion.sweeprobot.mbot", "com.inmotion.sweeprobot.mbot.Module.Homepage.HomepageActivity");
                intent.setComponent(componentName);
                startActivity(intent);
                break;
        }
    }

    /**
     * 检查包是否存在 * * @param packname * @return
     */
    private boolean checkPackInfo(String packname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }


}
