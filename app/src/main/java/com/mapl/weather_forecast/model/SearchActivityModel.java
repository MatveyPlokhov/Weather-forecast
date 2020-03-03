package com.mapl.weather_forecast.model;

import com.algolia.search.saas.Client;
import com.mapl.weather_forecast.adapter.CityDataClassSearchPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class SearchActivityModel {
    private final Client client = new Client("P44U24LSLP", "4fc2c6f8eb944ea5035fb67d2ad32665");


    private static final String OPEN_WEATHER_API_URL =
            "https://geocode-maps.yandex.ru/1.x/?apikey=e73b1227-394a-451c-848e-17adb16a752d&format=json&results=10&lang=%s&geocode=%s";

    public JSONObject getJSONData(String city) {

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

    public ArrayList<CityDataClassSearchPage> cityList(JSONObject jsonObject) {
        ArrayList<CityDataClassSearchPage> arrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject
                    .getJSONObject("response")
                    .getJSONObject("GeoObjectCollection")
                    .getJSONArray("featureMember");
            for (int i = 0; i < jsonArray.length(); i++) {
                arrayList.add(new CityDataClassSearchPage(
                        jsonArray.getJSONObject(i)
                                .getJSONObject("GeoObject")
                                .getString("name"),
                        jsonArray.getJSONObject(i)
                                .getJSONObject("GeoObject")
                                .getString("description"),
                        jsonArray.getJSONObject(i)
                                .getJSONObject("GeoObject")
                                .getJSONObject("Point")
                                .getString("pos")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}
