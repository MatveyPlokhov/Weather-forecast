package com.mapl.weather_forecast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mapl.weather_forecast.adapter.ViewPagerAdapter;
import com.mapl.weather_forecast.dao.CurrentWeatherDao;
import com.mapl.weather_forecast.service.WeatherForecastService;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final String BROADCAST_ACTION_WEATHER = "com.mapl.weather_forecast.services.weatherdatafinished";
    private static final String KEY_POSITION = "KEY_POSITION";
    private static int RESULT_KEY = 1;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ViewPager2 viewPager;

    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefaultNavigationView);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNotificationChannel();
        initToolbar();
        initNavigationView();
        initView();
        setViewPagerAdapter(getPosition());
        clickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(weatherDataFinishedReceiver, new IntentFilter(BROADCAST_ACTION_WEATHER));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(weatherDataFinishedReceiver);
        setPosition();
    }

    private void setPosition() {
        final SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_POSITION, viewPager.getCurrentItem());
        editor.apply();
    }

    private int getPosition() {
        final SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        return preferences.getInt(KEY_POSITION, 0);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initNavigationView() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.settingsItem:
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initView() {
        viewPager = findViewById(R.id.mainViewPager);
        floatingActionButton = findViewById(R.id.fabAddLocation);
        floatingActionButton.setColorFilter(Color.rgb(255, 255, 255));
    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel("2", "Weather Forecast", importance);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    private void clickListeners() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(intent, RESULT_KEY);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_KEY && resultCode == RESULT_OK) {
            String location = Objects.requireNonNull(data).getStringExtra(SearchActivity.LOCATION_KEY);
            Double lat = data.getDoubleExtra(SearchActivity.LAT_KEY, 0);
            Double lon = data.getDoubleExtra(SearchActivity.LON_KEY, 0);

            setLocationInDatabase(location, lat, lon);
        }
    }

    public void setLocationInDatabase(String location, Double lat, Double lon) {
        WeatherForecastService.startWeatherForecastService(MainActivity.this, location, lat, lon);
    }

    private BroadcastReceiver weatherDataFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(WeatherForecastService.EXTRA_RESULT);
            String message;
            if (result != null) {
                if (result.equals(WeatherForecastService.RESULT_OK)) {
                    setViewPagerAdapter();
                } else if (result.equals(WeatherForecastService.SERVER_ERROR)) {
                    message = intent.getStringExtra(WeatherForecastService.EXTRA_MESSAGE);
                    //Вывожу ошибку
                } else if (result.equals(WeatherForecastService.CONNECTION_ERROR)) {
                    //Вывожу ошибку
                }
            }
        }
    };

    private void setViewPagerAdapter() {
        AgentAsyncTask startAdapter = new AgentAsyncTask();
        startAdapter.execute();
    }

    private void setViewPagerAdapter(int position) {
        AgentAsyncTask startAdapter = new AgentAsyncTask();
        startAdapter.execute(position);
    }

    @SuppressLint("StaticFieldLeak")
    private class AgentAsyncTask extends AsyncTask<Integer, Integer, ViewPagerAdapter> {
        private Integer listSize, position = null;

        @Override
        protected ViewPagerAdapter doInBackground(Integer... integer) {
            CurrentWeatherDao currentWeatherDao = CurrentWeatherSingleton.getInstance().getCurrentWeatherDao();
            listSize = currentWeatherDao.getWeatherList().size();
            if (integer.length != 0) position = integer[0];
            return new ViewPagerAdapter(MainActivity.this, currentWeatherDao.getWeatherList());
        }

        @Override
        protected void onPostExecute(ViewPagerAdapter adapter) {
            setViewPagerPreferences();
            viewPager.setAdapter(adapter);
            if (position != null) viewPager.setCurrentItem(position);
            else viewPager.setCurrentItem(listSize);
        }
    }

    private void setViewPagerPreferences() {
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });
        viewPager.setPageTransformer(transformer);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        viewPager.setOffscreenPageLimit(3);
    }
}