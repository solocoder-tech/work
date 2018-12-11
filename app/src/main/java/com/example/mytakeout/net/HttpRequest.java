package com.example.mytakeout.net;

import java.util.Map;

/**
 * Created by zhuwujing on 2018/9/2.
 */

public class HttpRequest {
    private String identify;
    private Map<String,String> params;

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
