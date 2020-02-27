package com.mapl.weather_forecast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.mapl.weather_forecast.adapter.ViewPagerAdapter;
import com.mapl.weather_forecast.database.model.CurrentWeather;
import com.mapl.weather_forecast.service.WeatherForecastService;
import com.yayandroid.locationmanager.LocationManager;
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;
import com.yayandroid.locationmanager.configuration.PermissionConfiguration;
import com.yayandroid.locationmanager.listener.LocationListener;

import java.util.ArrayList;
import java.util.List;

public class WeatherNearMeFragment extends Fragment {
    public static final String BROADCAST_ACTION_WEATHER_MY_LOCATION = "com.mapl.weather_forecast.services.weathermylocationfinished";
    private LocationManager locationManager;
    private ViewPager2 viewPager;
    private Activity activity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_weather_near_me, container, false);
        initView(root);
        location();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.registerReceiver(weatherDataFinishedReceiver,
                new IntentFilter(BROADCAST_ACTION_WEATHER_MY_LOCATION));
    }

    @Override
    public void onStop() {
        activity.unregisterReceiver(weatherDataFinishedReceiver);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        locationManager.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        locationManager.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        locationManager.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        locationManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void location() {
        locationManager = new LocationManager.Builder(activity.getApplicationContext())
                .fragment(this)
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
                        List<CurrentWeather> list = new ArrayList<>();
                        CurrentWeather currentWeather = new CurrentWeather();
                        currentWeather.location = "My location";
                        currentWeather.latitude = location.getLatitude();
                        currentWeather.longitude = location.getLongitude();
                        list.add(currentWeather);
                        WeatherForecastService.startWeatherForecastService(getContext(), list, false);
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
                })
                .build();
        locationManager.get();
    }

    private void initView(View root) {
        viewPager = root.findViewById(R.id.weatherNearMeViewPager);
    }

    private BroadcastReceiver weatherDataFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(WeatherForecastService.EXTRA_RESULT);
            List<CurrentWeather> list = (List<CurrentWeather>) intent.getSerializableExtra(WeatherForecastService.EXTRA_LIST_SEND);
            if (result != null) {
                if (result.equals(WeatherForecastService.RESULT_OK)) {
                    setViewPagerAdapter(list);
                } else if (result.equals(WeatherForecastService.CONNECTION_ERROR)) {
                    //Вывожу ошибку
                }
            }
        }

        private void setViewPagerAdapter(List<CurrentWeather> list) {
            ViewPagerAdapter adapter = ViewPagerAdapter.getInstance(activity, list);
            adapter.refreshData(list);
            viewPager.setAdapter(adapter);
            viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
            viewPager.setClipToPadding(false);
            viewPager.setClipChildren(false);
            viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        }
    };
}