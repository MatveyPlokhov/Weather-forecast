package com.mapl.weather_forecast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mapl.weather_forecast.adapters.CityDataClassSearchPage;
import com.mapl.weather_forecast.adapters.RecyclerViewAdapterSearchPage;
import com.mapl.weather_forecast.loaders.CityDataLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity implements Postman {
    private ArrayList<CityDataClassSearchPage> arrayList;
    private RecyclerView recyclerView;
    private DataLoader dataLoader;
    Toolbar toolbar;

    final static String CITY_KEY = "CITY_KEY";
    final static String LAT_KEY = "LAT_KEY";
    final static String LON_KEY = "LON_KEY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initToolbar();
        initView();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerViewCityList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        MenuItem microItem = menu.findItem(R.id.action_micro);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (dataLoader != null)
                    dataLoader.cancel(false);
                dataLoader = new DataLoader();
                dataLoader.execute(newText);
                return false;
            }
        });

        microItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getApplicationContext(), "Скоро добавлю", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        return true;
    }


    private ArrayList<CityDataClassSearchPage> cityList(JSONObject jsonObject) {
        ArrayList<CityDataClassSearchPage> arrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject
                    .getJSONObject("response")
                    .getJSONObject("GeoObjectCollection")
                    .getJSONArray("featureMember");
            for (int i = 0; i < jsonArray.length(); i++) {
                arrayList.add(new CityDataClassSearchPage(
                        jsonArray.getJSONObject(i)
                                .getJSONObject("GeoObject")
                                .getString("name"),
                        jsonArray.getJSONObject(i)
                                .getJSONObject("GeoObject")
                                .getString("description"),
                        jsonArray.getJSONObject(i)
                                .getJSONObject("GeoObject")
                                .getJSONObject("Point")
                                .getString("pos")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    @Override
    public void getCityInfo(String cityName, Double lat, Double lon) {
        Intent intent = new Intent();
        intent.putExtra(SearchActivity.CITY_KEY, cityName);
        intent.putExtra(SearchActivity.LAT_KEY, lat);
        intent.putExtra(SearchActivity.LON_KEY, lon);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
        finish();
    }

    @SuppressLint("StaticFieldLeak")
    private class DataLoader extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            JSONObject jsonObject = CityDataLoader.getJSONData(strings[0]);
            if (jsonObject != null)
                arrayList = cityList(jsonObject);
            else
                arrayList = new ArrayList<>();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchActivity.this);
            RecyclerViewAdapterSearchPage recyclerViewAdapter = new RecyclerViewAdapterSearchPage(arrayList, SearchActivity.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(recyclerViewAdapter);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}