package com.mapl.weather_forecast.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.mapl.weather_forecast.MainActivity;
import com.mapl.weather_forecast.R;
import com.mapl.weather_forecast.rest.OpenWeatherMap;
import com.mapl.weather_forecast.rest.entities.OpenWeatherMapModel;
import com.microsoft.appcenter.Flags;
import com.microsoft.appcenter.analytics.Analytics;

import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherForecastService extends IntentService {
    private static final String EXTRA_LOCATION = "com.mapl.weather_forecast.services.extra.LOCATION";
    private static final String EXTRA_LAT = "com.mapl.weather_forecast.services.extra.LAT";
    private static final String EXTRA_LON = "com.mapl.weather_forecast.services.extra.LON";

    public static String EXTRA_RESULT_LOCATION = "com.mapl.weather_forecast.services.extra.result.LOCATION";
    public static String EXTRA_RESULT_HASH_MAP = "com.mapl.weather_forecast.services.extra.result.HASH_MAP";
    public static String EXTRA_RESULT_LONG_ARRAY = "com.mapl.weather_forecast.services.extra.result.LONG_ARRAY";

    private int messageId = 0;

    public WeatherForecastService() {
        super("WeatherForecastService");
    }

    public static void startWeatherForecastService(Context context, String LOCATION, Double LAT, Double LON) {
        Intent intent = new Intent(context, WeatherForecastService.class);
        intent.putExtra(EXTRA_LOCATION, LOCATION);
        intent.putExtra(EXTRA_LAT, LAT);
        intent.putExtra(EXTRA_LON, LON);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String location = intent.getStringExtra(EXTRA_LOCATION);
            Double lat = intent.getDoubleExtra(EXTRA_LAT, 0);
            Double lon = intent.getDoubleExtra(EXTRA_LON, 0);
            loadData(location, lat, lon);
        }
    }

    private void loadData(final String location, final Double lat, final Double lon) {
        OpenWeatherMap.getSingleton().getAPI().loadWeather(lat.toString(), lon.toString(),
                "8b872d9c30ed844f1fa73c7172a8313f", "metric", Locale.getDefault().getLanguage())
                .enqueue(new Callback<OpenWeatherMapModel>() {
                    @Override
                    public void onResponse(Call<OpenWeatherMapModel> call, Response<OpenWeatherMapModel> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            WeatherParsing weatherParsing = new WeatherParsing(response.body());
                            HashMap<String, String> dataDetails = weatherParsing.getDetails();
                            long[] arrayForIcon = weatherParsing.getDetailsForIcon();
                            sendBroadcast(location, dataDetails, arrayForIcon);
                        } else {
                            Toast.makeText(getBaseContext(), response.code() + " " + response.message(),
                                    Toast.LENGTH_SHORT).show();
                            HashMap<String, String> connectionError = new HashMap<>();
                            connectionError.put("Location", location + " (" + lat + ", " + lon + ")");
                            connectionError.put(String.valueOf(response.code()), response.message());
                            Analytics.trackEvent("Error in getting weather data",
                                    connectionError, Flags.CRITICAL);
                        }
                    }

                    @Override
                    public void onFailure(Call<OpenWeatherMapModel> call, Throwable t) {
                        sendNotification(getResources().getString(R.string.weather_data_false));
                    }
                });
    }

    private void sendBroadcast(String location, HashMap<String, String> dataDetails, long[] arrayForIcon) {
        Intent broadcastIntent = new Intent(MainActivity.BROADCAST_ACTION_WEATHER);
        broadcastIntent.putExtra(EXTRA_RESULT_LOCATION, location);
        broadcastIntent.putExtra(EXTRA_RESULT_HASH_MAP, dataDetails);
        broadcastIntent.putExtra(EXTRA_RESULT_LONG_ARRAY, arrayForIcon);
        sendBroadcast(broadcastIntent);
    }

    private void sendNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "2")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Weather Forecast")
                .setContentText(message);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(messageId++, builder.build());
        }
    }
}
