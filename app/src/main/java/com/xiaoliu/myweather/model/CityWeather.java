package com.xiaoliu.myweather.model;

import java.util.List;

/**
 * Created by Administrator on 2016/3/1.
 */
public class CityWeather {
    //当前天气
    private String cityName;
    private String releaseTime;
    private String weatherInfo;
    private int fa;
    private int fb;
    private String temperatureNow;
    private String temperatureRange;
    private String humidity;
    private String dressingIndex;
    private String wind;
    private String uvIndex;
    //空气质量
    private String aqiIndex;
    private String quality;
    //未来天气
    private List<FutureWeather> futureWeathers;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getWeatherInfo() {
        return weatherInfo;
    }

    public void setWeatherInfo(String weatherInfo) {
        this.weatherInfo = weatherInfo;
    }

    public int getFa() {
        return fa;
    }

    public void setFa(int fa) {
        this.fa = fa;
    }

    public int getFb() {
        return fb;
    }

    public void setFb(int fb) {
        this.fb = fb;
    }

    public String getTemperatureNow() {
        return temperatureNow;
    }

    public void setTemperatureNow(String temperatureNow) {
        this.temperatureNow = temperatureNow;
    }

    public String getTemperatureRange() {
        return temperatureRange;
    }

    public void setTemperatureRange(String temperatureRange) {
        this.temperatureRange = temperatureRange;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getDressingIndex() {
        return dressingIndex;
    }

    public void setDressingIndex(String dressingIndex) {
        this.dressingIndex = dressingIndex;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getUvIndex() {
        return uvIndex;
    }

    public void setUvIndex(String uvIndex) {
        this.uvIndex = uvIndex;
    }

    public String getAqiIndex() {
        return aqiIndex;
    }

    public void setAqiIndex(String aqiIndex) {
        this.aqiIndex = aqiIndex;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public List<FutureWeather> getFutureWeathers() {
        return futureWeathers;
    }

    public void setFutureWeathers(List<FutureWeather> futureWeathers) {
        this.futureWeathers = futureWeathers;
    }
}
