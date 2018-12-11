package com.example.mytakeout.ui.activity;

import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.mytakeout.R;
import com.example.mytakeout.base.BaseActivity;
import com.example.mytakeout.ui.views.MyToast;
import com.example.mytakeout.ui.views.WButton;
import com.example.mytakeout.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * WindowManager
 * WindowManager.LayoutParams
 * <p>
 * <p>
 * Context.getSystemService(Context.WINDOW_SERVICE)获取WindowManager对象
 * 全限：
 * <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
 * 在MIUI上需要在设置中打开本应用的”显示悬浮窗”开关，并且重启应用，否则悬浮窗只能显示在本应用界面内，不能显示在手机桌面上
 */
public class ToastActivity extends BaseActivity {
    @BindView(R.id.toast_short)
    Button shortBtn;
    @BindView(R.id.toast_long)
    Button longBtn;
    @BindView(R.id.mytoast)
    Button myToastBtn;
    private WindowManager mWindowManager;

    @Override
    protected void initViews() {
        setCustomView(R.layout.activity_toast, true, "Toast");
        ButterKnife.bind(this);
    }

    @Override
    protected void initDatas() {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    protected void initEvents() {

    }

    @OnClick({R.id.toast_short, R.id.toast_long, R.id.mytoast})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toast_short:
                toast("short", Toast.LENGTH_SHORT);
                break;
            case R.id.toast_long:
//                toast("long", Toast.LENGTH_LONG);
                boolean apkDebugable = UIUtils.isApkDebugable(this);
                toast("apkDebugable = " + apkDebugable, Toast.LENGTH_LONG);
                break;
            case R.id.mytoast:
//                MyToast.makeText(this, MyToast.TIME_8000, "myToast").show();
                WButton button = new WButton(this);
                button.setText("WindowManager test");
                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                //当touch移除btn外，后面的控件获取触摸和焦
                //FLAG_NOT_FOCUSABLE没有焦点
                //FLAG_NOT_TOUCHABLE不能触摸
                params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
                params.y = UIUtils.getScreenHeight(this) - UIUtils.dp2px(this, 20);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                mWindowManager.addView(button, params);
                break;
        }
    }
}
