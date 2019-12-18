package com.mapl.weather_forecast;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.LinkedList;

public class WeatherForecastFragment extends Fragment {
    ImageView day1, day2, day3, day4, day5,
            todayDayImage, todayNightImage, tomorrowDayImage, tomorrowNightImage;
    TextView temperature1, temperature2, temperature3, temperature4, temperature5,
            todayDayTemperature, todayNightTemperature, tomorrowDayTemperature, tomorrowNightTemperature;
    String selectCity;
    HashMap<String, String> fiveDays;
    LinkedList<String> cityList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather_forecast,container,false);
        initView(rootView);
        return rootView;
    }

    private void initView(View view) {
        day1 = view.findViewById(R.id.day1);
        day2 = view.findViewById(R.id.day2);
        day3 = view.findViewById(R.id.day3);
        day4 = view.findViewById(R.id.day4);
        day5 = view.findViewById(R.id.day5);
        todayDayImage = view.findViewById(R.id.todayDayImage);
        todayNightImage = view.findViewById(R.id.todayNightImage);
        tomorrowDayImage = view.findViewById(R.id.tomorrowDayImage);
        tomorrowNightImage = view.findViewById(R.id.tomorrowNightImage);

        temperature1 = view.findViewById(R.id.temperature1);
        temperature2 = view.findViewById(R.id.temperature2);
        temperature3 = view.findViewById(R.id.temperature3);
        temperature4 = view.findViewById(R.id.temperature4);
        temperature5 = view.findViewById(R.id.temperature5);
        todayDayTemperature = view.findViewById(R.id.todayDayTemperature);
        todayNightTemperature = view.findViewById(R.id.todayNightTemperature);
        tomorrowDayTemperature = view.findViewById(R.id.tomorrowDayTemperature);
        tomorrowNightTemperature = view.findViewById(R.id.tomorrowNightTemperature);

        fiveDays = new HashMap<>();
    }
}
