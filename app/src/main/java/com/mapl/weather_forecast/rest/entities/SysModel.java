package com.mapl.weather_forecast.rest.entities;

import com.google.gson.annotations.SerializedName;

public class SysModel {
    @SerializedName("sunrise") public long sunrise;
    @SerializedName("sunset") public long sunset;
}
