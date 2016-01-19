package com.xiaoliu.myweather.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.JuheSDKInitializer;
import com.thinkland.sdk.android.Parameters;
import com.xiaoliu.myweather.R;
import com.xiaoliu.myweather.utils.Constant;
import com.xiaoliu.myweather.utils.HttpCallbackListener;
import com.xiaoliu.myweather.utils.HttpUtils;

public class MainActivity extends AppCompatActivity {
    private TextView contentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JuheSDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        contentText = (TextView) findViewById(R.id.tv_show_content);
        Parameters params = new Parameters();
        params.add("cityname", "上海");
        params.add("format", 2);
        JuheData.executeWithAPI(this, 39, "http://v.juhe.cn/weather/index", JuheData.GET, params, new DataCallBack() {
            @Override
            public void onSuccess(int i, String s) {
                contentText.setText(s);
            }

            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "finish",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {
                contentText.append(throwable.getMessage() + "\n");
            }
        });
    }
}
