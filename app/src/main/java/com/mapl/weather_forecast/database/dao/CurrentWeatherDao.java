package com.mapl.weather_forecast.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.OnConflictStrategy;

import com.mapl.weather_forecast.database.model.CurrentWeather;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface CurrentWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCurrentWeather(CurrentWeather currentWeather);

    @Update
    void updateCurrentWeather(CurrentWeather currentWeather);

    @Delete
    void deleteCurrentWeather(CurrentWeather currentWeather);

    @Query("DELETE FROM CurrentWeather WHERE id = :id")
    void deteleCurrentWeatherById(long id);

    @Query("SELECT * FROM CurrentWeather")
    Single<List<CurrentWeather>> getWeatherListSingle();

    @Query("SELECT * FROM CurrentWeather")
    List<CurrentWeather> getWeatherList();

    @Query("SELECT * FROM CurrentWeather WHERE id = :id")
    CurrentWeather getCurrentWeatherById(long id);

    @Query("SELECT COUNT() FROM CurrentWeather")
    long getCount();
}
