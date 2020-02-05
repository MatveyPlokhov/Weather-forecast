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

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mapl.weather_forecast.adapter.ViewPagerAdapter;
import com.mapl.weather_forecast.broadcastreceiver.BatteryReceiver;
import com.mapl.weather_forecast.broadcastreceiver.NetworkReceiver;
import com.mapl.weather_forecast.database.CurrentWeatherSingleton;
import com.mapl.weather_forecast.database.dao.CurrentWeatherDao;
import com.mapl.weather_forecast.database.model.CurrentWeather;
import com.mapl.weather_forecast.service.WeatherForecastService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private final CurrentWeatherDao currentWeatherDao = CurrentWeatherSingleton.getInstance().getCurrentWeatherDao();
    public static final String BROADCAST_ACTION_WEATHER = "com.mapl.weather_forecast.services.weatherdatafinished";
    private static final String KEY_POSITION = "KEY_POSITION";
    private static int RESULT_KEY = 1;
    private boolean notRunBefore = true;

    private FloatingActionButton floatingActionButton;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefaultNavigationView);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNotificationChannel();
        initToolbar();
        initNavigationView();
        initView();
        setWeatherForAllLocations();
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
        //unregisterReceiver(weatherDataFinishedReceiver);
        setPosition();
    }

    private void setPosition() {
        final SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_POSITION, viewPager.getCurrentItem());
        editor.apply();
    }

    private Integer getPosition() {
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
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setTabRippleColor(null);
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
        initBroadcastReceiver();
    }

    private void initBroadcastReceiver() {
        BroadcastReceiver networkReceiver = new NetworkReceiver();
        BroadcastReceiver batteryReceiver = new BatteryReceiver();
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
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

            setWeatherByLocation(location, lat, lon);
        }
    }

    public void setWeatherByLocation(final String location, final Double lat, final Double lon) {
        currentWeatherDao.getWeatherList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<CurrentWeather>>() {
                    @Override
                    public void onSuccess(List<CurrentWeather> list) {
                        CurrentWeather currentWeather = new CurrentWeather();
                        currentWeather.location = location;
                        currentWeather.latitude = lat;
                        currentWeather.longitude = lon;
                        list.add(currentWeather);
                        WeatherForecastService.startWeatherForecastService(MainActivity.this, list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        WeatherForecastService.startWeatherForecastService(MainActivity.this, new ArrayList<CurrentWeather>());
                    }
                });
    }

    public void setWeatherForAllLocations() {
        currentWeatherDao.getWeatherList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<CurrentWeather>>() {
                    @Override
                    public void onSuccess(List<CurrentWeather> list) {
                        WeatherForecastService.startWeatherForecastService(MainActivity.this, list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        WeatherForecastService.startWeatherForecastService(MainActivity.this, new ArrayList<CurrentWeather>());
                    }
                });
    }

    private BroadcastReceiver weatherDataFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(WeatherForecastService.EXTRA_RESULT);
            List<CurrentWeather> list = (List<CurrentWeather>) intent.getSerializableExtra(WeatherForecastService.EXTRA_LIST_SEND);
            if (result != null) {
                if (result.equals(WeatherForecastService.RESULT_OK)) {
                    setViewPagerAdapter(list);
                } else if (result.equals(WeatherForecastService.CONNECTION_ERROR)) {
                    //Вывожу ошибку
                    setViewPagerAdapter(list);
                }
            }
        }
    };

    private void setViewPagerAdapter(List<CurrentWeather> list) {
        ViewPagerAdapter adapter = ViewPagerAdapter.getInstance(MainActivity.this, list);
        setViewPagerPreferences();
        adapter.refreshData(list);
        viewPager.setAdapter(adapter);
        if (notRunBefore) {
            notRunBefore = false;
            viewPager.setCurrentItem(getPosition());
        } else {
            viewPager.setCurrentItem(list.size());
        }

        //синхронизирую tabLayout с viewPager
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    }
                }).attach();
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
        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);
        viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        viewPager.setOffscreenPageLimit(3);
    }
}