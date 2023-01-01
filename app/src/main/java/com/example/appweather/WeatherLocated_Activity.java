package com.example.appweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 设置所在地区
 */
public class WeatherLocated_Activity extends AppCompatActivity {
    int[] idData;
    String[] locatedData;
    private RecyclerView mRvLocated;
    LocatedSetting_Adapter locatedSetting_adapter;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_located);
        initView();
        startConnection("http://guolin.tech/api/china");  //刚进来时后就进行网络请求
        initClick();
        mHandler = new myHandler();

    }
    /**
     * 初始化点击
     */
    private void initView(){
        mRvLocated = findViewById(R.id.rv_located);
    }
    /**
     * 初始化点击事件,需要进一步确定位置是哪里
     */
    private void initClick(){

    }

    /**
     * 先请求数据，使用get  网络编程
     */

    public void startConnection(String mUrl) {
        new Thread(() -> {
            try {
                URL url = new URL(mUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
                connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
                connection.connect();  //开始连接
                InputStream in = connection.getInputStream();
                String respondData = streamToString(in);               //处理输入流
                Message message = new Message();  //线程通信
                message.obj = respondData;
                mHandler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    /**
     * 处理输入流的方法
     */

    private String streamToString(InputStream in) {
        StringBuilder sb = new StringBuilder();
        String oneLine;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        try {
            while ((oneLine = bufferedReader.readLine()) != null) {
                sb.append(oneLine).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 解析JSON字段的方法
     */

    private void jsonDecode(String jsonDate){
        try {
            Log.d("LX", "jsonDecode: "+jsonDate);
            JSONArray jsonArray = new JSONArray(jsonDate);
            idData = new int[jsonArray.length()];           //给数组赋值
            locatedData = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {           //拆分出来id和地区
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                idData[i] = jsonObject1.getInt("id");
                locatedData[i] = jsonObject1.getString("name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 线程间通信的handler，新建一个内部类
     */

    private class myHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String responseData = msg.obj.toString();
            jsonDecode(responseData);
            locatedSetting_adapter = new LocatedSetting_Adapter(locatedData);
            mRvLocated.setAdapter(locatedSetting_adapter);
            mRvLocated.setLayoutManager(new GridLayoutManager(WeatherLocated_Activity.this,1));   //设置一溜下来
            mRvLocated.addItemDecoration(new DividerItemDecoration( WeatherLocated_Activity.this, DividerItemDecoration.VERTICAL));  //设置下划线
        }
    }
}