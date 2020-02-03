package com.mapl.weather_forecast.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.mapl.weather_forecast.Postman;
import com.mapl.weather_forecast.R;
import com.mapl.weather_forecast.SearchActivity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class RecyclerViewAdapterSearchPage extends RecyclerView.Adapter<RecyclerViewAdapterSearchPage.ViewHolder> {
    private ArrayList<CityDataClassSearchPage> arrayList;
    private LatLng center;
    private Activity activity;

    public RecyclerViewAdapterSearchPage(ArrayList<CityDataClassSearchPage> arrayList, final Activity activity) {
        this.arrayList = arrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_city, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String locationInfo = arrayList.get(position).description;
        center = new LatLng(arrayList.get(position).lat, arrayList.get(position).lon);
        holder.cityName.setText(arrayList.get(position).city);
        holder.cityAddress.setText(locationInfo);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInHistory(arrayList.get(position).city);
                ((Postman) activity).getLocationInfo(
                        arrayList.get(position).city,
                        arrayList.get(position).lat,
                        arrayList.get(position).lon
                );
            }
        });
    }

    private void addInHistory(String location) {
        final SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> set = preferences.getStringSet(SearchActivity.KEY_HISTORY, null);

        if (set == null) {
            set = new LinkedHashSet<>();
            set.add(location);
            editor.putStringSet(SearchActivity.KEY_HISTORY, set).apply();
        } else {
            set.add(location);
            preferences.edit().remove(SearchActivity.KEY_HISTORY).apply();
            editor.putStringSet(SearchActivity.KEY_HISTORY, set).apply();
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        TextView cityName;
        TextView cityAddress;
        MapView mapView;
        MaterialButton materialButton;
        OnMapReadyCallback onMapReadyCallback;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            onMapReadyCallback = this;
            cityName = itemView.findViewById(R.id.cityNameSP);
            cityAddress = itemView.findViewById(R.id.cityAddressSP);
            materialButton = itemView.findViewById(R.id.material_icon_button);
            mapView = itemView.findViewById(R.id.map);
            mapView.onCreate(null);
            materialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mapView.getVisibility() == View.GONE) {
                        mapView.setVisibility(View.VISIBLE);
                        materialButton.setIconResource(R.drawable.arrow_up);
                        if (mapView != null) {
                            mapView.getMapAsync(onMapReadyCallback);
                        }
                    } else {
                        mapView.onPause();
                        mapView.setVisibility(View.GONE);
                        materialButton.setIconResource(R.drawable.arrow_down);
                    }

                }
            });
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mapView.onLowMemory();
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setAllGesturesEnabled(false);
            googleMap.addMarker(new MarkerOptions().position(center));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 13f));
            mapView.onResume();
        }
    }
}