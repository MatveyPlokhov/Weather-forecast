package com.mapl.weather_forecast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements Postman {
    FragmentManager fragmentManager;
    CityListFragment cityListFragment;
    WeatherForecastFragment weatherForecastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initFragments();
    }

    private void initView() {
    }

    private void initFragments() {
        fragmentManager = getSupportFragmentManager();

        cityListFragment = (CityListFragment) fragmentManager.findFragmentById(R.id.fragmentSelectedListOfCities);
        weatherForecastFragment = (WeatherForecastFragment) fragmentManager.findFragmentById(R.id.fragmentWeatherForecast);

        fragmentManager.beginTransaction()
                .hide(weatherForecastFragment)
                .commit();
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
    public void getCityName(String cityName) {
        weatherForecastFragment.updateWeatherData(cityName);
    }
}