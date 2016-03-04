package com.xiaoliu.myweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoliu.myweather.R;
import com.xiaoliu.myweather.db.MyWeatherDB;
import com.xiaoliu.myweather.model.City;
import com.xiaoliu.myweather.model.County;
import com.xiaoliu.myweather.model.Province;
import com.xiaoliu.myweather.utils.ActivityController;
import com.xiaoliu.myweather.utils.HttpCallbackListener;
import com.xiaoliu.myweather.utils.HttpUtils;
import com.xiaoliu.myweather.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/3.
 */
public class ChooseAreaActivity extends Activity {

    // 控制层级的常量
    private final static int LEVEL_PROVINCE = 0;
    private final static int LEVEL_CITY = 1;
    private final static int LEVEL_COUNTY = 2;

    // 当前的层级，默认初始化为0
    private int currentLevel;

    // 适配器与其中的数据
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<String>();

    // 对应layout中的控件
    private TextView titleView;
    private ListView listView;

    // 数据库，单例模式
    private MyWeatherDB coolWeatherDB;

    // 数据库中对应的三个表结构
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    // 选中的省、市、县
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;

    // 查询服务器时候的显示进度
    private ProgressDialog progressDialog;

    // 标志，判断是否是从weatherActivity跳转来的
    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra(
                "isFromWeatherActivity", false);
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        if (sp.getBoolean("city_selected", false)&&!isFromWeatherActivity) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_area);
        titleView = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.list_view);

        coolWeatherDB = MyWeatherDB.getInstance(this);
        // 因为定义了adapter的引用所以如果使用adapter匿名类引用，各个query方法因为使用的是adapter所以会出现空指针
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index,
                                    long arg3) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(index);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(index);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String countyName = countyList.get(index).getCountyName();
                    Intent intent = new Intent(ChooseAreaActivity.this,
                            MainActivity.class);
                    intent.putExtra("countyName", countyName);
                    intent.putExtra("cityName", selectedCity.getCityName());
                    startActivity(intent);
                    finish();
                }
            }
        });
        Log.i("coolweather", "开始查询province数据");
        queryProvinces();
        ActivityController.addActivity(this);
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvinces() {
        provinceList = coolWeatherDB.loadProvinces();
        Log.i("coolweather", "载入province数据成功");
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            Log.i("coolweather", "datalist初始化完成");
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleView.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer("", "province");
        }
    }

    /**
     * 查询城市数据
     */
    private void queryCities() {
        cityList = coolWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleView.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 查询县区数据
     */
    private void queryCounties() {
        countyList = coolWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleView.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    /**
     * 如果本地数据库灭有数据，就从服务器查询，处理的还是字符串数据
     *
     * @param code
     * @param type
     */
    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code
                    + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }

        showProgressDialog();

        HttpUtils.sendHttpRequest(address, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesResponse(response,
                            coolWeatherDB);
                } else if ("city".equals(type)) {
                    result = Utility.handleCitiesResponse(response,
                            coolWeatherDB, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountiesResponse(response,
                            coolWeatherDB, selectedCity.getId());
                }
                if (result) {
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}

