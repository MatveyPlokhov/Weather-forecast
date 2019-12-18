package com.mapl.weather_forecast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class CityListInSearchPage extends AppCompatActivity implements Postman {
    private RecyclerViewAdapterSearchPage recyclerViewAdapter;
    private RecyclerView recyclerView;
    private String[] cities;
    private String cityName;

    final static String CITY_KEY = "CITY_KEY";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        addCards();
    }

    private void initView() {
        setContentView(R.layout.activity_city);
        recyclerView = findViewById(R.id.recyclerViewCityList);
    }

    private void addCards() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CityListInSearchPage.this);
        cities = getResources().getStringArray(R.array.citiesArray);
        ArrayList<CityDataClassSearchPage> arrayList = new ArrayList<>();

        for (String city : cities)
            arrayList.add(new CityDataClassSearchPage(city));

        recyclerViewAdapter = new RecyclerViewAdapterSearchPage(arrayList, CityListInSearchPage.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void getCityName(String cityName) {
        this.cityName = cityName;
        Intent intent = new Intent();
        intent.putExtra(CityListInSearchPage.CITY_KEY, cityName);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
        finish();
    }
}
