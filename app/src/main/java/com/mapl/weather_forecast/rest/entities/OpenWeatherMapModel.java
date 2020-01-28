package com.mapl.weather_forecast.rest.entities;

import com.google.gson.annotations.SerializedName;

public class OpenWeatherMapModel {
    @SerializedName("weather") public WeatherModel[] weather;
    @SerializedName("main") public MainModel main;
    @SerializedName("wind") public WindModel wind;
    @SerializedName("clouds") public CloudsModel clouds;
    @SerializedName("dt") public long dt;
    @SerializedName("sys") public  SysModel sys;
}
