package com.mapl.weather_forecast;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.mapl.weather_forecast.databases.WeatherForecast;
import com.mapl.weather_forecast.loaders.WeatherDataLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

public class WeatherForecastFragment extends Fragment {
    private final Handler handler = new Handler();
    private WeatherForecast weatherForecast;
    private ConstraintLayout constraintLayout;
    private View rootView;
    private Activity activity;
    private ImageView imageViewToday;
    private TextView cityName, textView;
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
            updateWeatherData(mainCity, mainLat, mainLon);
        else
            noData();
        return rootView;
    }

    private void initView() {
        weatherForecast = new WeatherForecast(getContext());
        imageViewToday = rootView.findViewById(R.id.imageViewToday);
        cityName = rootView.findViewById(R.id.cityNameW);
        textView = rootView.findViewById(R.id.textWeather);
        constraintLayout = rootView.findViewById(R.id.weatherConstraintLayout);

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


    void updateWeatherData(final String cityName, final Double lat, final Double lon) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final JSONObject jsonObject = WeatherDataLoader.getJSONData(lat, lon);
                if (jsonObject == null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            CoordinatorLayout coordinatorLayout = activity.findViewById(R.id.coordinatorLayout);
                            String text = getResources().getString(R.string.weather_data_false);
                            Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG).show();
                            noData();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            renderWeather(jsonObject, cityName);
                        }
                    });
                }
            }
        }).start();
    }

    private void renderWeather(JSONObject jsonObject, String cityText) {
        constraintLayout.setVisibility(View.VISIBLE);
        try {
            JSONObject details = jsonObject.getJSONArray("weather").getJSONObject(0);
            JSONObject main = jsonObject.getJSONObject("main");

            cityName.setText(cityText);
            setAll(details, main);
            imageViewToday.setImageResource(setWeatherIcon(details.getInt("id"),
                    jsonObject.getJSONObject("sys").getLong("sunrise") * 1000,
                    jsonObject.getJSONObject("sys").getLong("sunset") * 1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAll(JSONObject details, JSONObject main) throws JSONException {
        String detailsText = details.getString("description").toUpperCase() + "\n"
                + getResources().getString(R.string.temperature) + ": "
                + String.format(Locale.getDefault(), "%.2f", main.getDouble("temp")) + "\u2103\n"
                + getResources().getString(R.string.feels_like) + ": "
                + String.format(Locale.getDefault(), "%.2f", main.getDouble("feels_like")) + "\u2103\n"
                + getResources().getString(R.string.temperature_min) + ": "
                + String.format(Locale.getDefault(), "%.2f", main.getDouble("temp_min")) + "\u2103\n"
                + getResources().getString(R.string.temperature_max) + ": "
                + String.format(Locale.getDefault(), "%.2f", main.getDouble("temp_max")) + "\u2103\n"
                + getResources().getString(R.string.pressure) + ": "
                + main.getInt("pressure") * 0.75 + "\n"
                + getResources().getString(R.string.humidity) + ": "
                + main.getString("humidity") + "%";
        textView.setText(detailsText);
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

    private void noData() {
        constraintLayout.setVisibility(View.GONE);
    }
}
