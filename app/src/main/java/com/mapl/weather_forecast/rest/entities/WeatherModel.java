package com.mapl.weather_forecast.rest.entities;

import com.google.gson.annotations.SerializedName;

public class WeatherModel {
    @SerializedName("id") public int id;
    @SerializedName("description") public  String description;
}
