package com.mapl.weather_forecast.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mapl.weather_forecast.Postman;
import com.mapl.weather_forecast.R;
import com.mapl.weather_forecast.WeatherForecastFragment;

import java.util.ArrayList;
import java.util.Objects;

public class RecyclerViewAdapterSearchPage extends RecyclerView.Adapter<RecyclerViewAdapterSearchPage.ViewHolder> implements Filterable {
    private ArrayList<CityDataClassSearchPage> arrayList;
    private ArrayList<CityDataClassSearchPage> arrayListFull;
    private Activity activity;
    private Context context;

    public RecyclerViewAdapterSearchPage(ArrayList<CityDataClassSearchPage> arrayList, Activity activity) {
        if (arrayList != null)
            this.arrayList = arrayList;
        else
            this.arrayList = new ArrayList<>();
        arrayListFull = new ArrayList<>(Objects.requireNonNull(arrayList));
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
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

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<CityDataClassSearchPage> filteredArrayList = new ArrayList<>();
            if (constraint.length() == 0) {
                filteredArrayList.addAll(arrayListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (CityDataClassSearchPage cityDataClassSearchPage : arrayListFull) {
                    if (cityDataClassSearchPage.city.toLowerCase().contains(filterPattern)) {
                        filteredArrayList.add(cityDataClassSearchPage);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredArrayList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList.clear();
            arrayList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView cityName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.cityNameSP);
        }
    }
}
