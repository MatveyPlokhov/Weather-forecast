package com.mapl.weather_forecast;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapterSearchPage extends RecyclerView.Adapter<RecyclerViewAdapterSearchPage.ViewHolder> {
    private ArrayList<CityDataClassSearchPage> arrayList;
    private Activity activity;
    private Context context;

    RecyclerViewAdapterSearchPage(ArrayList<CityDataClassSearchPage> arrayList, Activity activity) {
        if (arrayList != null)
            this.arrayList = arrayList;
        else
            this.arrayList = new ArrayList<>();
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.card_city_in_search_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.cityName.setText(arrayList.get(position).city);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Postman) activity).getCityName(arrayList.get(position).city);
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
            cityName = itemView.findViewById(R.id.cityNameSP);
        }
    }
}
