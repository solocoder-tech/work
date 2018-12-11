package com.example.mytakeout.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mytakeout.R;
import com.example.mytakeout.base.BaseActivity;
import com.example.mytakeout.modle.net.bean.NowWeatherBean;
import com.example.mytakeout.modle.net.bean.WeatherBean;
import com.example.mytakeout.modle.net.bean.WeatherBodyBean;
import com.example.mytakeout.net.ApiManager;
import com.example.mytakeout.net.HttpRequest;
import com.example.mytakeout.net.HttpResponse;
import com.example.mytakeout.net.RetrofitUtils;
import com.example.mytakeout.ui.views.CommonDialogManager;
import com.example.mytakeout.ui.views.CustomDialog;
import com.example.mytakeout.ui.views.MiNiProgressBar;
import com.example.mytakeout.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitActivity extends BaseActivity {
    @BindView(R.id.retrofit_get)
    Button getBtn;
    private CustomDialog dialogupapp;
    private MiNiProgressBar pg;

    @Override
    protected void initViews() {
        setCustomView(R.layout.activity_retrofit, true, "Retrofit");
        ButterKnife.bind(this);
    }

    @Override
    protected void initDatas() {

    }

    @Override
    protected void initEvents() {
    }

    @OnClick(R.id.retrofit_get)
    public void onClicked(View view) {
        switch (view.getId()) {
            case R.id.retrofit_get:
                HttpRequest httpRequest = ApiManager.getSpot2Weather("华山");
                Call<WeatherBean> spot2Weather = mApi.getSpot2Weather(httpRequest.getParams());
                spot2Weather.enqueue(new Callback<WeatherBean>() {
                    @Override
                    public void onResponse(Call<WeatherBean> call, Response<WeatherBean> response) {
                        WeatherBean weatherBean = response.body();
                        if (weatherBean != null && weatherBean.getShowapi_res_code() == 0) {
                            WeatherBodyBean showapi_res_body = weatherBean.getShowapi_res_body();
                            NowWeatherBean now = showapi_res_body.getNow();
                            LogUtils.e("wind_direction==" + now.getWind_direction());
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherBean> call, Throwable throwable) {

                    }
                });
                break;
        }
    }

    private void showDownPrograss() {
        dialogupapp = CommonDialogManager.getInstance().createDialog(this, R.layout.down_file_dialog);
        dialogupapp.setCdHelper(new CustomDialog.CustomDialogHelper() {
            @Override
            public void showDialog(CustomDialog dialog) {
                ((TextView) dialog.findViewById(R.id.tv_downfile_dialog_desc)).setText("正在为您下载最新版应用，请稍候..");
                pg = (MiNiProgressBar) dialog.findViewById(R.id.mpg_donwnload_show);
            }
        });
        dialogupapp.setCancelable(false);
        dialogupapp.show();
    }
}
