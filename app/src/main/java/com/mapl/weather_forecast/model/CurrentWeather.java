package com.mapl.weather_forecast.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {CurrentWeather.LAT, CurrentWeather.LON}, unique = true)})

public class CurrentWeather {

    public final static String ID = "id";
    public final static String LOCATION = "location";
    public final static String LAT = "lat";
    public final static String LON = "lon";
    public final static String WEATHER_ID = "weather_id";
    public final static String SUNRISE = "sunrise";
    public final static String SUNSET = "sunset";
    public final static String DESCRIPTION = "description";
    public final static String TEMP = "temp";
    public final static String FEELS_LIKE = "feels_like";
    public final static String PRESSURE = "pressure";
    public final static String HUMIDITY = "humidity";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    public long id;

    @ColumnInfo(name = LOCATION)
    public String location;

    @ColumnInfo(name = LAT)
    public Double latitude;

    @ColumnInfo(name = LON)
    public Double longitude;

    @ColumnInfo(name = WEATHER_ID)
    public long weatherId;

    @ColumnInfo(name = SUNRISE)
    public long sunrise;

    @ColumnInfo(name = SUNSET)
    public long sunset;

    @ColumnInfo(name = DESCRIPTION)
    public String description;

    @ColumnInfo(name = TEMP)
    public int temperature;

    @ColumnInfo(name = FEELS_LIKE)
    public int feelsLike;

    @ColumnInfo(name = PRESSURE)
    public int pressure;

    @ColumnInfo(name = HUMIDITY)
    public int humidity;
}