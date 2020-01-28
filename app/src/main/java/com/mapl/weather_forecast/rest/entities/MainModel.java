package com.mapl.weather_forecast.rest.entities;

import com.google.gson.annotations.SerializedName;

public class MainModel {
    @SerializedName("temp") public float temp;
    @SerializedName("feels_like") public float feels_like;
    @SerializedName("pressure") public int pressure;
    @SerializedName("humidity") public int humidity;
}