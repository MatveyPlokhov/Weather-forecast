package com.mapl.weather_forecast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.mapl.weather_forecast.adapter.CityDataClassSearchPage;
import com.mapl.weather_forecast.adapter.RecyclerViewAdapterSearchPage;
import com.mapl.weather_forecast.loader.CityDataLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class SearchActivity extends AppCompatActivity implements Postman {
    public static final String KEY_HISTORY = "KEY_HISTORY";
    private ArrayList<CityDataClassSearchPage> arrayList;
    private RecyclerView recyclerView;
    private DataLoader dataLoader;
    private MapView bigMapView;
    private Toolbar toolbar;
    private ChipGroup chipGroup;

    final static String LOCATION_KEY = "LOCATION_KEY";
    final static String LAT_KEY = "LAT_KEY";
    final static String LON_KEY = "LON_KEY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        setTitle("");
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
        bigMapView = findViewById(R.id.bigMapView);
        initChipGroup();
    }

    private void initChipGroup() {
        chipGroup = findViewById(R.id.chipGroup);
        final SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        Set<String> set = preferences.getStringSet(SearchActivity.KEY_HISTORY, new HashSet<String>());
        String[] array = new String[set.size()];
        int i = 0;
        for (String location : set) {
            array[i] = location;
            i++;
        }
        for (int j = array.length - 1; j >= 0; j--) {
            Chip chip = new Chip(SearchActivity.this);
            chip.setText(array[j]);
            chipGroup.addView(chip);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        MenuItem microItem = menu.findItem(R.id.action_micro);
        SearchView searchView = (SearchView) menuItem.getActionView();
        new BottomActivity(SearchActivity.this, searchView);

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
    public void getLocationInfo(String cityName, Double lat, Double lon) {
        Intent intent = new Intent();
        intent.putExtra(SearchActivity.LOCATION_KEY, cityName);
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