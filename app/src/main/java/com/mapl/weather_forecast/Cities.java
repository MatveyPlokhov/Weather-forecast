package com.mapl.weather_forecast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.LinkedList;

public class Cities extends AppCompatActivity {
    LinkedList<String> cities;
    LinearLayout verticalLinearLayoutCities;

    final static String CITY_KEY = "CITY_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cities);
        initialization();
        addCitiesButtons();
    }

    private void initialization() {
        cities = new LinkedList<>(Arrays.asList(getResources().getString(R.string.cities).split(",")));
        verticalLinearLayoutCities = findViewById(R.id.verticalLinearLayoutCities);
    }

    private void addCitiesButtons() {
        for (int i = 0; i < cities.size(); i++) {
            final Button button = new Button(getApplicationContext());
            button.setLayoutParams(new ViewGroup.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setText(cities.get(i));
            verticalLinearLayoutCities.addView(button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(Cities.CITY_KEY, button.getText());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }
}
