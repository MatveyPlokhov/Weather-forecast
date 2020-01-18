package com.mapl.weather_forecast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements Postman {
    FragmentManager fragmentManager;
    private CityListFragment cityListFragment;
    private WeatherForecastFragment weatherForecastFragment;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialCardView phoneDataCardView;

    FloatingActionButton floatingActionButton;

    Sensor sensorAT, sensorRH;
    SensorManager sensorManager;
    TextView textViewAT, textViewRH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefaultNavigationView);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initNavigationView();
        initView();
        initFragments();
        clickListeners();
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
        infoPhoneData();
    }

    private void infoPhoneData() {
        textViewAT = findViewById(R.id.textViewAT);
        textViewRH = findViewById(R.id.textViewRH);
        phoneDataCardView = findViewById(R.id.phoneDataCardView);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAT = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorRH = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

        if (sensorAT == null || sensorRH == null)
            phoneDataCardView.setVisibility(View.GONE);
        else
            phoneDataCardView.setVisibility(View.GONE);
    }

    private void initFragments() {
        fragmentManager = getSupportFragmentManager();

        cityListFragment = (CityListFragment) fragmentManager.findFragmentById(R.id.fragmentSelectedListOfCities);
        weatherForecastFragment = (WeatherForecastFragment) fragmentManager.findFragmentById(R.id.fragmentWeatherForecast);

        fragmentManager.beginTransaction()
                .hide(weatherForecastFragment)
                .commit();
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
    public void getCityInfo(String cityName, Double lat, Double lon) {
        weatherForecastFragment.updateWeatherData(cityName, lat, lon);
    }

    //////////

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listenerAT, sensorAT, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listenerRH, sensorRH, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listenerAT, sensorAT);
        sensorManager.unregisterListener(listenerRH, sensorRH);
    }

    SensorEventListener listenerAT = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append((int)event.values[0]);
            textViewAT.setText(stringBuilder);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    final SensorEventListener listenerRH = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append((int)event.values[0]);
            textViewRH.setText(stringBuilder);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
}