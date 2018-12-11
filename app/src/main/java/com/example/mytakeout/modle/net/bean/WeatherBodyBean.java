package com.example.mytakeout.modle.net.bean;

public class WeatherBodyBean {
    private String time;
    private String ret_code;
    private Object cityInfo;
    private Object f1;
    private Object f2;
    private Object f3;
    private NowWeatherBean now;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRet_code() {
        return ret_code;
    }

    public void setRet_code(String ret_code) {
        this.ret_code = ret_code;
    }

    public Object getCityInfo() {
        return cityInfo;
    }

    public void setCityInfo(Object cityInfo) {
        this.cityInfo = cityInfo;
    }

    public Object getF1() {
        return f1;
    }

    public void setF1(Object f1) {
        this.f1 = f1;
    }

    public Object getF2() {
        return f2;
    }

    public void setF2(Object f2) {
        this.f2 = f2;
    }

    public Object getF3() {
        return f3;
    }

    public void setF3(Object f3) {
        this.f3 = f3;
    }

    public NowWeatherBean getNow() {
        return now;
    }

    public void setNow(NowWeatherBean now) {
        this.now = now;
    }
}
