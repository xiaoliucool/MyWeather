package com.xiaoliu.myweather.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.JuheSDKInitializer;
import com.thinkland.sdk.android.Parameters;
import com.xiaoliu.myweather.R;
import com.xiaoliu.myweather.model.AQI;
import com.xiaoliu.myweather.model.CityWeather;
import com.xiaoliu.myweather.model.FutureWeather;
import com.xiaoliu.myweather.pull2refresh.PullToRefreshBase;
import com.xiaoliu.myweather.pull2refresh.PullToRefreshScrollView;
import com.xiaoliu.myweather.utils.Constant;
import com.xiaoliu.myweather.utils.HttpCallbackListener;
import com.xiaoliu.myweather.utils.HttpUtils;
import com.xiaoliu.myweather.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //各个控件
    private TextView cityNameText;
    private TextView releaseText;
    private ImageView weatherImage;
    private TextView weatherText;
    private TextView temperatureUpText;
    private TextView temperatureDownText;
    private TextView temperatureNowText;
    private TextView aqiText;
    private TextView qualityText;

    private TextView todayText;
    private ImageView todayWeatherImage;
    private TextView todayTemperatureUpText;
    private TextView todayTemperatureDownText;
    private TextView tomorrowText;
    private ImageView tomorrowWeatherImage;
    private TextView tomorrowTemperatureUpText;
    private TextView tomorrowTemperatureDownText;
    private TextView theDayAfterTomorrowText;
    private ImageView theDayAfterTomorrowWeatherImage;
    private TextView theDayAfterTomorrowTemperatureUpText;
    private TextView theDayAfterTomorrowTemperatureDownText;
    private TextView lastText;
    private ImageView lastWeatherImage;
    private TextView lastTemperatureUpText;
    private TextView lastTemperatureDownText;

    private TextView humidityText;
    private TextView windText;
    private TextView uvIndexText;
    private TextView dressingIndexText;
    //下拉刷新控件
    PullToRefreshScrollView mPullRefreshScrollView;
    ScrollView mScrollView;
    //天气选择控件
    private ImageView citySelectImage;
    //查询天气的县市名
    private String county;
    //查询空气质量的城市名
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        city = getIntent().getStringExtra("cityName");
        county = getIntent().getStringExtra("countyName");
        if (!TextUtils.isEmpty(county)&&!TextUtils.isEmpty(city)){
            queryWeather(county);
            queryAQI(city);
        }else {
            showWeatherInfo(readWeatherInfoFromSP());
            showAQI(readAQIFromSP());
        }
    }

    /**
     * 初始化各个控件
     */
    public void initViews() {
        cityNameText = (TextView) findViewById(R.id.tv_city);
        releaseText = (TextView) findViewById(R.id.tv_release);

        weatherImage = (ImageView) findViewById(R.id.iv_weather);
        weatherText = (TextView) findViewById(R.id.tv_weather);
        temperatureUpText = (TextView) findViewById(R.id.temp_up);
        temperatureDownText = (TextView) findViewById(R.id.temp_down);
        temperatureNowText = (TextView) findViewById(R.id.tv_temp_now);
        aqiText = (TextView) findViewById(R.id.tv_aqi);
        qualityText = (TextView) findViewById(R.id.tv_quality);

        todayText = (TextView) findViewById(R.id.tv_today);
        todayWeatherImage = (ImageView) findViewById(R.id.iv_today_weather);
        todayTemperatureUpText = (TextView) findViewById(R.id.tv_today_tmp_a);
        todayTemperatureDownText = (TextView) findViewById(R.id.tv_today_tmp_b);
        tomorrowText = (TextView) findViewById(R.id.tv_tomorrow);
        tomorrowWeatherImage = (ImageView) findViewById(R.id.iv_tomorrow);
        tomorrowTemperatureUpText = (TextView) findViewById(R.id.tv_tomorrow_a);
        tomorrowTemperatureDownText = (TextView) findViewById(R.id.tv_tomorrow_b);
        theDayAfterTomorrowText = (TextView) findViewById(R.id.tv_the_day_after_tomorrow);
        theDayAfterTomorrowWeatherImage = (ImageView) findViewById(R.id.iv_the_day_after_tomorrow);
        theDayAfterTomorrowTemperatureUpText = (TextView) findViewById(R.id.tv_the_day_after_tomorrow_tmp_a);
        theDayAfterTomorrowTemperatureDownText = (TextView) findViewById(R.id.tv_the_day_after_tomorrow_tmp_b);
        lastText = (TextView) findViewById(R.id.tv_the_last_day);
        lastWeatherImage = (ImageView) findViewById(R.id.iv_the_last_day);
        lastTemperatureUpText = (TextView) findViewById(R.id.tv_the_last_day_tmp_a);
        lastTemperatureDownText = (TextView) findViewById(R.id.tv_the_last_day_tmp_b);

        humidityText = (TextView) findViewById(R.id.tv_humidity);
        windText = (TextView) findViewById(R.id.tv_wind);
        uvIndexText = (TextView) findViewById(R.id.tv_uv_index);
        dressingIndexText = (TextView) findViewById(R.id.tv_dressing_index);

        citySelectImage = (ImageView) findViewById(R.id.iv_city_select);
        citySelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChooseAreaActivity.class);
                intent.putExtra("isFromWeatherActivity", true);
                startActivity(intent);
            }
        });

        mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull2refresh);
        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                Log.i("MainActivity", "正在刷新");
                new GetDataTask().execute();
            }
        });
        mScrollView = mPullRefreshScrollView.getRefreshableView();
    }

    /**
     * 查询空气质量状况
     *
     * @param cityName
     */
    public void queryAQI(String cityName) {
        Parameters parameters = new Parameters();
        parameters.add("city", cityName);
        JuheData.executeWithAPI(getApplication(), 33, "http://web.juhe.cn:8080/environment/air/pm", JuheData.GET, parameters, new DataCallBack() {
            @Override
            public void onSuccess(int i, String s) {
                System.out.println(s);
                showAQI(parseJson2(s));
            }

            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "空气质量请求成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "空气质量请求失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 显示空气质量状况
     *
     * @param aqi
     */
    public void showAQI(AQI aqi) {
        aqiText.setText(aqi.getPm25());
        qualityText.setText(aqi.getQuality());
        Utility.saveAQIInfo(MainActivity.this, aqi);
    }

    /**
     *
     */
    public AQI parseJson2(final String json) {
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
    public void queryWeather(final String cityName) {
        Parameters parameters = new Parameters();
        parameters.add("cityname", cityName);
        parameters.add("format", 2);
        JuheData.executeWithAPI(getApplication(), 39, "http://v.juhe.cn/weather/index", JuheData.GET, parameters, new DataCallBack() {
            @Override
            public void onSuccess(int i, String s) {
                // System.out.println(s);
                showWeatherInfo(parseJson(getApplicationContext(), s));
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
    public CityWeather parseJson(Context context, String json) {
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
     * 显示天气信息
     *
     * @param cityWeather
     */
    public void showWeatherInfo(CityWeather cityWeather) {
        cityNameText.setText(cityWeather.getCityName());
        releaseText.setText("发布于今天" + cityWeather.getReleaseTime());
        weatherText.setText(cityWeather.getWeatherInfo());
        weatherImage.setImageResource(getResources().getIdentifier("d" + cityWeather.getFa(), "drawable", "com.xiaoliu.myweather"));
        String tempDown = "↓" + cityWeather.getTemperatureRange().split("~")[0];
        String tempUp = "↑" + cityWeather.getTemperatureRange().split("~")[1];
        temperatureUpText.setText(tempUp);
        temperatureDownText.setText(tempDown);
        temperatureNowText.setText(cityWeather.getTemperatureNow() + "℃");
        //显示未来天气状况
        if (cityWeather.getFutureWeathers().size() == 4) {
            showFutureWeatherInfo(todayText, todayWeatherImage, todayTemperatureUpText, todayTemperatureDownText, cityWeather.getFutureWeathers().get(0));
            showFutureWeatherInfo(tomorrowText, tomorrowWeatherImage, tomorrowTemperatureUpText, tomorrowTemperatureDownText, cityWeather.getFutureWeathers().get(1));
            showFutureWeatherInfo(theDayAfterTomorrowText, theDayAfterTomorrowWeatherImage, theDayAfterTomorrowTemperatureUpText, theDayAfterTomorrowTemperatureDownText, cityWeather.getFutureWeathers().get(2));
            showFutureWeatherInfo(lastText, lastWeatherImage, lastTemperatureUpText, lastTemperatureDownText, cityWeather.getFutureWeathers().get(3));
        }

        humidityText.setText(cityWeather.getHumidity());
        windText.setText(cityWeather.getWind());
        uvIndexText.setText(cityWeather.getUvIndex());
        dressingIndexText.setText(cityWeather.getDressingIndex());
        Utility.saveWeatherInfo(MainActivity.this, cityWeather);
    }

    private void showFutureWeatherInfo(TextView week, ImageView weatherImag, TextView tempUp, TextView tempDown, FutureWeather future) {
        week.setText(future.getWeek());
        weatherImag.setImageResource(getResources().getIdentifier("d" + future.getFa(), "drawable", "com.xiaoliu.myweather"));
        String tempDownStr = "↓" + future.getTemperature().split("~")[0];
        String tempUpStr = "↑" + future.getTemperature().split("~")[1];
        tempUp.setText(tempUpStr);
        tempDown.setText(tempDownStr);
    }

    /**
     * 从sharedPreference读取存储的天气信息
     * @return
     */
    public CityWeather readWeatherInfoFromSP(){
        CityWeather cityWeather = new CityWeather();
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
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
    public FutureWeather readFutureWeatherInfoFromSP(String week, int fa, String temp){
        FutureWeather futureWeather = new FutureWeather();
        futureWeather.setWeek(week);
        futureWeather.setFa(fa);
        futureWeather.setTemperature(temp);
        return futureWeather;
    }
    public AQI readAQIFromSP(){
        AQI aqi = new AQI();
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        aqi.setPm25(sp.getString("pm25", ""));
        aqi.setQuality(sp.getString("quality", ""));
        return aqi;
    }

    /**
     * 异步任务类，用于下拉刷新
     */
    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Log.i("MainActivity", "正在刷新");
                queryWeather(county);
                queryAQI(city);
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Do some stuff here

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshScrollView.onRefreshComplete();
            Log.i("MainActivity", "刷新完成");
            super.onPostExecute(result);
        }
    }
}
