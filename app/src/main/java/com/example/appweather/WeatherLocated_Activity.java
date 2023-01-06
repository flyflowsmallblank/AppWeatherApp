package com.example.appweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
    private int[] idData;
    private String[] locatedData;
    private String[] weather_id_data;
    private RecyclerView mRvLocated;
    private LocatedSetting_Adapter locatedSetting_adapter;
    private Handler mHandler;
    private Toolbar mTbBar;
    private TextView mTvLocated;
    private int weather_name;        //所在地的名称
    private int position;            //所在地的id
    private StringBuilder mmUrl = new StringBuilder("http://guolin.tech/api/china");    //请求地址的网站

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_located);
        initView();
        startConnection("http://guolin.tech/api/china");  //刚进来时后就进行网络请求
        mHandler = new myHandler();
        findViewById(R.id.tb_located_head).setPadding(0,getStatusBarHeight(),0,0);  //设置标题栏目远离状态栏,和状态栏分离
        setWeatherLocated();            //设置中间的文字

    }
    /**
     * 初始化点击
     */
    private void initView(){
        mRvLocated = findViewById(R.id.rv_located);
        mTbBar = findViewById(R.id.tb_located_head);
        mTvLocated = findViewById(R.id.tb_tv_center);
    }
    /**
     * 初始化点击事件,需要进一步确定位置是哪里
     */
    private void initClick(LocatedSetting_Adapter adapter){
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(weather_id_data[0]!=null){
                    startIntent(position);
                    finish();
                }
                setPosition(position);
                mmUrl.append('/').append(idData[position]);
                startConnection(mmUrl.toString());
                Log.d("LX", "onItemClick: "+mmUrl);
            }
        });
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
                Looper.prepare();           //子线程中弹出就需要这上下两个方法。
                Toast.makeText(WeatherLocated_Activity.this, "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                Looper.loop();
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
            weather_id_data = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {           //拆分出来id和地区
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                idData[i] = jsonObject1.getInt("id");
                locatedData[i] = jsonObject1.getString("name");
                try{
                    weather_id_data[i] = jsonObject1.getString("weather_id");
                }catch (Exception e){
                    e.printStackTrace();
                }
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
            initClick(locatedSetting_adapter);
        }
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
     * 下面将写一个设置地区的显示文字,那个地区显示id叫tv_located
     */

    private void setWeatherLocated(){
        mTvLocated.setText("我爱你");
    }

    /**
     * 设置跳转，并且传递地址信息，传递最后的id
     */
    private void startIntent(int position){
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("weather_id",weather_id_data[position]);          //传递天气id
        intent.putExtra("name",locatedData[position]);                    //传递天气名称
        Log.d("LX", "startIntent中的weather——id: "+weather_id_data[position]);
        Log.d("LX", "startIntent中的name: "+locatedData[position]);
        startActivity(intent);
        finish();
    }

    /**
     * 设施定位那item的position的
     */

    private void setPosition(int position){
        this.position = position;
    }

}