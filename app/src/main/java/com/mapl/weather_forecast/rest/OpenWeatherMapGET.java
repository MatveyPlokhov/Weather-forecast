package com.mapl.weather_forecast.rest;

import com.mapl.weather_forecast.rest.entities.OpenWeatherMapModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherMapGET {
    @GET("data/2.5/weather")
    Call<OpenWeatherMapModel> loadWeather(@Query("lat") String lat,
                                          @Query("lon") String lon,
                                          @Query("appid") String apiKey,
                                          @Query("units") String units,
                                          @Query("lang") String lang);
}
