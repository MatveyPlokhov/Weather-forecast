package com.mapl.weather_forecast.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.mapl.weather_forecast.Postman;
import com.mapl.weather_forecast.R;

import java.util.ArrayList;

public class RecyclerViewAdapterSearchPage extends RecyclerView.Adapter<RecyclerViewAdapterSearchPage.ViewHolder> {
    private ArrayList<CityDataClassSearchPage> arrayList;
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
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        TextView cityName, cityAddress;
        MapView mapView;
        GoogleMap map;
        MaterialButton materialButton;
        View layout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView;
            cityName = itemView.findViewById(R.id.cityNameSP);
            cityAddress = itemView.findViewById(R.id.cityAddressSP);
            materialButton = itemView.findViewById(R.id.material_icon_button);
            mapView = itemView.findViewById(R.id.map);

            if (mapView != null) {
                mapView.onCreate(null);
                mapView.getMapAsync(this);
                setMapLocation();
            }
        }

        private void setMapLocation() {
            if (map == null) return;
            CityDataClassSearchPage data = (CityDataClassSearchPage) mapView.getTag();
            if (data == null) return;

            LatLng center = new LatLng(data.lat, data.lon);
            map.getUiSettings().setMapToolbarEnabled(false);
            map.getUiSettings().setAllGesturesEnabled(false);
            map.addMarker(new MarkerOptions().position(center));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 13f));
            mapView.onResume();
        }

        void bindView(final int position) {
            final CityDataClassSearchPage item = arrayList.get(position);
            mapView.setTag(item);
            cityAddress.setText(item.description);
            cityName.setText(item.city);

            materialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mapView.getVisibility() == View.GONE) {
                        mapView.setVisibility(View.VISIBLE);
                        materialButton.setIconResource(R.drawable.arrow_up);
                        setMapLocation();
                    } else {
                        mapView.onPause();
                        mapView.setVisibility(View.GONE);
                        materialButton.setIconResource(R.drawable.arrow_down);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Postman) activity).getLocationInfo(
                            item.city, item.lat, item.lon
                    );
                }
            });
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(activity);
            map = googleMap;
        }
    }
}