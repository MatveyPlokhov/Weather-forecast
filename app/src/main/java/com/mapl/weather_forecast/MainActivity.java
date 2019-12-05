package com.mapl.weather_forecast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button moscow, stPetersburg, sochi, vladivostok, kazan;
    ImageView arms;
    ImageView day1, day2, day3, day4, day5, day6;
    TextView city, text;
    TextView temperatureDay1, temperatureDay2, temperatureDay3,
            temperatureDay4, temperatureDay5, temperatureDay6;
    TextView humidity, cloudiness;
    CheckBox checkBoxHumidity, checkBoxCloudiness;
    String humidityM, humidityP, humidityS, humidityV, humidityK,
            cloudinessM, cloudinessP, cloudinessS, cloudinessV, cloudinessK;
    int numCity;

    private static final String TAG = "status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        message("onCreate()");
        setContentView(R.layout.activity_main);
        initialization();
        checkedChangeListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        message("onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        message("onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        message("onPause()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        message("onSaveInstanceState()");
        outState.putString("humidityM", humidityM);
        outState.putString("humidityP", humidityP);
        outState.putString("humidityS", humidityS);
        outState.putString("humidityV", humidityV);
        outState.putString("humidityK", humidityK);
        outState.putString("cloudinessM", cloudinessM);
        outState.putString("cloudinessP", cloudinessP);
        outState.putString("cloudinessS", cloudinessS);
        outState.putString("cloudinessV", cloudinessV);
        outState.putString("cloudinessK", cloudinessK);
        outState.putInt("numCity", numCity);
    }

    @Override
    protected void onStop() {
        super.onStop();
        message("onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        message("onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        message("onDestroy()");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        message("onDestroy()");
        humidityM = savedInstanceState.getString("humidityM");
        humidityP = savedInstanceState.getString("humidityP");
        humidityS = savedInstanceState.getString("humidityS");
        humidityV = savedInstanceState.getString("humidityV");
        humidityK = savedInstanceState.getString("humidityK");
        cloudinessM = savedInstanceState.getString("cloudinessM");
        cloudinessP = savedInstanceState.getString("cloudinessP");
        cloudinessS = savedInstanceState.getString("cloudinessS");
        cloudinessV = savedInstanceState.getString("cloudinessV");
        cloudinessK = savedInstanceState.getString("cloudinessK");
        numCity = savedInstanceState.getInt("numCity");
        switchCase();
    }

    private void message(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        Log.i(TAG, message);
    }

    private void initialization() {
        initializationElements();
        initializationClick();
    }

    private void initializationElements() {
        Random random = new Random();

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

        checkBoxCloudiness = findViewById(R.id.checkBoxCloudiness);
        checkBoxHumidity = findViewById(R.id.checkBoxHumidity);

        cloudiness = findViewById(R.id.cloudiness);
        humidity = findViewById(R.id.humidity);

        humidityM = 70 + random.nextInt(20) + "%";
        humidityP = 70 + random.nextInt(20) + "%";
        humidityS = 70 + random.nextInt(20) + "%";
        humidityV = 70 + random.nextInt(20) + "%";
        humidityK = 70 + random.nextInt(20) + "%";
        cloudinessM = random.nextInt(101) + "%";
        cloudinessP = random.nextInt(101) + "%";
        cloudinessS = random.nextInt(101) + "%";
        cloudinessV = random.nextInt(101) + "%";
        cloudinessK = random.nextInt(101) + "%";
        numCity = 1;

        humidity.setText(humidityM);
        cloudiness.setText(cloudinessM);
    }

    private void initializationClick() {
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
                numCity = 1;
                switchCase();
                break;
            case R.id.stPetersburg:
                numCity = 2;
                switchCase();
                break;
            case R.id.sochi:
                numCity = 3;
                switchCase();
                break;
            case R.id.vladivostok:
                numCity = 4;
                switchCase();
                break;
            case R.id.kazan:
                numCity = 5;
                switchCase();
                break;
        }
    }

    public void switchCase() {
        switch (numCity) {
            case 1:
                arms.setImageResource(R.drawable.moscow);
                city.setText(R.string.moscow);
                text.setText(R.string.moscowText);
                temperature();
                imageView();
                humidity.setText(humidityM);
                cloudiness.setText(cloudinessM);
                break;
            case 2:
                arms.setImageResource(R.drawable.saint_petersburg);
                city.setText(R.string.st_petersburg);
                text.setText(R.string.st_petersburgText);
                temperature();
                imageView();
                humidity.setText(humidityP);
                cloudiness.setText(cloudinessP);
                break;
            case 3:
                arms.setImageResource(R.drawable.sochi);
                city.setText(R.string.sochi);
                text.setText(R.string.sochiText);
                temperature();
                imageView();
                humidity.setText(humidityS);
                cloudiness.setText(cloudinessS);
                break;
            case 4:
                arms.setImageResource(R.drawable.vladivostok);
                city.setText(R.string.vladivostok);
                text.setText(R.string.vladivostokText);
                temperature();
                imageView();
                humidity.setText(humidityV);
                cloudiness.setText(cloudinessV);
                break;
            case 5:
                arms.setImageResource(R.drawable.kazan);
                city.setText(R.string.kazan);
                text.setText(R.string.kazanText);
                temperature();
                imageView();
                humidity.setText(humidityK);
                cloudiness.setText(cloudinessK);
                break;
        }
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

    private void imageView() {
        day1.setImageResource(randomImageView());
        day2.setImageResource(randomImageView());
        day3.setImageResource(randomImageView());
        day4.setImageResource(randomImageView());
        day5.setImageResource(randomImageView());
        day6.setImageResource(randomImageView());
    }

    private void temperature() {
        temperatureDay1.setText(randomTemperature());
        temperatureDay2.setText(randomTemperature());
        temperatureDay3.setText(randomTemperature());
        temperatureDay4.setText(randomTemperature());
        temperatureDay5.setText(randomTemperature());
        temperatureDay6.setText(randomTemperature());
    }

    private int randomImageView() {
        Random random = new Random();
        int path = R.drawable.fog;
        switch (random.nextInt(5)) {
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
        return (random.nextInt(5) - 8) + "/" + (random.nextInt(5) - 8);
    }
}