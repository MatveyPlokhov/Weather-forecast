package com.mapl.weather_forecast.adapters;

public class CityDataClassSearchPage {
    String city, description;
    Double lat, lon;

    public CityDataClassSearchPage(String city, String description, String pos) {
        String[] latlon = pos.split(" ");
        this.city = city;
        this.description = description;
        lat = Double.parseDouble(latlon[1]);
        lon = Double.parseDouble(latlon[0]);
    }
}
