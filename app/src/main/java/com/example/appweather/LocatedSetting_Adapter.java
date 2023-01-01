package com.example.appweather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LocatedSetting_Adapter extends RecyclerView.Adapter<LocatedSetting_Adapter.InnerHolder>{
    public  String[] LocatedData;

    public LocatedSetting_Adapter(String[] LocatedData) {
        this.LocatedData = LocatedData;
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InnerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_located,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        holder.mTvLocated.setText(LocatedData[position].toString());
    }

    @Override
    public int getItemCount() {
        return LocatedData.length;
    }

    public static class InnerHolder extends RecyclerView.ViewHolder {
        public TextView mTvLocated;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mTvLocated = itemView.findViewById(R.id.item_tv_located);
        }
    }
}
