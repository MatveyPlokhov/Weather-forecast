package com.mapl.weather_forecast.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import androidx.core.app.NotificationCompat;

import com.mapl.weather_forecast.CurrentWeatherSingleton;
import com.mapl.weather_forecast.MainActivity;
import com.mapl.weather_forecast.R;
import com.mapl.weather_forecast.dao.CurrentWeatherDao;
import com.mapl.weather_forecast.model.CurrentWeather;
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

    public static String EXTRA_RESULT = "com.mapl.weather_forecast.services.extra.result.RESULT";
    public static String EXTRA_MESSAGE = "com.mapl.weather_forecast.services.extra.result.MESSAGE";

    public static String RESULT_OK = "RESULT_OK";
    public static String SERVER_ERROR = "SERVER_ERROR";
    public static String CONNECTION_ERROR = "CONNECTION_ERROR";

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
                            WeatherParsing weatherParsing = new WeatherParsing(response.body(),location, lat, lon);
                            weatherParsing.execute();
                        } else {
                            HashMap<String, String> connectionError = new HashMap<>();
                            connectionError.put("Location", location + " (" + lat + ", " + lon + ")");
                            connectionError.put(String.valueOf(response.code()), response.message());
                            Analytics.trackEvent("Error in getting weather data",
                                    connectionError, Flags.CRITICAL);
                            sendNotification(response.code() + " " + response.message());
                            sendBroadcast("SERVER_ERROR", response.code() + " " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<OpenWeatherMapModel> call, Throwable t) {
                        sendNotification(getResources().getString(R.string.weather_data_false));
                        sendBroadcast("CONNECTION_ERROR");
                    }
                });
    }

    private void sendBroadcast(String result) {
        Intent broadcastIntent = new Intent(MainActivity.BROADCAST_ACTION_WEATHER);
        broadcastIntent.putExtra(EXTRA_RESULT, result);
        sendBroadcast(broadcastIntent);
    }

    private void sendBroadcast(String result, String message) {
        Intent broadcastIntent = new Intent(MainActivity.BROADCAST_ACTION_WEATHER);
        broadcastIntent.putExtra(EXTRA_RESULT, result);
        broadcastIntent.putExtra(EXTRA_MESSAGE, message);
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

    @SuppressLint("StaticFieldLeak")
    class WeatherParsing extends AsyncTask<View, Integer, View> {
        private OpenWeatherMapModel model;
        private String location;
        private Double lat, lon;

        WeatherParsing(OpenWeatherMapModel model, String location, Double lat, Double lon) {
            this.model = model;
            this.location = location;
            this.lat = lat;
            this.lon = lon;
        }

        @Override
        protected View doInBackground(View... views) {
            CurrentWeatherDao currentWeatherDao = CurrentWeatherSingleton.getInstance().getCurrentWeatherDao();
            CurrentWeather currentWeather = new CurrentWeather();
            currentWeather.location = location;
            currentWeather.latitude = lat;
            currentWeather.longitude = lon;
            currentWeather.weatherId = model.weather[0].id;
            currentWeather.sunrise = model.sys.sunrise * 1000;
            currentWeather.sunset = model.sys.sunset * 1000;
            currentWeather.description = model.weather[0].description;
            currentWeather.temperature = Math.round(model.main.temp);
            currentWeather.feelsLike = Math.round(model.main.feels_like);
            currentWeather.pressure = model.main.pressure;
            currentWeather.humidity = model.main.humidity;
            currentWeatherDao.insertCurrentWeather(currentWeather);
            return null;
        }

        @Override
        protected void onPostExecute(View view) {
            sendBroadcast("RESULT_OK");
        }
    }
}
