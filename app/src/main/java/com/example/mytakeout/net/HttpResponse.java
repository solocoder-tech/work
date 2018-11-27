package com.example.mytakeout.net;

/**
 * Created by zhuwujing on 2018/9/2.
 * 接受服务器的数据类
 */

public class HttpResponse {
    private String code;
    private Object data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "code='" + code + '\'' +
                ", data=" + data +
                '}';
    }
}
