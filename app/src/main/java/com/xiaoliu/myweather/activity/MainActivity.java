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
import android.view.KeyEvent;
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
import com.xiaoliu.myweather.service.AutoUpdateService;
import com.xiaoliu.myweather.utils.ActivityController;
import com.xiaoliu.myweather.utils.Constant;
import com.xiaoliu.myweather.utils.HttpCallbackListener;
import com.xiaoliu.myweather.utils.HttpUtils;
import com.xiaoliu.myweather.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    //定时器，用于退出程序
    private Timer timer;
    private boolean isExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        city = getIntent().getStringExtra("cityName");
        county = getIntent().getStringExtra("countyName");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putString("cityName", city).putString("countyName", county).commit();
        if (!TextUtils.isEmpty(county)&&!TextUtils.isEmpty(city)){
            Utility.queryWeather(county, this, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    showWeatherInfo(Utility.parseJson(MainActivity.this, response));
                }

                @Override
                public void onError(Exception exception) {

                }
            });
            Utility.queryAQI(city, this, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    showAQI(Utility.parseJson2(response));
                }

                @Override
                public void onError(Exception exception) {

                }
            });
        }else {
            showWeatherInfo(Utility.readWeatherInfoFromSP(MainActivity.this));
            showAQI(Utility.readAQIFromSP(MainActivity.this));
        }
        Intent service = new Intent(this, AutoUpdateService.class);
        startService(service);
        ActivityController.addActivity(this);
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
     * 异步任务类，用于下拉刷新
     */
    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Log.i("MainActivity", "正在刷新");
                Utility.queryWeather(county, MainActivity.this, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        showWeatherInfo(Utility.parseJson(MainActivity.this, response));
                    }

                    @Override
                    public void onError(Exception exception) {

                    }
                });
                Utility.queryAQI(city, MainActivity.this, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        showAQI(Utility.parseJson2(response));
                    }

                    @Override
                    public void onError(Exception exception) {

                    }
                });
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (!isExit){
                isExit = true;
                if (timer!=null){
                    timer.cancel();
                }
                timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                };
                Toast.makeText(this, "再按一次退出程序",Toast.LENGTH_SHORT).show();
                timer.schedule(task, 2000);
            }else{
                ActivityController.finishAll();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
