package com.mapl.weather_forecast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements Postman {
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
                switch (menuItem.getItemId()){
                    case  R.id.settingsItem:
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
        floatingActionButton = findViewById(R.id.fabAddCity);
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void getCityInfo(String cityName, Double lat, Double lon) {
        weatherForecastFragment.updateWeatherData(cityName, lat, lon);
    }
}