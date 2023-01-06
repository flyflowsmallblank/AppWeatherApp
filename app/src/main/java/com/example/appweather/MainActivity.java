package com.example.appweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ImageView imgWelcome;      //图画
    private Toolbar mTbBar;
    private TextView mTvLocated;         //这是上面的地址
    private TextView mTvTemp;            //中间的温度
    private TextView mTvWeatherText;     //中间的天气
    private static int count = 0;      //记得改为静态变量，要不找一辈子也找不到
    //下面是跳转过来需要的变量
    private String weather_id = null;      //这个是地区天气id,用来求取天气和更改背景
    private String name = "需要更改地区";        //这个是所在地区的名称
    private Handler mHandler;                //通信所需
    private int temperature;
    private String weatherText;


    @SuppressLint({"MissingInflatedId", "UseSupportActionBar"})  //写这个是因为setActionBar警告
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();                              //初始化找id
        initWelcomeAnimate();                    //找动画
        mHandler = new MyHandler();
        mTbBar.setTitle("");                    //将左边那个天气之子暴力清空，使标题栏上的字消失
        setSupportActionBar(mTbBar);            //将标题栏换成toolbar
        findViewById(R.id.tb_head).setPadding(0,getStatusBarHeight(),0,0);  //设置标题栏目远离状态栏,和状态栏分离
        //这里是跳转之后的，获得传递过来的信息
        getIntentExtra();                       //每次跳转都需要的方法,获取信息，网络请求
        setWeatherLocated();                    //设置所在地方天气，将其展示到屏幕中间最上方
        setBackground();                         //设置背景，根据天气设置背景
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
                finish();//跳转天气界面,结束当前活动
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
        Log.d("LX", "所获取天气的网址是 "+mUrl);
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
        saveData();
        mTvTemp.setText(String.valueOf(temperature));
        mTvWeatherText.setText(weatherText);
    }

    /**
     * 设置主界面背景的,使用一个selector，提前预制好我们的背景，下雨，晴天，多云，下雪，等到实际传进来对应的我们就切换，默认是晴天
     */

    private void setBackground(){
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
                Log.d("LX", "可能出问题的地方: "+in);
                String responseData = streamToString(in);
                Log.d("LX", "可能出问题的地方 "+responseData);
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
            jsonDecode(respondData);
            setTempAndText();      //返回数据后第一时间更改温度和文本
        }
    }

    /**
     *下面是一个解码json的方法
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