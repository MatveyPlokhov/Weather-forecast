package com.mapl.weather_forecast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements Postman {
    Random random = new Random();

    ImageView day1, day2, day3, day4, day5,
            todayDayImage, todayNightImage, tomorrowDayImage, tomorrowNightImage;
    TextView temperature1, temperature2, temperature3, temperature4, temperature5,
            todayDayTemperature, todayNightTemperature, tomorrowDayTemperature, tomorrowNightTemperature;

    HashMap<String, String> fiveDays;

    FragmentManager fragmentManager;
    CityListFragment cityListFragment;
    WeatherForecastFragment weatherForecastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initFragments();
    }

    private void initView() {

    }

    private void initFragments() {
        fragmentManager = getSupportFragmentManager();

        cityListFragment = (CityListFragment) fragmentManager.findFragmentById(R.id.fragmentSelectedListOfCities);
        weatherForecastFragment = (WeatherForecastFragment) fragmentManager.findFragmentById(R.id.fragmentWeatherForecast);

        fragmentManager.beginTransaction()
                .hide(weatherForecastFragment)
                .commit();
    }

    private void initVariableForCity(String city) {
        if (!fiveDays.containsKey(city)) {
            StringBuilder data = new StringBuilder();
            for (int i = 1; i <= 5; i++) {
                data.append(-10 + random.nextInt(20)).append(",")
                        .append(-10 + random.nextInt(20)).append(",")
                        .append(randomImage(random.nextInt(15))).append(",")
                        .append(randomImage(random.nextInt(15))).append(",");
            }
            fiveDays.put(city, data.toString());
        }
    }

    private int randomImage(int r) {
        int id;
        switch (r) {
            case 0:
                id = R.drawable.cloud;
                break;
            case 1:
                id = R.drawable.fog;
                break;
            case 2:
                id = R.drawable.lightcloud;
                break;
            case 3:
                id = R.drawable.lightcloud_night;
                break;
            case 4:
                id = R.drawable.lightrain;
                break;
            case 5:
                id = R.drawable.lightrainsun;
                break;
            case 6:
                id = R.drawable.lightrainthunder;
                break;
            case 7:
                id = R.drawable.partlycloud;
                break;
            case 8:
                id = R.drawable.partlycloud_night;
                break;
            case 9:
                id = R.drawable.rain;
                break;
            case 10:
                id = R.drawable.rainthunder;
                break;
            case 11:
                id = R.drawable.sleet;
                break;
            case 12:
                id = R.drawable.snow;
                break;
            case 13:
                id = R.drawable.sun;
                break;
            case 14:
                id = R.drawable.sun_night;
                break;
            default:
                id = R.drawable.nodata;
        }
        return id;
    }

    private void writeVariableForCity(String city) {
        String[] parseDate = Objects.requireNonNull(fiveDays.get(city)).split(",");
        temperature1.setText((parseDate[0] + "/" + parseDate[1]));
        todayDayTemperature.setText(parseDate[0]);
        todayNightTemperature.setText(parseDate[1]);
        temperature2.setText((parseDate[4] + "/" + parseDate[5]));
        tomorrowDayTemperature.setText(parseDate[4]);
        tomorrowNightTemperature.setText(parseDate[5]);
        temperature3.setText((parseDate[8] + "/" + parseDate[9]));
        temperature4.setText((parseDate[12] + "/" + parseDate[13]));
        temperature5.setText((parseDate[16] + "/" + parseDate[17]));


        day1.setImageResource(Integer.parseInt(parseDate[2]));
        todayDayImage.setImageResource(Integer.parseInt(parseDate[2]));
        todayNightImage.setImageResource(Integer.parseInt(parseDate[3]));
        day2.setImageResource(Integer.parseInt(parseDate[6]));
        tomorrowDayImage.setImageResource(Integer.parseInt(parseDate[6]));
        tomorrowNightImage.setImageResource(Integer.parseInt(parseDate[7]));
        day3.setImageResource(Integer.parseInt(parseDate[10]));
        day4.setImageResource(Integer.parseInt(parseDate[14]));
        day5.setImageResource(Integer.parseInt(parseDate[18]));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        /*outState.putSerializable("fiveDays", fiveDays);
        outState.putSerializable("cityList", cityList);
        outState.putString("selectCity", selectCity);*/
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        /*fiveDays = (HashMap<String, String>) savedInstanceState.getSerializable("fiveDays");
        cityList = (LinkedList<String>) savedInstanceState.getSerializable("cityList");
        selectCity = savedInstanceState.getString("selectCity");
        writeVariableForCity(selectCity);*/
    }

    @Override
    public void getCityName(String cityName) {
        weatherForecastFragment.updateWeatherData(cityName);
    }
}