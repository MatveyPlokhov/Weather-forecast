package com.mapl.weather_forecast;

import android.app.Activity;
import android.content.ContentValues;
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

public class WeatherForecastFragment extends Fragment {
    private final Handler handler = new Handler();
    private WeatherForecast weatherForecast;
    private ConstraintLayout constraintLayout;
    private View rootView;
    private Activity activity;
    private ImageView imageViewToday;
    private TextView cityName;
    private String mainCity;

    private SQLiteDatabase sqLiteDatabase;
    private ContentValues contentValues;
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
            updateWeatherData(mainCity);
        else
            noData();
        return rootView;
    }

    private void initView() {
        weatherForecast = new WeatherForecast(getContext());
        imageViewToday = rootView.findViewById(R.id.imageViewToday);
        cityName = rootView.findViewById(R.id.cityNameW);
        constraintLayout = rootView.findViewById(R.id.weatherConstraintLayout);

        initSQLite();
        if (cursor.moveToFirst()) {
            mainCity = cursor.getString(cursor.getColumnIndex(weatherForecast.KEY_NAME));
        } else {
            mainCity = null;
        }
        cursor.close();
        weatherForecast.close();
    }

    private void initSQLite() {
        sqLiteDatabase = weatherForecast.getWritableDatabase();
        contentValues = new ContentValues();
        cursor = sqLiteDatabase.query(weatherForecast.TABLE_WEATHER_FORECAST,
                null, null, null, null, null, null);
    }


    public void updateWeatherData(final String cityName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final JSONObject jsonObject = WeatherDataLoader.getJSONData(cityName);
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
                            renderWeather(jsonObject);
                        }
                    });
                }
            }
        }).start();
    }

    public void renderWeather(JSONObject jsonObject) {
        constraintLayout.setVisibility(View.VISIBLE);
        try {
            JSONObject details = jsonObject.getJSONArray("weather").getJSONObject(0);
            JSONObject main = jsonObject.getJSONObject("main");

            setPlaceName(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPlaceName(JSONObject jsonObject) throws JSONException {
        String cityText = jsonObject.getString("name");
        cityName.setText(cityText);
    }

    private void noData() {
        constraintLayout.setVisibility(View.GONE);
    }
}
