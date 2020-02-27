package com.mapl.weather_forecast.Model;

import android.app.Activity;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.mapl.weather_forecast.R;
import com.yayandroid.locationmanager.LocationManager;
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;
import com.yayandroid.locationmanager.configuration.PermissionConfiguration;
import com.yayandroid.locationmanager.listener.LocationListener;

public class BottomSheetModel {
    /*public void getLocation() {
        LocationManager locationManager = new LocationManager.Builder(activity.getApplicationContext())
                .activity(this)
                .configuration(new LocationConfiguration.Builder()
                        .keepTracking(true)
                        .askForPermission(new PermissionConfiguration.Builder().build())
                        .useGooglePlayServices(new GooglePlayServicesConfiguration.Builder().build())
                        .build())
                .notify(new LocationListener() {
                    @Override
                    public void onProcessTypeChanged(int processType) {

                    }

                    @Override
                    public void onLocationChanged(Location location) {
                        center = new LatLng(location.getLatitude(), location.getLongitude());
                        mapView.getMapAsync(onMapReadyCallback);
                    }

                    @Override
                    public void onLocationFailed(int type) {

                    }

                    @Override
                    public void onPermissionGranted(boolean alreadyHadPermission) {

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                }).build();
        locationManager.get();
    }*/

    public MaterialAlertDialogBuilder getDialog(Activity activity, View dialog, TextInputEditText inputEditText) {
        return new MaterialAlertDialogBuilder(activity)
                .setView(dialog)
                .setTitle("Введите название:")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        /*((Postman) activity).getLocationInfo(
                                String.valueOf(inputEditText.getText()),
                                googleMap.getCameraPosition().target.latitude,
                                googleMap.getCameraPosition().target.longitude
                        );*/
                    }
                })
                .setNegativeButton("Cancel", null);
    }

    /*public void clearFocus(float slideOffset) {
        if (slideOffset > 0) searchView.clearFocus();
    }

    public void bottomSheetExpanded() {
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
    }

    public void bottomSheetCollapsed() {
        location.setImageResource(R.drawable.ic_location);
        pointer.animate().scaleX(0).scaleY(0).start();
        fabLocation.animate().scaleX(0).scaleY(0).start();
        fabDone.animate().scaleX(0).scaleY(0).start();
        if (!notRunBefore) mapView.onPause();
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
    }*/
}
