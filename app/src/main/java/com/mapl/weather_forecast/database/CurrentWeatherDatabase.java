package com.mapl.weather_forecast.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.mapl.weather_forecast.dao.CurrentWeatherDao;
import com.mapl.weather_forecast.model.CurrentWeather;

@Database(entities = {CurrentWeather.class}, version = 1)
public abstract class CurrentWeatherDatabase extends RoomDatabase {
    public abstract CurrentWeatherDao getCurrentWeatherDao();
}