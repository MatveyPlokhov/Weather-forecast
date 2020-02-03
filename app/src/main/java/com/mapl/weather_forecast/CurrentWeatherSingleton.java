package com.mapl.weather_forecast;

import android.app.Application;

import androidx.room.Room;

import com.mapl.weather_forecast.dao.CurrentWeatherDao;
import com.mapl.weather_forecast.database.CurrentWeatherDatabase;

public class CurrentWeatherSingleton extends Application {
    private static CurrentWeatherSingleton instance;
    private CurrentWeatherDatabase database;

    public static CurrentWeatherSingleton getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, CurrentWeatherDatabase.class,
                "current_weather").build();
    }

    public CurrentWeatherDao getCurrentWeatherDao() {
        return database.getCurrentWeatherDao();
    }
}
