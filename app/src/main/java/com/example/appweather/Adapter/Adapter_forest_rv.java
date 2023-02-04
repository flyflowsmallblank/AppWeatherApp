package com.example.appweather.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appweather.DayInformation;
import com.example.appweather.R;

import java.util.ArrayList;

public class Adapter_forest_rv extends RecyclerView.Adapter<Adapter_forest_rv.InnerHolder>{
    private ArrayList<DayInformation> data;
    private int size;

    //构造的时候就将我们的数据传进来，不然就报错，同时传入想要返回的数量
    public Adapter_forest_rv(ArrayList<DayInformation> data,int size){
        this.data = data;
        if(size<data.size()){
            this.size = size;
        }else{
            this.size = data.size();
        }
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InnerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        holder.mTvTime.setText(data.get(position).getDay());
        holder.mTvWeatherText.setText(data.get(position).getForestWeatherText());
        holder.mTvTempMax.setText(data.get(position).getTempMax());
        holder.mTvTempMin.setText(data.get(position).getTempMin());
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public static class InnerHolder extends RecyclerView.ViewHolder {
        TextView mTvTime;
        TextView mTvWeatherText;
        TextView mTvTempMax;
        TextView mTvTempMin;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mTvTime = itemView.findViewById(R.id.tv_time);
            mTvWeatherText = itemView.findViewById(R.id.tv_forest_weather_text);
            mTvTempMax = itemView.findViewById(R.id.tv_tmp_max);
            mTvTempMin = itemView.findViewById(R.id.tv_tmp_min);
        }
    }
}
