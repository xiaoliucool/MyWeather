package com.xiaoliu.myweather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;
import com.xiaoliu.myweather.db.MyWeatherDB;
import com.xiaoliu.myweather.model.AQI;
import com.xiaoliu.myweather.model.City;
import com.xiaoliu.myweather.model.CityWeather;
import com.xiaoliu.myweather.model.County;
import com.xiaoliu.myweather.model.FutureWeather;
import com.xiaoliu.myweather.model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


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
    /**
     * 查询空气质量状况
     *
     * @param cityName
     */
    public static void queryAQI(String cityName, final Context context, final HttpCallbackListener listener) {
        Parameters parameters = new Parameters();
        parameters.add("city", cityName);
        JuheData.executeWithAPI(context, 33, "http://web.juhe.cn:8080/environment/air/pm", JuheData.GET, parameters, new DataCallBack() {
            @Override
            public void onSuccess(int i, String s) {
                System.out.println(s);
                listener.onFinish(s);
            }

            @Override
            public void onFinish() {
                //Toast.makeText(context, "空气质量请求成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {
                //Toast.makeText(context, "空气质量请求失败", Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     *
     */
    public static AQI parseJson2(final String json) {
        AQI aqi = new AQI();
        try {
            JSONObject jsonObject = new JSONObject(json);
            int resultCode = jsonObject.getInt("resultcode");
            System.out.println("resultCode==" + resultCode);
            Log.i("MainActivity", "开始解析");
            JSONArray result = jsonObject.getJSONArray("result");
            aqi.setPm25(result.getJSONObject(0).getString("PM2.5"));
            aqi.setQuality(result.getJSONObject(0).getString("quality"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return aqi;
    }

    /**
     * 根据城市查询天气
     */
    public static void queryWeather(final String cityName, final Context context, final HttpCallbackListener listener) {
        Parameters parameters = new Parameters();
        parameters.add("cityname", cityName);
        parameters.add("format", 2);
        JuheData.executeWithAPI(context, 39, "http://v.juhe.cn/weather/index", JuheData.GET, parameters, new DataCallBack() {
            @Override
            public void onSuccess(int i, String s) {
                // System.out.println(s);
                listener.onFinish(s);
            }

            @Override
            public void onFinish() {
                //Toast.makeText(getApplicationContext(),"请求成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {
                //Toast.makeText(getApplicationContext(),"请求失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 解析json
     *
     * @param context
     * @param json
     * @return
     */
    public static CityWeather parseJson(Context context, String json) {
        CityWeather cityWeather = new CityWeather();
        try {
            JSONObject jsonObject = new JSONObject(json);
            int resultCode = jsonObject.getInt("resultcode");
            if (resultCode == 200) {
                Log.i("MainActivity", "数据请求成功");
                JSONObject weatherObject = jsonObject.getJSONObject("result");
                JSONObject skWeather = weatherObject.getJSONObject("sk");
                JSONObject todayWeather = weatherObject.getJSONObject("today");
                JSONArray futureWeatherArray = weatherObject.getJSONArray("future");
                //今天的天气情况
                cityWeather.setCityName(todayWeather.getString("city"));
                cityWeather.setReleaseTime(skWeather.getString("time"));
                cityWeather.setWeatherInfo(todayWeather.getString("weather"));
                cityWeather.setFa(todayWeather.getJSONObject("weather_id").getInt("fa"));
                cityWeather.setFb(todayWeather.getJSONObject("weather_id").getInt("fb"));
                cityWeather.setTemperatureNow(skWeather.getString("temp"));
                cityWeather.setTemperatureRange(todayWeather.getString("temperature"));
                cityWeather.setHumidity(skWeather.getString("humidity"));
                cityWeather.setWind(todayWeather.getString("wind"));
                cityWeather.setDressingIndex(todayWeather.getString("dressing_index"));
                cityWeather.setUvIndex(todayWeather.getString("uv_index"));
                //未来4天的天气
                List<FutureWeather> futureWeathers = new ArrayList<>();
                FutureWeather today = new FutureWeather();
                today.setWeek("今天");
                today.setFa(futureWeatherArray.getJSONObject(0).getJSONObject("weather_id").getInt("fa"));
                today.setFb(futureWeatherArray.getJSONObject(0).getJSONObject("weather_id").getInt("fb"));
                today.setTemperature(futureWeatherArray.getJSONObject(0).getString("temperature"));
                futureWeathers.add(today);
                FutureWeather tomorrow = new FutureWeather();
                tomorrow.setWeek("明天");
                tomorrow.setFa(futureWeatherArray.getJSONObject(1).getJSONObject("weather_id").getInt("fa"));
                tomorrow.setFb(futureWeatherArray.getJSONObject(1).getJSONObject("weather_id").getInt("fb"));
                tomorrow.setTemperature(futureWeatherArray.getJSONObject(1).getString("temperature"));
                futureWeathers.add(tomorrow);
                FutureWeather theDayAfterTomorrow = new FutureWeather();
                theDayAfterTomorrow.setWeek("后天");
                theDayAfterTomorrow.setFa(futureWeatherArray.getJSONObject(2).getJSONObject("weather_id").getInt("fa"));
                theDayAfterTomorrow.setFb(futureWeatherArray.getJSONObject(2).getJSONObject("weather_id").getInt("fb"));
                theDayAfterTomorrow.setTemperature(futureWeatherArray.getJSONObject(2).getString("temperature"));
                futureWeathers.add(theDayAfterTomorrow);
                FutureWeather lastDay = new FutureWeather();
                lastDay.setWeek(futureWeatherArray.getJSONObject(3).getString("week"));
                lastDay.setFa(futureWeatherArray.getJSONObject(3).getJSONObject("weather_id").getInt("fa"));
                lastDay.setFb(futureWeatherArray.getJSONObject(3).getJSONObject("weather_id").getInt("fb"));
                lastDay.setTemperature(futureWeatherArray.getJSONObject(3).getString("temperature"));
                futureWeathers.add(lastDay);
                cityWeather.setFutureWeathers(futureWeathers);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cityWeather;
    }

    /**
     * 从sharedPreference读取存储的天气信息
     * @return
     */
    public static CityWeather readWeatherInfoFromSP(Context context){
        CityWeather cityWeather = new CityWeather();
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
        cityWeather.setCityName(sp.getString("city_name", ""));
        cityWeather.setReleaseTime(sp.getString("release", ""));
        cityWeather.setWeatherInfo(sp.getString("weather_info", ""));
        cityWeather.setFa(sp.getInt("fa", 0));
        cityWeather.setTemperatureNow(sp.getString("temperature_now", ""));
        cityWeather.setTemperatureRange(sp.getString("temperature_range", ""));
        cityWeather.setHumidity(sp.getString("humidity", ""));
        cityWeather.setDressingIndex(sp.getString("dressing_index", ""));
        cityWeather.setWind(sp.getString("wind", ""));
        cityWeather.setUvIndex(sp.getString("uv_index", ""));
        ArrayList<FutureWeather> list = new ArrayList<>();
        list.add(readFutureWeatherInfoFromSP(sp.getString("today_week", ""),sp.getInt("today_fa", 0) , sp.getString("today_temperature", "")));
        list.add(readFutureWeatherInfoFromSP(sp.getString("tomorrow_week", ""),sp.getInt("tomorrow_fa", 0) , sp.getString("tomorrow_temperature", "")));
        list.add(readFutureWeatherInfoFromSP(sp.getString("the_day_after_tomorrow_week", ""),sp.getInt("the_day_after_tomorrow_fa", 0) , sp.getString("the_day_after_tomorrow_temperature", "")));
        list.add(readFutureWeatherInfoFromSP(sp.getString("last_week", ""),sp.getInt("last_fa", 0) , sp.getString("last_temperature", "")));
        cityWeather.setFutureWeathers(list);
        return cityWeather;
    }

    /**
     * 从sharedPreference读取存储的空气质量信息
     * @param week
     * @param fa
     * @param temp
     * @return
     */
    public static FutureWeather readFutureWeatherInfoFromSP(String week, int fa, String temp){
        FutureWeather futureWeather = new FutureWeather();
        futureWeather.setWeek(week);
        futureWeather.setFa(fa);
        futureWeather.setTemperature(temp);
        return futureWeather;
    }
    public static  AQI readAQIFromSP(Context context){
        AQI aqi = new AQI();
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
        aqi.setPm25(sp.getString("pm25", ""));
        aqi.setQuality(sp.getString("quality", ""));
        return aqi;
    }
}
