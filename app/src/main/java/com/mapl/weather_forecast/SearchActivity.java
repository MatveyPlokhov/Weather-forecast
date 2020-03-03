package com.mapl.weather_forecast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

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
import com.mapl.weather_forecast.adapter.CityDataClassSearchPage;
import com.mapl.weather_forecast.adapter.RecyclerViewAdapterSearchPage;
import com.mapl.weather_forecast.presenter.BottomSheetPresenter;
import com.mapl.weather_forecast.presenter.SearchActivityPresenter;
import com.mapl.weather_forecast.view.BottomSheetView;
import com.mapl.weather_forecast.view.SearchActivityView;
import com.yayandroid.locationmanager.LocationManager;
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;
import com.yayandroid.locationmanager.configuration.PermissionConfiguration;
import com.yayandroid.locationmanager.listener.LocationListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;


public class SearchActivity extends MvpAppCompatActivity
        implements BottomSheetView, SearchActivityView,
        OnMapReadyCallback, View.OnClickListener,
        LocationListener, Postman {

    @InjectPresenter
    BottomSheetPresenter bottomSheetPresenter;

    @InjectPresenter
    SearchActivityPresenter searchActivityPresenter;

    private RecyclerView recyclerView;

    final static String LOCATION_KEY = "LOCATION_KEY";
    final static String LAT_KEY = "LAT_KEY";
    final static String LON_KEY = "LON_KEY";

    private LocationManager locationManager;
    private MapView mapView;
    private GoogleMap googleMap;
    private View dialog;
    private TextInputEditText inputEditText;
    private MaterialCardView topBar;
    private FloatingActionButton fabDone, fabLocation;
    private BottomSheetBehavior bottomSheet;
    private SearchView searchView;
    private ImageView location, pointer;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setSupportActionBar(findViewById(R.id.toolbar));

        recyclerView = findViewById(R.id.recyclerViewCityList);

        dialog = LayoutInflater.from(SearchActivity.this).inflate(R.layout.textinputlayout_item, null);
        inputEditText = dialog.findViewById(R.id.textInputEditText);

        topBar = findViewById(R.id.topBar);
        fabLocation = findViewById(R.id.fabLocation);
        fabDone = findViewById(R.id.fabDone);
        mapView = findViewById(R.id.mapView);
        location = findViewById(R.id.locationImage);
        pointer = findViewById(R.id.pointerImageView);
        bottomSheet = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));

        mapView.onCreate(null);
        mapView.getMapAsync(this);
        bottomSheet.addBottomSheetCallback(bottomSheetCallback);
        topBar.setOnClickListener(this);
        fabLocation.setOnClickListener(this);
        fabDone.setOnClickListener(this);
    }

    @SuppressLint("CheckResult")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();

        Observable.create(observable)
                .debounce(250, TimeUnit.MILLISECONDS)
                .distinct()
                .subscribe(text -> searchActivityPresenter.textChanged((String) text));

        return super.onCreateOptionsMenu(menu);
    }

    ObservableOnSubscribe<Object> observable = new ObservableOnSubscribe<Object>() {
        @Override
        public void subscribe(ObservableEmitter<Object> emitter) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    emitter.onNext(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    emitter.onNext(newText);
                    return false;
                }
            });
        }
    };

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            bottomSheetPresenter.bottomSheetStateChanged(newState);
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            bottomSheetPresenter.bottomSheetSlide(slideOffset);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topBar:
                bottomSheetPresenter.topBarClick(bottomSheet.getState());
                break;
            case R.id.fabLocation:
                bottomSheetPresenter.fabLocationClick();
                break;
            case R.id.fabDone:
                bottomSheetPresenter.fabDoneClick();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
    }

    @Override
    public void onProcessTypeChanged(int processType) {

    }

    @Override
    public void onLocationChanged(Location location) {
        googleMap.animateCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15f));
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

    @Override
    public void setBottomSheetState(int state) {
        bottomSheet.setState(state);
    }

    @Override
    public void setLocation() {
        locationManager = new LocationManager.Builder(getApplicationContext())
                .activity(SearchActivity.this)
                .configuration(new LocationConfiguration.Builder()
                        .keepTracking(true)
                        .askForPermission(new PermissionConfiguration.Builder().build())
                        .useGooglePlayServices(new GooglePlayServicesConfiguration.Builder().build())
                        .build())
                .notify(SearchActivity.this)
                .build();
        locationManager.get();
    }

    @Override
    public void setDialog() {
        new MaterialAlertDialogBuilder(SearchActivity.this)
                .setView(dialog)
                .setTitle("Введите название:")
                .setPositiveButton("Ok", (dialogOk, which) -> getLocationInfo(
                        String.valueOf(inputEditText.getText()),
                        googleMap.getCameraPosition().target.latitude,
                        googleMap.getCameraPosition().target.longitude
                ))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void clearFocus() {
        searchView.clearFocus();
    }

    @Override
    public void bottomSheetExpanded() {
        location.setImageResource(R.drawable.arrow_down);
        pointer.animate().scaleX(1).scaleY(1).setDuration(300).start();
        fabLocation.animate().scaleX(1).scaleY(1).setDuration(300).start();
        fabDone.animate().scaleX(1).scaleY(1).setDuration(300).start();
        mapView.onResume();
    }

    @Override
    public void bottomSheetCollapsed() {
        location.setImageResource(R.drawable.ic_location);
        pointer.animate().scaleX(0).scaleY(0).start();
        fabLocation.animate().scaleX(0).scaleY(0).start();
        fabDone.animate().scaleX(0).scaleY(0).start();
        mapView.onPause();
    }

    @Override
    public void closeBottomSheet() {
        bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void closeSearchActivity() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        locationManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void setRecyclerView(ArrayList<CityDataClassSearchPage> arrayList) {
        runOnUiThread(() -> {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false);
            RecyclerViewAdapterSearchPage recyclerViewAdapter = new RecyclerViewAdapterSearchPage(arrayList, SearchActivity.this);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(recyclerViewAdapter);
        });
    }

    @Override
    public void onBackPressed() {
        bottomSheetPresenter.backPressed(bottomSheet.getState());
    }

    @Override
    public void getLocationInfo(String cityName, Double lat, Double lon) {
        Intent intent = new Intent();
        intent.putExtra(SearchActivity.LOCATION_KEY, cityName);
        intent.putExtra(SearchActivity.LAT_KEY, lat);
        intent.putExtra(SearchActivity.LON_KEY, lon);
        setResult(RESULT_OK, intent);
        finish();
    }
}