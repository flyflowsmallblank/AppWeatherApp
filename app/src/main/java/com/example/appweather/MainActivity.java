package com.example.appweather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView imgWelcome;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initWelcomeAnimate();
    }

    private void initView(){
        imgWelcome = findViewById(R.id.img_welcome);
    }

    private void initWelcomeAnimate(){
        Animation alphaAnimation = new AlphaAnimation(1.0f,0.0f);     //设置渐入动画
        alphaAnimation.setDuration(5000);                    //设置持续时间
        alphaAnimation.setFillAfter(true);                 //完成后不填充
        imgWelcome.setAnimation(alphaAnimation);           //设置欢迎图片动画为渐变
    }
}