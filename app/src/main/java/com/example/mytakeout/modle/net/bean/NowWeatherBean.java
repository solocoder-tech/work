package com.example.mytakeout.modle.net.bean;

/**
 * "wind_direction":"北风",
 * "aqi":"68",
 * "weather_pic":"http://app1.showapi.com/weather/icon/day/02.png",
 * "wind_power":"1级",
 * "temperature_time":"14:30",
 * "weather_code":"02",
 * "temperature":"20",
 * "sd":"80%",
 * "aqiDetail":{
 * "quality":"良好",
 * "aqi":"68",
 * "pm10":"86",
 * "area":"漳州",
 * "co":"0.733",
 * "o3":"66",
 * "so2":"11",
 * "no2":"33",
 * "primary_pollutant":"颗粒物(PM10)",
 * "o3_8h":"28",
 * "num":"131",
 * "pm2_5":"46"
 * },
 * "weather":"阴"
 */
public class NowWeatherBean {
    private String wind_direction;

    public String getWind_direction() {
        return wind_direction;
    }

    public void setWind_direction(String wind_direction) {
        this.wind_direction = wind_direction;
    }
}
