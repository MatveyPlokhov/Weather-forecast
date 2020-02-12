package com.mapl.weather_forecast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity implements PageChanged {
    public static final String TAG_LOCATIONS = "LOCATIONS";
    public static final String TAG_MY_LOCATION = "MY_LOCATION";
    private final String PREFERENCES = "PREFERENCES";
    private boolean darkTheme;
    private FragmentManager fragmentManager;

    private FloatingActionButton fab;
    private BottomAppBar bottomAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNotificationChannel();
        initMain();
        initView();
        clickListener();
    }

    private void initMain() {
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
        addToggle(drawerLayout, bottomAppBar);
        NavigationUI.setupWithNavController(navigationView, navController);

        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_theme);
        SwitchMaterial switchTheme = menuItem.getActionView().findViewById(R.id.switchTheme);
        switchTheme.setChecked(darkTheme);

        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("DARK_THEME", true);
                    editor.apply();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("DARK_THEME", false);
                    editor.apply();
                }
            }
        });
    }

    private void setTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        darkTheme = sharedPreferences.getBoolean("DARK_THEME", true);

        if (darkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void addToggle(DrawerLayout drawerLayout, BottomAppBar bottomAppBar) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this, drawerLayout, bottomAppBar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initView() {
        fab = findViewById(R.id.fabWithBottomAppBar);
        fab.setTag(TAG_LOCATIONS);
    }

    private void clickListener() {
        fragmentManager = getSupportFragmentManager();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fab.getTag().equals(TAG_LOCATIONS)) {
                    NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment);
                    SelectedLocationsFragment fragment = (SelectedLocationsFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                    fragment.newLocation();
                } else if (fab.getTag().equals(TAG_MY_LOCATION)) {

                }
            }
        });
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

    @Override
    public void fabTag(String tag) {
        fab.setTag(tag);
        if (fab.getTag().equals(TAG_LOCATIONS)) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.add));
            bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
        } else if (fab.getTag().equals(TAG_MY_LOCATION)) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_share));
            bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
        }
    }
}