package com.example.appweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ImageView imgWelcome;
    private Toolbar mTbBar;

    @SuppressLint({"MissingInflatedId", "UseSupportActionBar"})  //写这个是因为setActionBar警告
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initWelcomeAnimate();
        setSupportActionBar(mTbBar);
        findViewById(R.id.tb_head).setPadding(0,getStatusBarHeight(),0,0);
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
                Toast.makeText(this, "你点了地区改变一下", Toast.LENGTH_SHORT).show();
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
    }

    private void initWelcomeAnimate(){
        Animation alphaAnimation = new AlphaAnimation(1.0f,0.0f);     //设置渐入动画
        alphaAnimation.setDuration(5000);                    //设置持续时间
        alphaAnimation.setFillAfter(true);                 //完成后不填充
        imgWelcome.setAnimation(alphaAnimation);           //设置欢迎图片动画为渐变
    }

    /**
     * 获取状态栏的高度
     */

    public int getStatusBarHeight() {
        int result = 0;
        //获取状态栏高度的资源id
        @SuppressLint("InternalInsetResource")
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}