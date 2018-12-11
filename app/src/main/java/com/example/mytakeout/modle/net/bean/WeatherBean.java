package com.example.mytakeout.modle.net.bean;

/**
 * {"showapi_res_error":"","showapi_res_id":"8534019a665f4634a60ce629249ad2c2",
 * "showapi_res_code":0,"showapi_res_body":{"remark":"找不到此地名!","ret_code":-1}}
 */
public class WeatherBean {
    private String showapi_res_error;
    private String showapi_res_id;
    private int showapi_res_code;
    private WeatherBodyBean showapi_res_body;

    public String getShowapi_res_error() {
        return showapi_res_error;
    }

    public void setShowapi_res_error(String showapi_res_error) {
        this.showapi_res_error = showapi_res_error;
    }

    public String getShowapi_res_id() {
        return showapi_res_id;
    }

    public void setShowapi_res_id(String showapi_res_id) {
        this.showapi_res_id = showapi_res_id;
    }

    public int getShowapi_res_code() {
        return showapi_res_code;
    }

    public void setShowapi_res_code(int showapi_res_code) {
        this.showapi_res_code = showapi_res_code;
    }

    public WeatherBodyBean getShowapi_res_body() {
        return showapi_res_body;
    }

    public void setShowapi_res_body(WeatherBodyBean showapi_res_body) {
        this.showapi_res_body = showapi_res_body;
    }

    @Override
    public String toString() {
        return "WeatherBean{" +
                "showapi_res_error='" + showapi_res_error + '\'' +
                ", showapi_res_id='" + showapi_res_id + '\'' +
                ", showapi_res_code=" + showapi_res_code +
                ", showapi_res_body=" + showapi_res_body +
                '}';
    }
}
