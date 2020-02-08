package com.mapl.weather_forecast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.snackbar.Snackbar;
import com.mapl.weather_forecast.adapter.ViewPagerAdapter;
import com.mapl.weather_forecast.database.model.CurrentWeather;
import com.mapl.weather_forecast.rest.OpenWeatherMap;
import com.mapl.weather_forecast.rest.entities.OpenWeatherMapModel;
import com.microsoft.appcenter.Flags;
import com.microsoft.appcenter.analytics.Analytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationWeatherActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private LocationManager locManager = null;
    private LocListener locListener = null;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_weather);

        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        initToolbar();
        initView();
        getLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (locListener != null) locManager.removeUpdates(locListener);
        super.onPause();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarInLocationActivity);
        toolbar.setTitle(getResources().getString(R.string.weather_near_me));
        setSupportActionBar(toolbar);
    }

    private void initView() {
        viewPager = findViewById(R.id.weatherInLocationActivity);
        coordinatorLayout = findViewById(R.id.coordinatorLayoutInLocationActivity);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        } else {
            if (locListener == null) locListener = new LocListener();
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    3000L, 1.0F, locListener);

            @SuppressLint("MissingPermission") final Location loc = locManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (loc != null) {
                setWeather(loc);
            }
        }
    }

    private void setWeather(final Location loc) {
        OpenWeatherMap.getSingleton().getAPI().loadWeather(
                String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()),
                "8b872d9c30ed844f1fa73c7172a8313f", "metric", Locale.getDefault().getLanguage())
                .enqueue(new Callback<OpenWeatherMapModel>() {
                    @Override
                    public void onResponse(Call<OpenWeatherMapModel> call, Response<OpenWeatherMapModel> response) {
                        List<CurrentWeather> list = new ArrayList<>();
                        CurrentWeather currentWeather = new CurrentWeather();
                        OpenWeatherMapModel model = response.body();
                        if (response.isSuccessful() && model != null) {
                            currentWeather.location = " ";
                            currentWeather.latitude = loc.getLatitude();
                            currentWeather.longitude = loc.getLongitude();
                            currentWeather.weatherId = model.weather[0].id;
                            currentWeather.sunrise = model.sys.sunrise * 1000;
                            currentWeather.sunset = model.sys.sunset * 1000;
                            currentWeather.description = model.weather[0].description;
                            currentWeather.temperature = Math.round(model.main.temp);
                            currentWeather.feelsLike = Math.round(model.main.feels_like);
                            currentWeather.pressure = model.main.pressure;
                            currentWeather.humidity = model.main.humidity;
                            list.add(currentWeather);
                            ViewPagerAdapter adapter = ViewPagerAdapter.getInstance(LocationWeatherActivity.this, list);
                            adapter.refreshData(list);
                            viewPager.setAdapter(adapter);
                            viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
                            viewPager.setClipToPadding(false);
                            viewPager.setClipChildren(false);
                            viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
                        } else {
                            HashMap<String, String> connectionError = new HashMap<>();
                            connectionError.put("Location", currentWeather.location +
                                    " (" + currentWeather.latitude + ", " + currentWeather.longitude + ")");
                            connectionError.put(String.valueOf(response.code()), response.message());
                            Analytics.trackEvent("Error in getting weather data",
                                    connectionError, Flags.CRITICAL);
                            Snackbar.make(coordinatorLayout, response.code() + " " + response.message(),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OpenWeatherMapModel> call, Throwable t) {
                        Snackbar.make(coordinatorLayout, getResources().getString(R.string.weather_data_false),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            boolean permissionsGranted = (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    && (grantResults[0] == PackageManager.PERMISSION_GRANTED);
            if (permissionsGranted) recreate();
        }
    }

    private final class LocListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Log.i("Location", "локация изменилась: " + location.toString());
            setWeather(location);
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
    }
}
