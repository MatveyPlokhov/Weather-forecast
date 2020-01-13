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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mapl.weather_forecast.adapters.CityDataClassHomePage;
import com.mapl.weather_forecast.adapters.RecyclerViewAdapterHomePage;
import com.mapl.weather_forecast.databases.WeatherForecast;

import java.util.ArrayList;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class CityListFragment extends Fragment {
    private Activity activity;
    private RecyclerView recyclerView;
    private WeatherForecast weatherForecast;
    private ArrayList<CityDataClassHomePage> arrayList;
    private static int RESULT_KEY = 1;

    private SQLiteDatabase sqLiteDatabase;
    private ContentValues contentValues;
    private Cursor cursor;

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
        return rootView;
    }

    private void initSQLite() {
        sqLiteDatabase = weatherForecast.getWritableDatabase();
        contentValues = new ContentValues();
        cursor = sqLiteDatabase.query(WeatherForecast.TABLE_WEATHER_FORECAST,
                null, null, null, null, null, null);
    }

    private void initView(View view) {
        arrayList = new ArrayList<>();
        weatherForecast = new WeatherForecast(getContext());
        recyclerView = view.findViewById(R.id.recyclerViewSelectedListOfCities);
    }

    private void addCities() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager
                (getContext(), LinearLayoutManager.HORIZONTAL, false);

        addElementsInArray();
        RecyclerViewAdapterHomePage recyclerViewAdapter = new RecyclerViewAdapterHomePage(arrayList, activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    void searchActivityCall() {
        Intent intent = new Intent(getContext(), SearchActivity.class);
        startActivityForResult(intent, RESULT_KEY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_KEY && resultCode == RESULT_OK) {
            String city = Objects.requireNonNull(data).getStringExtra(SearchActivity.CITY_KEY);
            Double lat = data.getDoubleExtra(SearchActivity.LAT_KEY, 0);
            Double lon = data.getDoubleExtra(SearchActivity.LON_KEY, 0);

            if (!cityInDB(city, lat, lon)) {
                addCityInDB(city, lat, lon);
            }
            ((Postman) activity).getCityInfo(city, lat, lon);
        }
    }

    private boolean cityInDB(String city, Double lat, Double lon) {
        initSQLite();
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(WeatherForecast.KEY_NAME);
            int latIndex = cursor.getColumnIndex(WeatherForecast.KEY_LAT);
            int lonIndex = cursor.getColumnIndex(WeatherForecast.KEY_LON);
            do {
                if (city.equals(cursor.getString(nameIndex)) &&
                        lat == cursor.getDouble(latIndex) &&
                        lon == cursor.getDouble(lonIndex))
                    return true;
            } while (cursor.moveToNext());
            cursor.close();
            weatherForecast.close();
        }
        return false;
    }

    private void addCityInDB(String city, Double lat, Double lon) {
        initSQLite();
        contentValues.put(WeatherForecast.KEY_NAME, city);
        contentValues.put(WeatherForecast.KEY_LAT, lat);
        contentValues.put(WeatherForecast.KEY_LON, lon);
        sqLiteDatabase.insert(WeatherForecast.TABLE_WEATHER_FORECAST, null, contentValues);
        cursor.close();
        weatherForecast.close();
        addCities();
    }

    private void addElementsInArray() {
        initSQLite();
        arrayList.clear();
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(WeatherForecast.KEY_NAME);
            int latIndex = cursor.getColumnIndex(WeatherForecast.KEY_LAT);
            int lonIndex = cursor.getColumnIndex(WeatherForecast.KEY_LON);
            do {
                String cityName = cursor.getString(nameIndex);
                Double lat = cursor.getDouble(latIndex);
                Double lon = cursor.getDouble(lonIndex);
                arrayList.add(new CityDataClassHomePage(cityName, lat, lon));
            } while (cursor.moveToNext());
            cursor.close();
            weatherForecast.close();
        }
    }
}
