package com.mapl.weather_forecast.services;

import com.mapl.weather_forecast.rest.entities.OpenWeatherMapModel;

import java.util.HashMap;

class WeatherParsing {
    private OpenWeatherMapModel model;

    WeatherParsing(OpenWeatherMapModel model) {
        this.model = model;
    }

    HashMap<String, String> getDetails() {
        HashMap<String, String> data = new HashMap<>();
        data.put("description", model.weather[0].description);
        data.put("temp", String.valueOf(Math.round(model.main.temp)));
        data.put("feels_like", String.valueOf(Math.round(model.main.feels_like)));
        data.put("pressure", String.valueOf(model.main.pressure * 0.75));
        data.put("humidity", String.valueOf(model.main.humidity));
        return data;
    }

    long[] getDetailsForIcon() {
        long[] array = new long[3];
        array[0] = model.weather[0].id;
        array[1] = model.sys.sunrise * 1000;
        array[2] = model.sys.sunset * 1000;
        return array;
    }
}
