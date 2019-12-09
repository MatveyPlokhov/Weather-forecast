package com.mapl.weather_forecast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;
import java.util.Random;

public class Main extends AppCompatActivity {
    Button addNewCity;
    LinearLayout horizontalLinearLayoutCities;
    ImageView day1, day2, day3, day4, day5;
    TextView temperatureDay1, temperatureDay2, temperatureDay3,
            temperatureDay4, temperatureDay5;
    TextView humidity, cloudiness, textViewUrl;
    CheckBox checkBoxHumidity, checkBoxCloudiness;
    private final int RESULT_KEY = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initialization();
        clickListeners();
        checkedChangeListener();
    }

    private void initialization() {
        Random random = new Random();

        addNewCity = findViewById(R.id.addNewCity);
        horizontalLinearLayoutCities = findViewById(R.id.horizontalLinearLayoutCities);

        textViewUrl = findViewById(R.id.textViewUrl);

        day1 = findViewById(R.id.day1);
        day2 = findViewById(R.id.day2);
        day3 = findViewById(R.id.day3);
        day4 = findViewById(R.id.day4);
        day5 = findViewById(R.id.day5);

        temperatureDay1 = findViewById(R.id.temperatureDay1);
        temperatureDay2 = findViewById(R.id.temperatureDay2);
        temperatureDay3 = findViewById(R.id.temperatureDay3);
        temperatureDay4 = findViewById(R.id.temperatureDay4);
        temperatureDay5 = findViewById(R.id.temperatureDay5);

        checkBoxCloudiness = findViewById(R.id.checkBoxCloudiness);
        checkBoxHumidity = findViewById(R.id.checkBoxHumidity);

        cloudiness = findViewById(R.id.cloudiness);
        humidity = findViewById(R.id.humidity);
    }


    private void clickListeners() {
        addNewCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, Cities.class);
                startActivityForResult(intent, RESULT_KEY);
            }
        });
        day1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_KEY && resultCode == RESULT_OK) {
            final Button button;
            button = addButtonsInLayout(0,
                    horizontalLinearLayoutCities,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    (int) getResources().getDimension(R.dimen.buttonSize),
                    Objects.requireNonNull(data).getStringExtra(Cities.CITY_KEY));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pasteURL(textViewUrl, "https://ru.wikipedia.org/wiki/" + button.getText());
                }
            });
        }
    }

    private void pasteURL(TextView textView, String url) {
        textView.setText(url);
        Linkify.addLinks(textView, Linkify.WEB_URLS);
    }

    private Button addButtonsInLayout(int position, LinearLayout linearLayout, int width, int height, String city) {
        Button button = new Button(getApplicationContext());
        button.setLayoutParams(new ViewGroup.LayoutParams(width,height));
        button.setText(city);
        linearLayout.addView(button, position);
        pasteURL(textViewUrl, "https://ru.wikipedia.org/wiki/" + city);
        return button;
    }

    private void checkedChangeListener() {
        checkBoxHumidity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    humidity.setVisibility(View.VISIBLE);
                } else {
                    humidity.setVisibility(View.GONE);
                }
            }
        });
        checkBoxCloudiness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cloudiness.setVisibility(View.VISIBLE);
                } else
                    cloudiness.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }
}