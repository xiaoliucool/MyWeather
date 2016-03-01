package com.xiaoliu.myweather.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.JuheSDKInitializer;
import com.thinkland.sdk.android.Parameters;
import com.xiaoliu.myweather.R;
import com.xiaoliu.myweather.pull2refresh.PullToRefreshBase;
import com.xiaoliu.myweather.pull2refresh.PullToRefreshScrollView;
import com.xiaoliu.myweather.utils.Constant;
import com.xiaoliu.myweather.utils.HttpCallbackListener;
import com.xiaoliu.myweather.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
    private TextView nextThreeDescripeText;
    private ImageView nextThreeWeatherImage;
    private TextView nextThreeTempText;
    private TextView nextSixDescripeText;
    private ImageView nextSixWeatherImage;
    private TextView nextSixTempText;
    private TextView nextNineDescripeText;
    private ImageView nextNineWeatherImage;
    private TextView nextTNineTempText;
    private TextView nextTwelveDescripeText;
    private ImageView nextTwelveWeatherImage;
    private TextView nextTwelveTempText;
    private TextView nextTFifteenDescripeText;
    private ImageView nextFifteenWeatherImage;
    private TextView nextTFifteenTempText;
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
    private TextView feelingText;
    private TextView humidityText;
    private TextView windText;
    private TextView uvIndexText;
    private TextView dressingIndexText;

    PullToRefreshScrollView mPullRefreshScrollView;
    ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        queryWeather("北京");
        /*mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull2refresh);
        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                new GetDataTask().execute();
            }
        });
        mScrollView = mPullRefreshScrollView.getRefreshableView();*/
    }

    /**
     * 初始化各个控件
     */
    public void initViews(){
        cityNameText = (TextView) findViewById(R.id.tv_city);
        releaseText = (TextView) findViewById(R.id.tv_release);

        weatherImage = (ImageView) findViewById(R.id.iv_weather);
        weatherText = (TextView) findViewById(R.id.tv_weather);
        temperatureUpText = (TextView) findViewById(R.id.temp_up);
        temperatureDownText = (TextView) findViewById(R.id.temp_down);
        temperatureNowText = (TextView) findViewById(R.id.tv_temp_now);
        aqiText = (TextView) findViewById(R.id.tv_aqi);
        qualityText = (TextView) findViewById(R.id.tv_quality);

        nextThreeDescripeText = (TextView) findViewById(R.id.tv_next_three);
        nextThreeWeatherImage = (ImageView) findViewById(R.id.iv_next_three);
        nextThreeTempText = (TextView) findViewById(R.id.tv_next_three_temp);
        nextSixDescripeText = (TextView) findViewById(R.id.tv_next_six);
        nextSixWeatherImage = (ImageView) findViewById(R.id.iv_next_six);
        nextSixTempText = (TextView) findViewById(R.id.tv_next_six_temp);
        nextNineDescripeText = (TextView) findViewById(R.id.tv_next_nine);
        nextNineWeatherImage = (ImageView) findViewById(R.id.iv_next_nine);
        nextTNineTempText = (TextView) findViewById(R.id.tv_next_nine_temp);
        nextTwelveDescripeText = (TextView) findViewById(R.id.tv_next_twelve);
        nextTwelveWeatherImage = (ImageView) findViewById(R.id.iv_next_twelve);
        nextTwelveTempText = (TextView) findViewById(R.id.tv_next_twelve_temp);
        nextTFifteenDescripeText = (TextView) findViewById(R.id.tv_next_fifteen);
        nextFifteenWeatherImage = (ImageView) findViewById(R.id.iv_next_fifteen);
        nextTFifteenTempText = (TextView) findViewById(R.id.tv_next_fifteen_temp);

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

        feelingText = (TextView) findViewById(R.id.tv_feeling);
        humidityText = (TextView) findViewById(R.id.tv_humidity);
        windText = (TextView) findViewById(R.id.tv_wind);
        uvIndexText = (TextView) findViewById(R.id.tv_uv_index);
        dressingIndexText = (TextView) findViewById(R.id.tv_dressing_index);
    }
    /**
     * 根据城市查询天气
     */
    public void queryWeather(String cityName){
        Parameters parameters = new Parameters();
        parameters.add("cityname","北京");
        parameters.add("format", 2);
        JuheData.executeWithAPI(getApplication(), 39, "http://v.juhe.cn/weather/index", JuheData.GET, parameters, new DataCallBack() {
            @Override
            public void onSuccess(int i, String s) {
                System.out.println(s);
            }

            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(),"请求成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {
                Toast.makeText(getApplicationContext(),"请求失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void parseJson(Context context, String json){
        try {
            JSONObject jsonObject = new JSONObject(json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 异步任务类，用于下拉刷新
     */
    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Do some stuff here

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshScrollView.onRefreshComplete();

            super.onPostExecute(result);
        }
    }
}
