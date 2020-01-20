package com.mapl.weather_forecast;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BottomActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final Handler handler = new Handler();
    private Activity activity;
    private MapView mapView;
    private FloatingActionButton fabDone, fabLocation;
    private OnMapReadyCallback onMapReadyCallback;
    private BottomSheetBehavior bottomSheet;
    private SearchView searchView;

    BottomActivity(Activity activity, SearchView searchView) {
        this.activity = activity;
        this.searchView = searchView;
        onMapReadyCallback = this;
        initView();
        listeners();
        create();
    }

    private void initView() {
        bottomSheet = BottomSheetBehavior.from(activity.findViewById(R.id.bottom_sheet));
        mapView = activity.findViewById(R.id.bigMapView);
        fabLocation = activity.findViewById(R.id.my_location);
        fabDone = activity.findViewById(R.id.location_done);
        fabLocation.setColorFilter(Color.rgb(255, 255, 255));
        fabDone.setColorFilter(Color.rgb(255, 255, 255));
        fabLocation.animate().scaleX(0).scaleY(0).start();
        fabDone.animate().scaleX(0).scaleY(0).start();
    }

    private void create() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mapView.onCreate(null);
                        mapView.getMapAsync(onMapReadyCallback);
                    }
                });
            }
        }).start();
    }

    private void listeners() {
        bottomSheet.addBottomSheetCallback(bottomSheetCallback);

        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        mapView.onResume();
    }

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                fabLocation.animate().scaleX(1).scaleY(1).setDuration(300).start();
                fabDone.animate().scaleX(1).scaleY(1).setDuration(300).start();
                mapView.onResume();
            } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                fabLocation.animate().scaleX(0).scaleY(0).start();
                fabDone.animate().scaleX(0).scaleY(0).start();
                mapView.onPause();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            if (slideOffset > 0) {
                searchView.clearFocus();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}