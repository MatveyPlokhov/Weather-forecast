package com.mapl.weather_forecast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;

import static com.mapl.weather_forecast.R.id.fragmentCities;

public class MainActivity extends AppCompatActivity implements Postman {
    Random random = new Random();
    static LinearLayout horizontalLinearLayoutCities;
    static HorizontalScrollView horizontalScrollViewSettings;
    static ScrollView scrollView;

    Button addNewCity;
    ImageView day1, day2, day3, day4, day5,
            todayDayImage, todayNightImage, tomorrowDayImage, tomorrowNightImage;
    TextView temperature1, temperature2, temperature3, temperature4, temperature5,
            todayDayTemperature, todayNightTemperature, tomorrowDayTemperature, tomorrowNightTemperature;
    TextView textViewUrl;
    CheckBox checkBoxHumidity, checkBoxCloudiness;
    String url, selectCity;
    HashMap<String, String> fiveDays;
    LinkedList<String> cityList;

    CitiesFragment citiesFragment;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        initView();
        initFragment();
        clickListeners();
        checkedChangeListener();
    }

    private void initView() {
        addNewCity = findViewById(R.id.addNewCity);

        horizontalLinearLayoutCities = findViewById(R.id.horizontalLinearLayoutCities);
        horizontalScrollViewSettings = findViewById(R.id.horizontalScrollViewSettings);
        scrollView = findViewById(R.id.scrollView);

        day1 = findViewById(R.id.day1);
        day2 = findViewById(R.id.day2);
        day3 = findViewById(R.id.day3);
        day4 = findViewById(R.id.day4);
        day5 = findViewById(R.id.day5);
        todayDayImage = findViewById(R.id.todayDayImage);
        todayNightImage = findViewById(R.id.todayNightImage);
        tomorrowDayImage = findViewById(R.id.tomorrowDayImage);
        tomorrowNightImage = findViewById(R.id.tomorrowNightImage);

        temperature1 = findViewById(R.id.temperature1);
        temperature2 = findViewById(R.id.temperature2);
        temperature3 = findViewById(R.id.temperature3);
        temperature4 = findViewById(R.id.temperature4);
        temperature5 = findViewById(R.id.temperature5);
        todayDayTemperature = findViewById(R.id.todayDayTemperature);
        todayNightTemperature = findViewById(R.id.todayNightTemperature);
        tomorrowDayTemperature = findViewById(R.id.tomorrowDayTemperature);
        tomorrowNightTemperature = findViewById(R.id.tomorrowNightTemperature);

        checkBoxCloudiness = findViewById(R.id.checkBoxCloudiness);
        checkBoxHumidity = findViewById(R.id.checkBoxHumidity);

        textViewUrl = findViewById(R.id.textViewUrl);

        fiveDays = new HashMap<>();
        cityList = new LinkedList<>();
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();

        citiesFragment = (CitiesFragment) fragmentManager.findFragmentById(fragmentCities);
        fragmentManager.beginTransaction()
                .hide(citiesFragment)
                .commit();
    }

    private void initVariableForCity(String city) {
        if (!fiveDays.containsKey(city)) {
            String data = "";
            for (int i = 1; i <= 5; i++) {
                data += (-10 + random.nextInt(20)) + ","
                        + (-10 + random.nextInt(20)) + ","
                        + randomImage(random.nextInt(15)) + ","
                        + randomImage(random.nextInt(15)) + ",";
            }
            fiveDays.put(city, data);
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

        pasteURL(textViewUrl, city);
    }

    private void clickListeners() {
        addNewCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .show(citiesFragment)
                        .commit();
            }
        });
        textViewUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(textViewUrl.getText().toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    @Override
    public void fragmentMail(String cityName) {
        selectCity = cityName;
        fragmentManager.beginTransaction()
                .hide(citiesFragment)
                .commit();
        if (!cityList.contains(cityName)) {
            addButtonInLayout(horizontalLinearLayoutCities, LinearLayout.LayoutParams.WRAP_CONTENT,
                    (int) getResources().getDimension(R.dimen.buttonSize), selectCity);
            cityList.addFirst(selectCity);
            initVariableForCity(selectCity);
            allVisible();
        }
        writeVariableForCity(selectCity);
    }

    private void pasteURL(TextView textView, String city) {
        url = ("https://ru.wikipedia.org/wiki/" + city).replaceAll(" ", "_");
        textView.setText(url);
    }

    private void addButtonInLayout(LinearLayout linearLayout, int width, int height, String city) {
        final Button button = new Button(getApplicationContext());
        button.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        button.setText(city);
        linearLayout.addView(button, 0);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCity = (String) button.getText();
                writeVariableForCity(selectCity);
            }
        });
    }

    private void checkedChangeListener() {
        checkBoxHumidity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    checkBoxHumidity.setText(getString(R.string.humidity) + ": " + random.nextInt(101));
                else
                    checkBoxHumidity.setText(getString(R.string.humidity));
            }
        });
        checkBoxCloudiness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    checkBoxCloudiness.setText(getString(R.string.cloudiness) + ": " + random.nextInt(101));
                else
                    checkBoxCloudiness.setText(getString(R.string.cloudiness));
            }
        });
    }

    static void allVisible() {
        horizontalScrollViewSettings.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("fiveDays", fiveDays);
        outState.putSerializable("cityList", cityList);
        outState.putString("selectCity", selectCity);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fiveDays = (HashMap<String, String>) savedInstanceState.getSerializable("fiveDays");
        cityList = (LinkedList<String>) savedInstanceState.getSerializable("cityList");
        selectCity = savedInstanceState.getString("selectCity");
        for (int i = cityList.size() - 1; i >= 0; i--) {
            addButtonInLayout(horizontalLinearLayoutCities, LinearLayout.LayoutParams.WRAP_CONTENT,
                    (int) getResources().getDimension(R.dimen.buttonSize), cityList.get(i));
        }
        writeVariableForCity(selectCity);
        allVisible();
    }
}