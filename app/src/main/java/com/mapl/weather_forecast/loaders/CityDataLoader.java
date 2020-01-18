package com.mapl.weather_forecast.loaders;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class CityDataLoader {
    private static final String OPEN_WEATHER_API_URL =
            "https://geocode-maps.yandex.ru/1.x/?apikey=e73b1227-394a-451c-848e-17adb16a752d&format=json&results=10&lang=%s&geocode=%s";

    public static JSONObject getJSONData(String city) {

        try {
            String lang;
            switch (Locale.getDefault().getLanguage()) {
                case "ru":
                    lang = "ru_RU";
                    break;
                case "uk":
                    lang = "uk_UA";
                    break;
                case "be":
                    lang = "be_BY";
                    break;
                default:
                    lang = "en_US";

            }
            URL url = new URL(String.format(OPEN_WEATHER_API_URL, lang, city));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder rawData = new StringBuilder(1024);

            String tempVariable;

            while ((tempVariable = reader.readLine()) != null) {
                rawData.append(tempVariable).append("\n");
            }

            reader.close();
            connection.disconnect();
            return new JSONObject(rawData.toString());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
