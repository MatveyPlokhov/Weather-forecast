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
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class WeatherForecastFragment extends Fragment {
    private WeatherForecast weatherForecast;
    private View rootView;
    private Activity activity;
    private ImageView imageViewToday, imageViewBackground;
    private TextView textViewLocation, textViewDescription, textViewTemp, textViewFeelsLike,
            textViewPressure, textViewHumidity;
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
        imageViewBackground = rootView.findViewById(R.id.imageViewBackground);
        textViewLocation = rootView.findViewById(R.id.textViewLocation);
        textViewDescription = rootView.findViewById(R.id.textViewDescription);
        textViewTemp = rootView.findViewById(R.id.textViewTemp);
        textViewFeelsLike = rootView.findViewById(R.id.textViewFeelsLike);
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
                    Picasso.get().load("https://images.unsplash.com/photo-1510300101842-d7a2ed0bde3b?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=399&q=80")
                            .into(imageViewBackground);
                    return R.drawable.thunderstorm;
                case (3):
                    Picasso.get().load("https://images.unsplash.com/photo-1562431382-9f95ac53b255?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=667&q=80")
                            .into(imageViewBackground);
                    return R.drawable.shower_rain;
                case (5):
                    if (actualID >= 500 && actualID <= 504) {
                        Picasso.get().load("https://images.unsplash.com/photo-1534088568595-a066f410bcda?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=389&q=80")
                                .into(imageViewBackground);
                        return R.drawable.rain_d;
                    } else if (actualID == 511) {
                        Picasso.get().load("https://images.unsplash.com/photo-1558449033-fdc1c2839a66?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=750&q=80")
                                .into(imageViewBackground);
                        return R.drawable.snow;
                    } else {
                        Picasso.get().load("https://images.unsplash.com/photo-1562431382-9f95ac53b255?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=667&q=80")
                                .into(imageViewBackground);
                        return R.drawable.shower_rain;
                    }
                case (6):
                    Picasso.get().load("https://images.unsplash.com/photo-1558449033-fdc1c2839a66?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=750&q=80")
                            .into(imageViewBackground);
                    return R.drawable.snow;
                case (7):
                    Picasso.get().load("https://images.unsplash.com/photo-1542362642-b430743e4686?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=400&q=80")
                            .into(imageViewBackground);
                    return R.drawable.mist;
                case (8):
                    if (actualID == 800) {
                        Picasso.get().load("https://images.unsplash.com/photo-1530530824905-661c2bb787f6?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=373&q=80")
                                .into(imageViewBackground);
                        return R.drawable.clear_sky_d;
                    } else if (actualID == 801) {
                        Picasso.get().load("https://images.unsplash.com/photo-1443756972431-09d3b5cd65a3?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=750&q=80")
                                .into(imageViewBackground);
                        return R.drawable.few_clouds_d;
                    } else if (actualID == 802) {
                        Picasso.get().load("https://images.unsplash.com/photo-1570356812095-e08416a06299?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=667&q=80")
                                .into(imageViewBackground);
                        return R.drawable.scattered_clouds;
                    } else if (actualID == 803 || actualID == 804) {
                        Picasso.get().load("https://images.unsplash.com/photo-1571181284424-1271ac959bbf?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=334&q=80")
                                .into(imageViewBackground);
                        return R.drawable.broken_clouds;
                    }
            }
        } else {
            switch (id) {
                case (2):
                    Picasso.get().load("https://images.unsplash.com/photo-1510300101842-d7a2ed0bde3b?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=399&q=80")
                            .into(imageViewBackground);
                    return R.drawable.thunderstorm;
                case (3):
                    Picasso.get().load("https://images.unsplash.com/photo-1562431382-9f95ac53b255?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=667&q=80")
                            .into(imageViewBackground);
                    return R.drawable.shower_rain;
                case (5):
                    if (actualID >= 500 && actualID <= 504) {
                        Picasso.get().load("https://images.unsplash.com/photo-1534088568595-a066f410bcda?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=389&q=80")
                                .into(imageViewBackground);
                        return R.drawable.rain_n;
                    } else if (actualID == 511) {
                        Picasso.get().load("https://images.unsplash.com/photo-1558449033-fdc1c2839a66?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=750&q=80")
                                .into(imageViewBackground);
                        return R.drawable.snow;
                    } else {
                        Picasso.get().load("https://images.unsplash.com/photo-1562431382-9f95ac53b255?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=667&q=80")
                                .into(imageViewBackground);
                        return R.drawable.shower_rain;
                    }
                case (6):
                    Picasso.get().load("https://images.unsplash.com/photo-1558449033-fdc1c2839a66?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=750&q=80")
                            .into(imageViewBackground);
                    return R.drawable.snow;
                case (7):
                    Picasso.get().load("https://images.unsplash.com/photo-1542362642-b430743e4686?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=400&q=80")
                            .into(imageViewBackground);
                    return R.drawable.mist;
                case (8):
                    if (actualID == 800) {
                        Picasso.get().load("https://images.unsplash.com/photo-1530530824905-661c2bb787f6?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=373&q=80")
                                .into(imageViewBackground);
                        return R.drawable.clear_sky_n;
                    } else if (actualID == 801) {
                        Picasso.get().load("https://images.unsplash.com/photo-1443756972431-09d3b5cd65a3?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=750&q=80")
                                .into(imageViewBackground);
                        return R.drawable.few_clouds_n;
                    } else if (actualID == 802) {
                        Picasso.get().load("https://images.unsplash.com/photo-1570356812095-e08416a06299?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=667&q=80")
                                .into(imageViewBackground);
                        return R.drawable.scattered_clouds;
                    } else if (actualID == 803 || actualID == 804) {
                        Picasso.get().load("https://images.unsplash.com/photo-1571181284424-1271ac959bbf?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=334&q=80")
                                .into(imageViewBackground);
                        return R.drawable.broken_clouds;
                    }
            }
        }
        return R.drawable.ic_launcher_foreground;
    }
}
