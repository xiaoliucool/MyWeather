package com.xiaoliu.myweather.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity {
    PullToRefreshScrollView mPullRefreshScrollView;
    ScrollView mScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryWeather();
        /*mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull2refresh);
        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                new GetDataTask().execute();
            }
        });
        mScrollView = mPullRefreshScrollView.getRefreshableView();*/
    }
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
    public void queryWeather(){
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
}
