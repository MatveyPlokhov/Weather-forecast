package com.mapl.weather_forecast.loaders;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

public class WeatherJSONParsing {
    private JSONObject jsonObject, details, main;

    public WeatherJSONParsing(JSONObject jsonObject) {
        try {
            this.jsonObject = jsonObject;
            details = jsonObject.getJSONArray("weather").getJSONObject(0);
            main = jsonObject.getJSONObject("main");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getDetails() {
        HashMap<String, String> data = null;
        try {
            data = new HashMap<>();
            data.put("description", details.getString("description"));
            data.put("temp", String.format(Locale.getDefault(), "%.2f", main.getDouble("temp")));
            data.put("feels_like", String.format(Locale.getDefault(), "%.2f", main.getDouble("feels_like")));
            data.put("temp_min", String.format(Locale.getDefault(), "%.2f", main.getDouble("temp_min")));
            data.put("temp_max", String.format(Locale.getDefault(), "%.2f", main.getDouble("temp_max")));
            data.put("pressure", String.valueOf(main.getInt("pressure") * 0.75));
            data.put("humidity", main.getString("humidity"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public long[] getDetailsForIcon() {
        long[] array = new long[3];
        try {
            array[0] = details.getInt("id");
            array[1] = jsonObject.getJSONObject("sys").getLong("sunrise") * 1000;
            array[2] = jsonObject.getJSONObject("sys").getLong("sunset") * 1000;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }
}
