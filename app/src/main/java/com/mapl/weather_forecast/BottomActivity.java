package com.mapl.weather_forecast;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import mumayank.com.airlocationlibrary.AirLocation;

public class BottomActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final Handler handler = new Handler();
    private AirLocation airLocation;
    private Activity activity;
    private MapView mapView;
    private FloatingActionButton fabDone, fabLocation;
    private OnMapReadyCallback onMapReadyCallback;
    private BottomSheetBehavior bottomSheet;
    private SearchView searchView;
    private LatLng center;
    private MaterialCardView topBar;
    private ImageView location;

    private boolean notRunBefore = true;

    BottomActivity(Activity activity, SearchView searchView) {
        this.activity = activity;
        this.searchView = searchView;
        onMapReadyCallback = this;
        initView();
        listeners();
    }

    private void initView() {
        bottomSheet = BottomSheetBehavior.from(activity.findViewById(R.id.bottom_sheet));
        mapView = activity.findViewById(R.id.bigMapView);
        fabLocation = activity.findViewById(R.id.my_location);
        fabDone = activity.findViewById(R.id.location_done);
        topBar = activity.findViewById(R.id.topBar);
        location = activity.findViewById(R.id.locationImage);

        fabLocation.setColorFilter(Color.rgb(255, 255, 255));
        fabDone.setColorFilter(Color.rgb(255, 255, 255));
        fabLocation.animate().scaleX(0).scaleY(0).start();
        fabDone.animate().scaleX(0).scaleY(0).start();
    }

    private void listeners() {
        bottomSheet.addBottomSheetCallback(bottomSheetCallback);

        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                airLocation = new AirLocation(activity, true, true, new AirLocation.Callbacks() {
                    @Override
                    public void onSuccess(@NonNull Location location) {
                        center = new LatLng(location.getLatitude(), location.getLongitude());
                        mapView.getMapAsync(onMapReadyCallback);
                    }

                    @Override
                    public void onFailed(@NonNull AirLocation.LocationFailedEnum locationFailedEnum) {

                    }
                });
            }
        });

        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        topBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheet.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                else if (bottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
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
        if (center != null) {
            googleMap.addMarker(new MarkerOptions().position(center));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 13f));
        }
        mapView.onResume();
    }

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                if (notRunBefore) {
                    notRunBefore = false;
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
                } else mapView.onResume();
                location.setImageResource(R.drawable.arrow_down);
                fabLocation.animate().scaleX(1).scaleY(1).setDuration(300).start();
                fabDone.animate().scaleX(1).scaleY(1).setDuration(300).start();
            } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                location.setImageResource(R.drawable.ic_location);
                fabLocation.animate().scaleX(0).scaleY(0).start();
                fabDone.animate().scaleX(0).scaleY(0).start();
                if (!notRunBefore) mapView.onPause();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        airLocation.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

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
