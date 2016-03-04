package com.xiaoliu.myweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoliu.myweather.receiver.AutoUpdateReceiver;
import com.xiaoliu.myweather.utils.HttpCallbackListener;
import com.xiaoliu.myweather.utils.Utility;

/**
 * Created by Administrator on 2016/3/4.
 */
public class AutoUpdateService extends Service {
    private String county;
    private String city;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("AutoUpdateService", "服务开启");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("AutoUpdateService", "正在后台更新。。。。。");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        county = sp.getString("countyName", "");
        city = sp.getString("cityName", "");
        if (!TextUtils.isEmpty(county)&&!TextUtils.isEmpty(city)){
            Utility.queryWeather(county, getApplicationContext(), new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    if (!TextUtils.isEmpty(response)){
                        Log.i("AutoUpdateService", "正在后台更新天气的城市是"+county);
                        Utility.saveWeatherInfo(getApplicationContext(), Utility.parseJson(getApplicationContext(), response));
                    }
                }

                @Override
                public void onError(Exception exception) {

                }
            });
            Utility.queryAQI(city, getApplicationContext(), new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    if (!TextUtils.isEmpty(response)){
                        Log.i("AutoUpdateService", "正在后台更新空气质量的城市是"+city);
                        Utility.saveAQIInfo(getApplicationContext(), Utility.parseJson2(response));
                    }
                }

                @Override
                public void onError(Exception exception) {

                }
            });
        }
        //每隔一小时自动更新
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long anHour = 60*60*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }
}
