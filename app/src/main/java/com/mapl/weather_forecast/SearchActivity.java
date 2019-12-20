package com.mapl.weather_forecast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements Postman {
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchActivity.this);
        cities = getResources().getStringArray(R.array.citiesArray);
        ArrayList<CityDataClassSearchPage> arrayList = new ArrayList<>();

        for (String city : cities)
            arrayList.add(new CityDataClassSearchPage(city));

        recyclerViewAdapter = new RecyclerViewAdapterSearchPage(arrayList, SearchActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public void getCityName(String cityName) {
        this.cityName = cityName;
        Intent intent = new Intent();
        intent.putExtra(SearchActivity.CITY_KEY, cityName);
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
