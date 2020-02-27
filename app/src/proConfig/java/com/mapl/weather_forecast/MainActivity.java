package com.mapl.weather_forecast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final String PREFERENCES = "PREFERENCES";
    private boolean darkTheme;
    private int currentId = -1;

    private FloatingActionButton fab;
    private NavigationView navigationView;
    private BottomAppBar bottomAppBar;
    private DrawerLayout drawerLayout;
    private SwitchMaterial switchTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNotificationChannel();
        initView();
        setAccount();
        setFragment(savedInstanceState);
        addToggle();
    }

    private void initView() {
        navigationView = findViewById(R.id.navigationView);
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        drawerLayout = findViewById(R.id.drawerLayout);
        fab = findViewById(R.id.fabWithBottomAppBar);
        switchTheme = navigationView.getMenu().findItem(R.id.nav_theme).
                getActionView().findViewById(R.id.switchTheme);
        switchTheme.setChecked(darkTheme);
    }

    private void setAccount() {
        Intent intent = getIntent();
        GoogleSignInAccount account = intent.getParcelableExtra("ACCOUNT");
        View header = navigationView.getHeaderView(0);
        ImageView avatar = header.findViewById(R.id.avatar);
        TextView nick = header.findViewById(R.id.nick);
        TextView email = header.findViewById(R.id.email);

        if (account != null) {
            Glide.with(MainActivity.this)
                    .load(account.getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(avatar);
            nick.setText(account.getGivenName());
            email.setText(account.getEmail());
        } else {
            Glide.with(MainActivity.this)
                    .load(getResources().getDrawable(R.drawable.mapl))
                    .apply(RequestOptions.circleCropTransform())
                    .into(avatar);
        }
    }

    private void setFragment(Bundle savedInstanceState) {
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null)
            this.onNavigationItemSelected(navigationView.getMenu().getItem(0));

        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                resetTheme(isChecked);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.nav_fragment);
                if (currentFragment instanceof SelectedLocationsFragment) {
                    ((SelectedLocationsFragment) currentFragment).newLocation();
                } else if (currentFragment instanceof WeatherNearMeFragment) {
                    Toast.makeText(MainActivity.this, "Share", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_selectedLocations && id != currentId) {
            currentId = id;
            fab.setImageDrawable(getResources().getDrawable(R.drawable.add));
            bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
            fragment = new SelectedLocationsFragment();
            item.setChecked(true);
        } else if (id == R.id.nav_weatherNearMe && id != currentId) {
            currentId = id;
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_share));
            bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
            fragment = new WeatherNearMeFragment();
            item.setChecked(true);
        } else if (id == R.id.nav_theme) {
            switchTheme.setChecked(!switchTheme.isChecked());
            resetTheme(switchTheme.isChecked());
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.nav_fragment, fragment).commit();
            drawerLayout.closeDrawers();
        }
        return true;
    }

    private void addToggle() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this, drawerLayout, bottomAppBar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        darkTheme = sharedPreferences.getBoolean("DARK_THEME", true);
        if (darkTheme) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void resetTheme(boolean darkTheme) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (darkTheme) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        editor.putBoolean("DARK_THEME", darkTheme);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dispatchToFragment(requestCode, resultCode, data);
    }

    private void dispatchToFragment(int requestCode, int resultCode, Intent data) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.nav_fragment);
        if (currentFragment instanceof WeatherNearMeFragment) {
            currentFragment.onActivityResult(requestCode, resultCode, data);
        }
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
}