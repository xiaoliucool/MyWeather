package com.xiaoliu.myweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xiaoliu.myweather.service.AutoUpdateService;

/**
 * Created by Administrator on 2016/3/4.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("AutoUpdateReceiver", "收到广播");
        Intent service = new Intent(context, AutoUpdateService.class);
        context.startService(service);
    }
}
