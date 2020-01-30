package com.mapl.weather_forecast;

public interface Postman {
    void getLocationInfo(String cityName, Double lat, Double lon);
}