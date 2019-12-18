package com.mapl.weather_forecast;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mapl.weather_forecast.databases.WeatherForecast;

import java.util.ArrayList;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class CityListInHomePage extends Fragment {
    private RecyclerViewAdapterHomePage recyclerViewAdapter;
    Activity activity;
    RecyclerView recyclerView;
    WeatherForecast weatherForecast;
    CardView cardView;
    private ArrayList<CityDataClassHomePage> arrayList;
    public static int RESULT_KEY = 1;

    SQLiteDatabase sqLiteDatabase;
    ContentValues contentValues;
    Cursor cursor;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_selected_list_of_cities, container, false);
        initView(rootView);
        addCities();
        clickListeners();
        return rootView;
    }

    private void initSQLite() {
        sqLiteDatabase = weatherForecast.getWritableDatabase();
        contentValues = new ContentValues();
        cursor = sqLiteDatabase.query(weatherForecast.TABLE_WEATHER_FORECAST,
                null, null, null, null, null, null);
    }

    private void initView(View view) {
        arrayList = new ArrayList<>();
        weatherForecast = new WeatherForecast(getContext());
        recyclerView = view.findViewById(R.id.recyclerViewSelectedListOfCities);
        cardView = view.findViewById(R.id.addCityHP);
    }

    private void addCities() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager
                (getContext(), LinearLayoutManager.HORIZONTAL, false);

        addElementsInArray();
        recyclerViewAdapter = new RecyclerViewAdapterHomePage(arrayList, activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void clickListeners() {
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivityForResult(intent, RESULT_KEY);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_KEY && resultCode == RESULT_OK) {
            String city = Objects.requireNonNull(data).getStringExtra(SearchActivity.CITY_KEY);
            if (!cityInDB(city))
                addCityInDB(city);
        }
    }

    private boolean cityInDB(String city) {
        initSQLite();
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(weatherForecast.KEY_NAME);
            do {
                if (city.equals(cursor.getString(nameIndex)))
                    return true;
            } while (cursor.moveToNext());
            cursor.close();
            weatherForecast.close();
        }
        return false;
    }

    private void addCityInDB(String city) {
        initSQLite();
        contentValues.put(weatherForecast.KEY_NAME, city);
        sqLiteDatabase.insert(weatherForecast.TABLE_WEATHER_FORECAST, null, contentValues);
        cursor.close();
        weatherForecast.close();
        addCities();
        CoordinatorLayout coordinatorLayout = activity.findViewById(R.id.coordinatorLayout);
        String text = getResources().getString(R.string.snackbarInfo);
        Snackbar.make(coordinatorLayout, text + " (" + city + ")", Snackbar.LENGTH_LONG)
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }

    private void addElementsInArray() {
        initSQLite();
        arrayList.clear();
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(weatherForecast.KEY_NAME);
            do {
                String cityName = cursor.getString(nameIndex);
                arrayList.add(new CityDataClassHomePage(cityName));
            } while (cursor.moveToNext());
            cursor.close();
            weatherForecast.close();
        }
    }

}
