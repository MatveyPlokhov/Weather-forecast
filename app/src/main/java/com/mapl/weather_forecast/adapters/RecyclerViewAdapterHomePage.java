package com.mapl.weather_forecast.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mapl.weather_forecast.Postman;
import com.mapl.weather_forecast.R;

import java.util.ArrayList;

public class RecyclerViewAdapterHomePage extends RecyclerView.Adapter<RecyclerViewAdapterHomePage.ViewHolder> {
    private ArrayList<CityDataClassHomePage> arrayList;
    private Activity activity;

    public RecyclerViewAdapterHomePage(ArrayList<CityDataClassHomePage> arrayList, Activity activity) {
        if (arrayList != null)
            this.arrayList = arrayList;
        else
            this.arrayList = new ArrayList<>();
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_city_in_home_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String cityName = arrayList.get(position).city;
        final Double lat = arrayList.get(position).lat;
        final Double lon = arrayList.get(position).lon;
        holder.cityName.setText(cityName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Postman) activity).getCityInfo(cityName, lat, lon);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView cityName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.cityNameHP);
        }
    }
}