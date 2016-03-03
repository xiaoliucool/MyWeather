package com.xiaoliu.myweather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.xiaoliu.myweather.db.MyWeatherDB;
import com.xiaoliu.myweather.model.AQI;
import com.xiaoliu.myweather.model.City;
import com.xiaoliu.myweather.model.CityWeather;
import com.xiaoliu.myweather.model.County;
import com.xiaoliu.myweather.model.Province;


/**
 * Created by Administrator on 2016/3/3.
 */
public class Utility {
    /**
     * 处理province数据 格式为“代号|城市，代号|城市”
     *
     * @param response
     *            服务器响应的字符串数据
     * @param MyWeatherDB
     * @return
     */
    public synchronized static boolean handleProvincesResponse(String response,
                                                               MyWeatherDB MyWeatherDB) {

        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            for (String p : allProvinces) {
                String[] array = p.split("\\|");
                Province province = new Province();
                province.setProvinceCode(array[0]);
                province.setProvinceName(array[1]);
                // 存入province表中
                MyWeatherDB.saveProvince(province);
            }
            return true;
        }
        return false;
    }

    /**
     * 处理city信息
     *
     * @param response
     *            格式为“代号|城市名，代号|城市名”
     * @param MyWeatherDB
     * @param provinceId
     * @return
     */
    public static boolean handleCitiesResponse(String response,
                                               MyWeatherDB MyWeatherDB, int provinceId) {

        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    // 存入数据库city表
                    MyWeatherDB.saveCity(city);
                }
            }
            return true;
        }

        return false;
    }

    /**
     * 处理县数据
     *
     * @param response
     *            格式为“代号|县名，代号|县名”
     * @param MyWeatherDB
     * @param cityId
     * @return
     */
    public static boolean handleCountiesResponse(String response,
                                                 MyWeatherDB MyWeatherDB, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    // 存入数据库county表
                    MyWeatherDB.saveCounty(county);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 将天气数据存储到本地，以防止断网的时候不能显示天气的问题。
     * @param context
     * @param cityWeather
     */
    public static void saveWeatherInfo(Context context, CityWeather cityWeather) {
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityWeather.getCityName());
        editor.putString("release", cityWeather.getReleaseTime());
        editor.putString("weather_info", cityWeather.getWeatherInfo());
        editor.putInt("fa", cityWeather.getFa());
        editor.putString("temperature_now", cityWeather.getTemperatureNow());
        editor.putString("temperature_range", cityWeather.getTemperatureRange());
        editor.putString("humidity", cityWeather.getHumidity());
        editor.putString("dressing_index", cityWeather.getDressingIndex());
        editor.putString("wind", cityWeather.getWind());
        editor.putString("uv_index", cityWeather.getUvIndex());
        editor.putString("today_week", cityWeather.getFutureWeathers().get(0).getWeek());
        editor.putInt("today_fa", cityWeather.getFutureWeathers().get(0).getFa());
        editor.putString("today_temperature", cityWeather.getFutureWeathers().get(0).getTemperature());
        editor.putString("tomorrow_week", cityWeather.getFutureWeathers().get(1).getWeek());
        editor.putInt("tomorrow_fa", cityWeather.getFutureWeathers().get(1).getFa());
        editor.putString("tomorrow_temperature", cityWeather.getFutureWeathers().get(1).getTemperature());
        editor.putString("the_day_after_tomorrow_week", cityWeather.getFutureWeathers().get(2).getWeek());
        editor.putInt("the_day_after_tomorrow_fa", cityWeather.getFutureWeathers().get(2).getFa());
        editor.putString("the_day_after_tomorrow_temperature", cityWeather.getFutureWeathers().get(2).getTemperature());
        editor.putString("last_week", cityWeather.getFutureWeathers().get(3).getWeek());
        editor.putInt("last_fa", cityWeather.getFutureWeathers().get(3).getFa());
        editor.putString("last_temperature", cityWeather.getFutureWeathers().get(3).getTemperature());
        editor.commit();
    }

    /**
     * 将空气质量数据存储到本地，以防止断网的时候不能显示天气的问题。
     * @param context
     * @param aqi
     */
    public static void saveAQIInfo(Context context, AQI aqi){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("pm25", aqi.getPm25());
        editor.putString("quality", aqi.getQuality());
        editor.commit();
    }
}
