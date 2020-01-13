package com.mapl.weather_forecast.adapters;

public class CityDataClassHomePage {
    String city;
    Double lat, lon;

    public CityDataClassHomePage(String city, Double lat, Double lon) {
        this.city = city;
        this.lat = lat;
        this.lon = lon;
    }
}
