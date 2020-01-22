package com.mapl.weather_forecast.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.mapl.weather_forecast.MainActivity;
import com.mapl.weather_forecast.R;
import com.mapl.weather_forecast.loaders.WeatherDataLoader;
import com.mapl.weather_forecast.loaders.WeatherJSONParsing;

import org.json.JSONObject;

import java.util.HashMap;

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
            Double lat = intent.getDoubleExtra(EXTRA_LAT, 0);
            Double lon = intent.getDoubleExtra(EXTRA_LON, 0);
            JSONObject jsonObject = WeatherDataLoader.getJSONData(lat, lon);
            checkJSON(intent, jsonObject);
        }
    }

    private void checkJSON(Intent intent, JSONObject jsonObject) {
        if (jsonObject == null) {
            sendNotification(getResources().getString(R.string.weather_data_false));
        } else {
            String location = intent.getStringExtra(EXTRA_LOCATION);
            WeatherJSONParsing weatherJSONParsing = new WeatherJSONParsing(jsonObject);
            HashMap<String, String> dataDetails = weatherJSONParsing.getDetails();
            long[] arrayForIcon = weatherJSONParsing.getDetailsForIcon();
            sendBroadcast(location, dataDetails, arrayForIcon);
        }
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
