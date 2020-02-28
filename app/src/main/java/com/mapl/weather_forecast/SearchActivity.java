package com.mapl.weather_forecast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.mapl.weather_forecast.loader.CityDataLoader;
import com.mapl.weather_forecast.presenter.BottomSheetPresenter;
import com.mapl.weather_forecast.view.BottomSheetView;
import com.yayandroid.locationmanager.LocationManager;
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;
import com.yayandroid.locationmanager.configuration.PermissionConfiguration;
import com.yayandroid.locationmanager.listener.LocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;


public class SearchActivity extends MvpAppCompatActivity
        implements BottomSheetView, OnMapReadyCallback, View.OnClickListener, LocationListener, Postman {

    @InjectPresenter
    BottomSheetPresenter bottomSheetPresenter;

    private ArrayList<CityDataClassSearchPage> arrayList;
    private RecyclerView recyclerView;
    private DataLoader dataLoader;

    final static String LOCATION_KEY = "LOCATION_KEY";
    final static String LAT_KEY = "LAT_KEY";
    final static String LON_KEY = "LON_KEY";

    private LocationManager locationManager;
    private MapView mapView;
    private GoogleMap googleMap;
    private View dialog;
    private TextInputEditText inputEditText;
    private FloatingActionButton fabDone, fabLocation;
    private BottomSheetBehavior bottomSheet;
    private SearchView searchView;
    private ImageView location, pointer;

    private boolean notRunBefore = true;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recyclerViewCityList);
        mapView = findViewById(R.id.bigMapView);

        dialog = LayoutInflater.from(SearchActivity.this).inflate(R.layout.textinputlayout_item, null);
        inputEditText = dialog.findViewById(R.id.textInputEditText);

        MaterialCardView topBar = findViewById(R.id.topBar);
        fabLocation = findViewById(R.id.fabLocation);
        fabDone = findViewById(R.id.fabDone);
        mapView = findViewById(R.id.bigMapView);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        MenuItem microItem = menu.findItem(R.id.action_micro);
        searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (dataLoader != null)
                    dataLoader.cancel(false);
                dataLoader = new DataLoader();
                dataLoader.execute(newText);
                return false;
            }
        });

        microItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getApplicationContext(), "Скоро добавлю", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        return true;
    }


    private ArrayList<CityDataClassSearchPage> cityList(JSONObject jsonObject) {
        ArrayList<CityDataClassSearchPage> arrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject
                    .getJSONObject("response")
                    .getJSONObject("GeoObjectCollection")
                    .getJSONArray("featureMember");
            for (int i = 0; i < jsonArray.length(); i++) {
                arrayList.add(new CityDataClassSearchPage(
                        jsonArray.getJSONObject(i)
                                .getJSONObject("GeoObject")
                                .getString("name"),
                        jsonArray.getJSONObject(i)
                                .getJSONObject("GeoObject")
                                .getString("description"),
                        jsonArray.getJSONObject(i)
                                .getJSONObject("GeoObject")
                                .getJSONObject("Point")
                                .getString("pos")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
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

    @SuppressLint("StaticFieldLeak")
    private class DataLoader extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            JSONObject jsonObject = CityDataLoader.getJSONData(strings[0]);
            if (jsonObject != null)
                arrayList = cityList(jsonObject);
            else
                arrayList = new ArrayList<>();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchActivity.this);
            RecyclerViewAdapterSearchPage recyclerViewAdapter = new RecyclerViewAdapterSearchPage(arrayList, SearchActivity.this);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
                @Override
                public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {

                }
            });
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }


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
                break;
            case R.id.fabDone:
                new MaterialAlertDialogBuilder(SearchActivity.this)
                        .setView(dialog)
                        .setTitle("Введите название:")
                        .setPositiveButton("Ok", (dialogOk, which) -> {
                    /*((Postman) activity).getLocationInfo(
                            String.valueOf(inputEditText.getText()),
                            googleMap.getCameraPosition().target.latitude,
                            googleMap.getCameraPosition().target.longitude
                    );*/
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
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
    public void clearFocus() {
        searchView.clearFocus();
    }

    @Override
    public void onBackPressed() {
        bottomSheetPresenter.backPressed(bottomSheet.getState());
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
}