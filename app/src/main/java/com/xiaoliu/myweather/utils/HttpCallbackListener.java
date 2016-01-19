package com.xiaoliu.myweather.utils;

/**
 * Created by Administrator on 2016/1/19.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception exception);
}
