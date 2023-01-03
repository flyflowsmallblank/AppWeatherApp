package com.example.appweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ImageView imgWelcome;
    private Toolbar mTbBar;
    private TextView mTvLocated;
    private static int count = 0;      //记得改为静态变量，要不找一辈子也找不到
    //下面是跳转过来需要的变量
    private String weather_id = null;      //这个是地区天气id,用来求取天气和更改背景
    private String name = "需要更改地区";        //这个是所在地区的名称


    @SuppressLint({"MissingInflatedId", "UseSupportActionBar"})  //写这个是因为setActionBar警告
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();                              //初始化找id
        initWelcomeAnimate();                    //找动画
        mTbBar.setTitle("");                    //将左边那个天气之子暴力清空，使标题栏上的字消失
        setSupportActionBar(mTbBar);            //将标题栏换成toolbar
        findViewById(R.id.tb_head).setPadding(0,getStatusBarHeight(),0,0);  //设置标题栏目远离状态栏,和状态栏分离
        //这里是跳转之后的，获得传递过来的信息
        if(count%2==1){
            getIntentExtra();
        }
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
                count++;
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
        Bundle extras = getIntent().getExtras();
        name = extras.getString("name");
        weather_id = extras.getString("weather_id");
        Log.d("LX", "getIntentExtra: "+name+"的天气id"+weather_id);
        startConnection("");
    }

    /**
     * 下面将写一个设置地区的显示文字,那个地区显示id叫tv_located
     */

    private void setWeatherLocated(){
        mTvLocated.setText(name);
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

        }).start();
    }
}