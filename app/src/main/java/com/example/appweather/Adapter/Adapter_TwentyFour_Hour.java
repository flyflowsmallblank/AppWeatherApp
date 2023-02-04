package com.example.appweather.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appweather.OnTimeHour;
import com.example.appweather.R;

import java.util.ArrayList;

public class Adapter_TwentyFour_Hour extends RecyclerView.Adapter<Adapter_TwentyFour_Hour.InnerHolder>{
    private ArrayList<OnTimeHour> arrayList;

    public Adapter_TwentyFour_Hour(ArrayList<OnTimeHour> arrayList){
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InnerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_hour,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        holder.mTvTime.setText(arrayList.get(position).getTime());
        holder.mTvTemp.setText(arrayList.get(position).getTemp());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class InnerHolder extends RecyclerView.ViewHolder{
        TextView mTvTime;
        TextView mTvTemp;
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mTvTime = itemView.findViewById(R.id.item_tv_onTime_hour);
            mTvTemp = itemView.findViewById(R.id.item_tv_onTime_temp);
        }
    }
}
