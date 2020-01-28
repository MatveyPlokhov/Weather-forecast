package com.mapl.weather_forecast.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenWeatherMap {
    private static OpenWeatherMap singleton = null;
    private OpenWeatherMapGET API;

    private OpenWeatherMap() {
        API = createAdapter();
    }

    public static OpenWeatherMap getSingleton() {
        if(singleton == null) {
            singleton = new OpenWeatherMap();
        }

        return singleton;
    }

    public OpenWeatherMapGET getAPI() {
        return API;
    }

    private OpenWeatherMapGET createAdapter() {
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return adapter.create(OpenWeatherMapGET.class);
    }
}