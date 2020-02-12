package com.mapl.weather_forecast.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.mapl.weather_forecast.database.dao.CurrentWeatherDao;
import com.mapl.weather_forecast.database.model.CurrentWeather;

@Database(entities = {CurrentWeather.class}, version = 1, exportSchema = false)
public abstract class CurrentWeatherDatabase extends RoomDatabase {
    public abstract CurrentWeatherDao getCurrentWeatherDao();
}