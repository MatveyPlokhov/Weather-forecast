package com.mapl.weather_forecast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import mumayank.com.airlocationlibrary.AirLocation;

public class BottomActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final Handler handler = new Handler();
    private Activity activity;
    private MapView mapView;
    private GoogleMap googleMap;
    private FloatingActionButton fabDone, fabLocation;
    private OnMapReadyCallback onMapReadyCallback;
    private BottomSheetBehavior bottomSheet;
    private SearchView searchView;
    private LatLng center;
    private MaterialCardView topBar;
    private ImageView location, pointer;

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
        pointer = activity.findViewById(R.id.pointerImageView);

        pointer.animate().scaleX(0).scaleY(0).start();
        fabLocation.animate().scaleX(0).scaleY(0).start();
        fabDone.animate().scaleX(0).scaleY(0).start();
    }

    private void listeners() {
        bottomSheet.addBottomSheetCallback(bottomSheetCallback);

        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AirLocation(activity, true, true, new AirLocation.Callbacks() {
                    @Override
                    public void onSuccess(@NonNull Location location) {
                        center = new LatLng(location.getLatitude(), location.getLongitude());
                        mapView.getMapAsync(onMapReadyCallback);
                    }

                    @Override
                    public void onFailed(@NonNull AirLocation.LocationFailedEnum locationFailedEnum) {
                        //ошибка в предоставлении разрешения/поиска локации
                    }
                });
            }
        });

        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("InflateParams")
                View view = LayoutInflater.from(activity).inflate(R.layout.textinputlayout_item, null);
                final TextInputEditText inputEditText = view.findViewById(R.id.textInputEditText);
                new MaterialAlertDialogBuilder(activity)
                        .setView(view)
                        .setTitle("Введите название:")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((Postman) activity).getLocationInfo(
                                        String.valueOf(inputEditText.getText()),
                                        googleMap.getCameraPosition().target.latitude,
                                        googleMap.getCameraPosition().target.longitude
                                );
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                System.out.println(googleMap.getCameraPosition().target);
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
    public void onMapReady(final GoogleMap googleMap) {
        if (notRunBefore) {
            notRunBefore = false;
            this.googleMap = googleMap;
        }
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        if (center != null) googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 15f));
        mapView.onResume();
    }

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                if (notRunBefore) {
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
                pointer.animate().scaleX(1).scaleY(1).setDuration(300).start();
                fabLocation.animate().scaleX(1).scaleY(1).setDuration(300).start();
                fabDone.animate().scaleX(1).scaleY(1).setDuration(300).start();
            } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                location.setImageResource(R.drawable.ic_location);
                pointer.animate().scaleX(0).scaleY(0).start();
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
}
