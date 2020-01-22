package com.mapl.weather_forecast;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mapl.weather_forecast.databases.WeatherForecast;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class WeatherForecastFragment extends Fragment {
    private WeatherForecast weatherForecast;
    private View rootView;
    private Activity activity;
    private ImageView imageViewToday;
    private TextView textViewLocation, textViewDescription, textViewTemp, textViewFeelsLike,
            textViewTempMin, textViewTempMax, textViewPressure, textViewHumidity;
    private String mainCity;
    private Double mainLat, mainLon;

    private Cursor cursor;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_weather_forecast, container, false);
        initView();
        if (mainCity != null)
            ((Postman) activity).getCityInfo(mainCity, mainLat, mainLon);
        return rootView;
    }

    private void initView() {
        weatherForecast = new WeatherForecast(getContext());
        imageViewToday = rootView.findViewById(R.id.imageViewToday);
        textViewLocation = rootView.findViewById(R.id.textViewLocation);
        textViewDescription = rootView.findViewById(R.id.textViewDescription);
        textViewTemp = rootView.findViewById(R.id.textViewTemp);
        textViewFeelsLike = rootView.findViewById(R.id.textViewFeelsLike);
        textViewTempMin = rootView.findViewById(R.id.textViewTempMin);
        textViewTempMax = rootView.findViewById(R.id.textViewTempMax);
        textViewPressure = rootView.findViewById(R.id.textViewPressure);
        textViewHumidity = rootView.findViewById(R.id.textViewHumidity);

        initSQLite();
        if (cursor.moveToFirst()) {
            mainCity = cursor.getString(cursor.getColumnIndex(WeatherForecast.KEY_NAME));
            mainLat = cursor.getDouble(cursor.getColumnIndex(WeatherForecast.KEY_LAT));
            mainLon = cursor.getDouble(cursor.getColumnIndex(WeatherForecast.KEY_LON));
        } else {
            mainCity = null;
        }
        cursor.close();
        weatherForecast.close();
    }

    private void initSQLite() {
        SQLiteDatabase sqLiteDatabase = weatherForecast.getWritableDatabase();
        cursor = sqLiteDatabase.query(WeatherForecast.TABLE_WEATHER_FORECAST,
                null, null, null, null, null, null);
    }

    void updateWeatherData(String location, HashMap<String, String> dataDetails, long[] arrayForIcon) {
        textViewLocation.setText(location);

        String description = Objects.requireNonNull(dataDetails.get("description")).toUpperCase();
        textViewDescription.setText(description);

        String temp = getResources().getString(R.string.temperature) + ": " + dataDetails.get("temp");
        textViewTemp.setText(temp);

        String feels_like = getResources().getString(R.string.feels_like) + ": " + dataDetails.get("feels_like");
        textViewFeelsLike.setText(feels_like);

        String temp_min = getResources().getString(R.string.temperature_min) + ": " + dataDetails.get("temp_min");
        textViewTempMin.setText(temp_min);

        String temp_max = getResources().getString(R.string.temperature_max) + ": " + dataDetails.get("temp_max");
        textViewTempMax.setText(temp_max);

        String pressure = getResources().getString(R.string.pressure) + ": " + dataDetails.get("pressure");
        textViewPressure.setText(pressure);

        String humidity = getResources().getString(R.string.humidity) + ": " + dataDetails.get("humidity");
        textViewHumidity.setText(humidity);

        imageViewToday.setImageResource(setWeatherIcon((int) arrayForIcon[0], arrayForIcon[1], arrayForIcon[2]));
    }

    private int setWeatherIcon(int actualID, long sunrise, long sunset) {
        int id = actualID / 100;
        long currentTime = new Date().getTime();
        if (currentTime >= sunrise && currentTime < sunset) {
            switch (id) {
                case (2):
                    return R.drawable.thunderstorm;
                case (3):
                    return R.drawable.shower_rain;
                case (5):
                    if (actualID >= 500 && actualID <= 504)
                        return R.drawable.rain_d;
                    else if (actualID == 511)
                        return R.drawable.snow;
                    else
                        return R.drawable.shower_rain;
                case (6):
                    return R.drawable.snow;
                case (7):
                    return R.drawable.mist;
                case (8):
                    if (actualID == 800)
                        return R.drawable.clear_sky_d;
                    else if (actualID == 801)
                        return R.drawable.few_clouds_d;
                    else if (actualID == 802)
                        return R.drawable.scattered_clouds;
                    else if (actualID == 803 || actualID == 804)
                        return R.drawable.broken_clouds;
            }
        } else {
            switch (id) {
                case (2):
                    return R.drawable.thunderstorm;
                case (3):
                    return R.drawable.shower_rain;
                case (5):
                    if (actualID >= 500 && actualID <= 504)
                        return R.drawable.rain_n;
                    else if (actualID == 511)
                        return R.drawable.snow;
                    else
                        return R.drawable.shower_rain;
                case (6):
                    return R.drawable.snow;
                case (7):
                    return R.drawable.mist;
                case (8):
                    if (actualID == 800)
                        return R.drawable.clear_sky_n;
                    else if (actualID == 801)
                        return R.drawable.few_clouds_n;
                    else if (actualID == 802)
                        return R.drawable.scattered_clouds;
                    else if (actualID == 803 || actualID == 804)
                        return R.drawable.broken_clouds;
            }
        }
        return R.drawable.ic_launcher_foreground;
    }
}
