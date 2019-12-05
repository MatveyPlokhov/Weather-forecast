package com.mapl.weather_forecast;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button moscow, stPetersburg, sochi, vladivostok, kazan;
    ImageView arms;
    ImageView day1,day2,day3,day4,day5,day6;
    TextView city, text;
    TextView temperatureDay1, temperatureDay2, temperatureDay3,
            temperatureDay4, temperatureDay5, temperatureDay6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialization();
    }

    private void initialization() {
        initializationElements();
        initializationButtonClick();
    }

    private void initializationElements() {
        moscow = findViewById(R.id.moscow);
        stPetersburg = findViewById(R.id.stPetersburg);
        sochi = findViewById(R.id.sochi);
        vladivostok = findViewById(R.id.vladivostok);
        kazan = findViewById(R.id.kazan);

        arms = findViewById(R.id.arms);

        day1 = findViewById(R.id.day1);
        day2 = findViewById(R.id.day2);
        day3 = findViewById(R.id.day3);
        day4 = findViewById(R.id.day4);
        day5 = findViewById(R.id.day5);
        day6 = findViewById(R.id.day6);

        city = findViewById(R.id.city);
        text = findViewById(R.id.text);

        temperatureDay1 = findViewById(R.id.temperatureDay1);
        temperatureDay2 = findViewById(R.id.temperatureDay2);
        temperatureDay3 = findViewById(R.id.temperatureDay3);
        temperatureDay4 = findViewById(R.id.temperatureDay4);
        temperatureDay5 = findViewById(R.id.temperatureDay5);
        temperatureDay6 = findViewById(R.id.temperatureDay6);
    }

    private void initializationButtonClick() {
        moscow.setOnClickListener(this);
        stPetersburg.setOnClickListener(this);
        sochi.setOnClickListener(this);
        vladivostok.setOnClickListener(this);
        kazan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.moscow:
                arms.setImageResource(R.drawable.moscow);
                city.setText(R.string.moscow);
                text.setText(R.string.moscowText);
                temperature();
                imageView();
                break;
            case R.id.stPetersburg:
                arms.setImageResource(R.drawable.saint_petersburg);
                city.setText(R.string.st_petersburg);
                text.setText(R.string.st_petersburgText);
                temperature();
                imageView();
                break;
            case R.id.sochi:
                arms.setImageResource(R.drawable.sochi);
                city.setText(R.string.sochi);
                text.setText(R.string.sochiText);
                temperature();
                imageView();
                break;
            case R.id.vladivostok:
                arms.setImageResource(R.drawable.vladivostok);
                city.setText(R.string.vladivostok);
                text.setText(R.string.vladivostokText);
                temperature();
                imageView();
                break;
            case R.id.kazan:
                arms.setImageResource(R.drawable.kazan);
                city.setText(R.string.kazan);
                text.setText(R.string.kazanText);
                temperature();
                imageView();
                break;
        }
    }
    private void imageView() {
        day1.setImageResource(randomImageView());
        day2.setImageResource(randomImageView());
        day3.setImageResource(randomImageView());
        day4.setImageResource(randomImageView());
        day5.setImageResource(randomImageView());
        day6.setImageResource(randomImageView());
    }
    private void temperature(){
        temperatureDay1.setText(randomTemperature());
        temperatureDay2.setText(randomTemperature());
        temperatureDay3.setText(randomTemperature());
        temperatureDay4.setText(randomTemperature());
        temperatureDay5.setText(randomTemperature());
        temperatureDay6.setText(randomTemperature());
    }
    private int randomImageView(){
        Random random = new Random();
        int path = R.drawable.fog;
        switch (random.nextInt(5)){
            case 0:
                path = R.drawable.cloud;
                break;
            case 1:
                path = R.drawable.partlycloud;
                break;
            case 2:
                path = R.drawable.sun;
                break;
            case 3:
                path = R.drawable.lightrainsun_night;
                break;
            case 4:
                path = R.drawable.sleet;
                break;
        }
        return path;
    }
    private String randomTemperature() {
        Random random = new Random();
        return (random.nextInt(5)-8) + "/" + (random.nextInt(5)-8);
    }
}
