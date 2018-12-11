package com.example.mytakeout.net;

import com.example.mytakeout.modle.net.bean.WeatherBean;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class ApiManager {
    public static HttpRequest getSpot2Weather(String area) {
        HttpRequest httpRequest = new HttpRequest();
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("area", "华山");
        querys.put("need3HourForcast", "0");
        querys.put("needAlarm", "0");
        querys.put("needHourData", "0");
        querys.put("needIndex", "0");
        querys.put("needMoreDay", "0");
        httpRequest.setParams(querys);
        return httpRequest;
    }
}
