package com.mapl.weather_forecast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mapl.weather_forecast.services.WeatherForecastService;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements Postman {
    public static final String BROADCAST_ACTION_WEATHER = "com.mapl.weather_forecast.services.weatherdatafinished";
    FragmentManager fragmentManager;
    private CityListFragment cityListFragment;
    private WeatherForecastFragment weatherForecastFragment;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefaultNavigationView);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initNavigationView();
        initView();
        initNotificationChannel();
        initFragments();
        clickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(weatherDataFinishedReceiver, new IntentFilter(BROADCAST_ACTION_WEATHER));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(weatherDataFinishedReceiver);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initNavigationView() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.settingsItem:
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this
                , drawerLayout
                , toolbar
                , R.string.open
                , R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initView() {
        floatingActionButton = findViewById(R.id.fabAddLocation);
        floatingActionButton.setColorFilter(Color.rgb(255, 255, 255));
    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel("2", "Weather Forecast", importance);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    private void initFragments() {
        fragmentManager = getSupportFragmentManager();

        cityListFragment = (CityListFragment) fragmentManager.findFragmentById(R.id.fragmentSelectedListOfCities);
        weatherForecastFragment = (WeatherForecastFragment) fragmentManager.findFragmentById(R.id.fragmentWeatherForecast);

        /*fragmentManager.beginTransaction()
                .hide(weatherForecastFragment)
                .commit();*/
    }

    private void clickListeners() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityListFragment.searchActivityCall();
            }
        });
    }

    @Override
    public void getCityInfo(String location, Double lat, Double lon) {
        WeatherForecastService.startWeatherForecastService(MainActivity.this, location, lat, lon);
    }

    private BroadcastReceiver weatherDataFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String location = intent.getStringExtra(WeatherForecastService.EXTRA_RESULT_LOCATION);
            HashMap<String,String> dataDetails = (HashMap<String, String>)intent.getSerializableExtra(WeatherForecastService.EXTRA_RESULT_HASH_MAP);
            long[] arrayForIcon = intent.getLongArrayExtra(WeatherForecastService.EXTRA_RESULT_LONG_ARRAY);
            weatherForecastFragment.updateWeatherData(location, dataDetails, arrayForIcon);
        }
    };
}