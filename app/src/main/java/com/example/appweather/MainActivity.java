package com.example.appweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appweather.Adapter.Adapter_TwentyFour_Hour;
import com.example.appweather.Adapter.Adapter_forest_rv;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ImageView imgWelcome;      //图画
    private Toolbar mTbBar;
    private ConstraintLayout constraintLayout;
    private TextView mTvLocated;         //这是上面的地址
    private TextView mTvTemp;            //中间的温度
    private TextView mTvWeatherText;     //中间的天气
    private SwipeRefreshLayout mSrl;     //下拉刷新
    private RecyclerView mRvInfo;        //下面天气预报的信息
    private RecyclerView mRvHour;           //24h天气预报
    private static int count = 0;      //记得改为静态变量，要不找一辈子也找不到
    //下面是跳转过来需要的变量
    private String weather_id = null;      //这个是地区天气id,用来求取天气和更改背景
    private String name = "需要更改地区";        //这个是所在地区的名称
    private Handler mHandler;                //通信所需
    private int temperature;
    private String weatherText;
    private ArrayList<DayInformation> data = new ArrayList<>();     //七日天气预报的信息存储
    private ArrayList<OnTimeHour> data2 = new ArrayList<>();         //24小时天气预报的信息存储


    @SuppressLint({"MissingInflatedId", "UseSupportActionBar"})  //写这个是因为setActionBar警告
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();                              //初始化找id
        mSrl.setProgressBackgroundColorSchemeColor(Color.parseColor("#FF03DAC5")); //设置刷新背景
        my_refresh();                           //设置刷新之后应该做的事
        initWelcomeAnimate();                    //找动画
        mHandler = new MyHandler();
        mTbBar.setTitle("");                    //将左边那个天气之子暴力清空，使标题栏上的字消失
        setSupportActionBar(mTbBar);            //将标题栏换成toolbar
        findViewById(R.id.tb_head).setPadding(0,getStatusBarHeight(),0,0);  //设置标题栏目远离状态栏,和状态栏分离
        //这里是跳转之后的，获得传递过来的信息
        getIntentExtra();                       //每次跳转都需要的方法,获取信息，网络请求
        setWeatherLocated();                    //设置所在地方天气，将其展示到屏幕中间最上方
    }

    /**
     * 设置刷新之后的内部逻辑
     */

    private void my_refresh() {
        //首先监听按钮
        mSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSrl.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startConnection("https://devapi.qweather.com/v7/weather/now?location="+weather_id+"&key=12d6778b71cb41e092299b6629f43438");
                        mSrl.setRefreshing(false);
                    }
                },500);
            }
        });
    }

    /**
     * 下面是为了menu，就是右上角三个点点，重写两个方法
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_change:
                count++;   //跳转之后count就会加一
                Log.d("LX", "onOptionsItemSelected: count"+count);
                Intent intent = new Intent(this,WeatherLocated_Activity.class);
                startActivity(intent);
//                finish();//跳转天气界面,结束当前活动
                break;
            case R.id.menu_setting:
                Toast.makeText(this, "你点了设置一下", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    /**
     * 下面是初始化，找id和进入动画的初始化
     */

    private void initView(){
        imgWelcome = findViewById(R.id.img_welcome);
        mTbBar = (Toolbar) findViewById(R.id.tb_head);
        mTvLocated = findViewById(R.id.tv_located);
        mTvTemp = findViewById(R.id.tv_temp);
        mTvWeatherText = findViewById(R.id.tv_weather_text);
        constraintLayout = findViewById(R.id.layout_main);
        mSrl = findViewById(R.id.srl_refresh);
        mRvInfo = findViewById(R.id.rv_weather_information);
        mRvHour = findViewById(R.id.rv_hour);
    }

    private void initWelcomeAnimate(){              //入场动画
        Animation alphaAnimation = new AlphaAnimation(1.0f,0.0f);     //设置渐入动画
        alphaAnimation.setDuration(5000);                    //设置持续时间
        alphaAnimation.setFillAfter(true);                 //完成后不填充
        imgWelcome.setAnimation(alphaAnimation);           //设置欢迎图片动画为渐变
    }

    /**
     * 获取状态栏的高度
     */

    public int getStatusBarHeight() {      //得到状态栏的高度
        int result = 0;
        //获取状态栏高度的资源id
        @SuppressLint("InternalInsetResource")
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    /**
     * 下面写一个获取别的活动传递过来的信息的方法
     */
    private void getIntentExtra(){
        if(count==0){           //如果刚打开界面，就从之前存储的文件找，请求网络
            getData();
        }else{                 //打开过后count++，获得之后返回的数据
            Bundle extras = getIntent().getExtras();
            name = extras.getString("name");
            weather_id = extras.getString("weather_id");
        }
        Log.d("LX", "getIntentExtra: "+name+"的天气id"+weather_id);
        StringBuilder mUrl = new StringBuilder("https://devapi.qweather.com/v7/weather/now?location=");
        mUrl.append(weather_id).append("&key=12d6778b71cb41e092299b6629f43438");
        startConnection(mUrl.toString());
        Log.d("LX", "所获取实时天气的网址是 "+mUrl);
        //下面是七天的天气预报
        StringBuilder mUrlForest = new StringBuilder("https://devapi.qweather.com/v7/weather/7d?location=");
        mUrlForest.append(weather_id).append("&key=12d6778b71cb41e092299b6629f43438");
        startConnection(mUrlForest.toString());
        Log.d("LX", "所获取的预告天气是 " +mUrlForest );
        //下面是24小时的天气预报
        StringBuilder mUrlHour = new StringBuilder("https://devapi.qweather.com/v7/weather/24h?location=");
        mUrlHour.append(weather_id).append("&key=12d6778b71cb41e092299b6629f43438");
        startConnection(mUrlHour.toString());
        Log.d("LX", "24h预告天气的网址为 " + mUrlHour);
    }

    /**
     * 下面将写一个设置地区的显示文字,那个地区显示id叫tv_located
     */

    private void setWeatherLocated(){
        mTvLocated.setText(name);
    }

    /**
     *下面将写一个方法，用于设置主界面天气的温度，和下面的文本。
     */

    private void setTempAndText(){
        setBackground();                         //设置背景，根据天气设置背景
        saveData();
        mTvTemp.setText(String.valueOf(temperature));
        mTvWeatherText.setText(weatherText);
    }

    /**
     * 根据传过来的信息设置下面的recyclerView
     */

    private void setForestInfo() {
        Adapter_forest_rv adapter_forest_rv = new Adapter_forest_rv(data,7); //构造适配器,设置显示七天的
        mRvInfo.setAdapter(adapter_forest_rv);            //同时设置我们recyclerView的适配器
        mRvInfo.setLayoutManager(new GridLayoutManager(this,1));  //设置七天预报数据一溜下来；
    }

    private void setHourInfo() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvHour.setLayoutManager(linearLayoutManager);
        Adapter_TwentyFour_Hour adapter_twentyFour_hour = new Adapter_TwentyFour_Hour(data2);
        mRvHour.setAdapter(adapter_twentyFour_hour);
//        mRvHour.setLayoutManager(new GridLayoutManager(this,1));
    }

    /**
     * 设置主界面背景的,使用一个selector，提前预制好我们的背景，下雨，晴天，多云，下雪，等到实际传进来对应的我们就切换，默认是晴天
     */

    @SuppressLint("ResourceType")
    private void setBackground(){
        switch (weatherText){
            case "晴":
                constraintLayout.setBackgroundResource(R.drawable.weather_fine_day);
                break;
            case "多云":
                constraintLayout.setBackgroundResource(R.drawable.weather_cloud_day);
                break;
            case "雪":
            case "小雪":
            case "中雪":
            case "大雪":
                constraintLayout.setBackgroundResource(R.drawable.weather_snow_day);
                break;
            case "雨":
            case "小雨":
            case "中雨":
            case "大雨":
                constraintLayout.setBackgroundResource(R.drawable.weather_rain_day);
                break;
        }
    }

    /**
     * 进行天气的网络请求,weather_id已知
     */

    private void startConnection(String mUrl){
        new Thread(()->{
            try{
                URL url = new URL(mUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(8000);
                connection.setConnectTimeout(8000);
                connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
//                connection.setRequestProperty("Accept-Encoding", "gzip,deflate");         //这个请求头删掉就好了
                connection.connect();
                InputStream in = connection.getInputStream();
                Log.d("LX", "处理之前的输入流: "+in);
                String responseData = streamToString(in);
                Log.d("LX", "处理完之后的输入流 "+responseData);
                Message message = new Message();
                message.obj = responseData;
                mHandler.sendMessage(message);

            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 下面写一个拼接输入流的方法
     */

    private String streamToString(InputStream in){
        StringBuilder sb = new StringBuilder();
        String oneLine;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try{
            while((oneLine = reader.readLine()) != null){        //readline一读读一行
                sb.append(oneLine).append('\n');           //换行增加可读性
            }
        }catch (IOException e){  //捕获输入输出异常
            e.printStackTrace();
        }finally {
            try{
                in.close();               //关闭输入流
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 新建一个内部类用于通信
     */

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String respondData = msg.obj.toString();
            try {
                JSONObject jsonObject = new JSONObject(respondData);  //测试一下
                if(jsonObject.has("now")){
                    Log.d("LX", "我成功匹配实时天气");
                    jsonDecode(respondData);  //这个是温度和天气状况文本的改变
                    setTempAndText();      //第一时间更改温度和文本
                    //上面是匹配现在的实时天气
                    //下面匹配的是七天预告天气
                }else if(jsonObject.has("daily")){
                    Log.d("LX", "我成功匹配预告天气");
                    jsonDecodeForest(respondData);
                    setForestInfo();      //第一时间更改温度和文本
                }else if(jsonObject.has("hourly")){
                    Log.d("LX", "我成功匹配24小时天气 ");
                    jsonDecodeHour(respondData);
                    setHourInfo();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *下面是三个解码json的方法
     */

    private void jsonDecode(String respondData) {
        try {
            JSONObject jsonObject = new JSONObject(respondData);
            JSONObject jsonObject1 = jsonObject.getJSONObject("now");
            temperature = jsonObject1.getInt("temp");
            weatherText = jsonObject1.getString("text");
            Log.d("LX", "温度和天气文本是 "+temperature+weatherText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void jsonDecodeHour(String respondData) {
        try{
            JSONObject jsonObject = new JSONObject(respondData);
            JSONArray jsonArray = jsonObject.getJSONArray("hourly");
            for (int i = 0; i < jsonArray.length(); i++) {
                OnTimeHour onTimeHour = new OnTimeHour();
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                onTimeHour.setTime(jsonObject1.getString("fxTime"));
                onTimeHour.setTemp(jsonObject1.getString("temp"));
                data2.add(onTimeHour);
            }
            Log.d("LX", "获得的集合为 ");
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void jsonDecodeForest(String respondData) {
        try{
            JSONObject jsonObject = new JSONObject(respondData);
            JSONArray jsonArray = jsonObject.getJSONArray("daily");
            for (int i = 0; i < jsonArray.length(); i++) {
                DayInformation dayInformation = new DayInformation();
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                dayInformation.setDay(jsonObject1.getString("fxDate"));
                if(jsonObject1.getString("textDay").equals(jsonObject1.getString("textNight"))){
                    dayInformation.setForestWeatherText(jsonObject1.getString("textDay"));
                }else{
                    dayInformation.setForestWeatherText(jsonObject1.getString("textDay")+"转"+jsonObject1.getString("textNight"));
                }
                dayInformation.setTempMax(jsonObject1.getString("tempMax"));
                dayInformation.setTempMin(jsonObject1.getString("tempMin"));
                data.add(dayInformation);
            }
            Log.d("LX", "获得的集合为 ");
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 下面是一个存储的方法,简单的本地存储
     */

    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("地址信息", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("located",name);
        editor.putString("weather_id",weather_id);
        editor.apply();
    }

    /**
     * 下面是一个读取saveData的方法
     */

    private void getData(){
        SharedPreferences sharedPreferences = getSharedPreferences("地址信息",Context.MODE_PRIVATE);
        name = sharedPreferences.getString("located","北京");
        weather_id = sharedPreferences.getString("weather_id","CN101010100");
    }
}