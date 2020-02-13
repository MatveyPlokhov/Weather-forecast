package com.mapl.weather_forecast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mapl.weather_forecast.adapter.ViewPagerAdapter;
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

import static android.app.Activity.RESULT_OK;

public class SelectedLocationsFragment extends Fragment {
    private final CurrentWeatherDao currentWeatherDao = CurrentWeatherSingleton.getInstance().getCurrentWeatherDao();
    public static final String BROADCAST_ACTION_WEATHER = "com.mapl.weather_forecast.services.weatherdatafinished";
    private static final String KEY_POSITION = "KEY_POSITION";
    private static int RESULT_KEY = 1;
    private boolean notRunBefore = true;
    private Activity activity;
    private List<CurrentWeather> list;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TextView title;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_selected_locations, container, false);
        initView(root);
        setWeatherForAllLocations();
        return root;
    }

    @Override
    public void onStart() {
        activity.registerReceiver(weatherDataFinishedReceiver, new IntentFilter(BROADCAST_ACTION_WEATHER));
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        activity.unregisterReceiver(weatherDataFinishedReceiver);
        setPosition();
    }

    private void initView(View root) {
        title = root.findViewById(R.id.toolbarTitle);
        viewPager = root.findViewById(R.id.mainViewPager);
        tabLayout = root.findViewById(R.id.tabLayout);
        tabLayout.setTabRippleColor(null);
    }

    private void setPosition() {
        final SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_POSITION, viewPager.getCurrentItem());
        editor.apply();
    }

    private Integer getPosition() {
        final SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        return preferences.getInt(KEY_POSITION, 0);
    }

    void newLocation() {
        Intent intent = new Intent(getContext(), SearchActivity.class);
        startActivityForResult(intent, RESULT_KEY);
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

    private void setWeatherByLocation(final String location, final Double lat, final Double lon) {
        currentWeatherDao.getWeatherListSingle()
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
                        WeatherForecastService.startWeatherForecastService(getContext(), list, true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        WeatherForecastService.startWeatherForecastService(getContext(), new ArrayList<CurrentWeather>(), true);
                    }
                });
    }

    private void setWeatherForAllLocations() {
        currentWeatherDao.getWeatherListSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<CurrentWeather>>() {
                    @Override
                    public void onSuccess(List<CurrentWeather> list) {
                        WeatherForecastService.startWeatherForecastService(getContext(), list, true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        WeatherForecastService.startWeatherForecastService(getContext(), new ArrayList<CurrentWeather>(), true);
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

    private void setViewPagerAdapter(final List<CurrentWeather> list) {
        this.list = list;
        ViewPagerAdapter adapter = ViewPagerAdapter.getInstance(activity, list);
        setViewPagerPreferences();
        adapter.refreshData(list);
        viewPager.setAdapter(adapter);
        if (notRunBefore) {
            notRunBefore = false;
            viewPager.setCurrentItem(getPosition(), false);
        } else {
            viewPager.setCurrentItem(list.size(), false);
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
        viewPager.unregisterOnPageChangeCallback(pageChangeCallback);
        viewPager.registerOnPageChangeCallback(pageChangeCallback);
    }

    private ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            title.setText(list.get(position).location);
        }
    };
}
