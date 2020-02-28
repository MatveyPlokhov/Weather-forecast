package com.mapl.weather_forecast.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.mapl.weather_forecast.SelectedLocationsFragment;
import com.mapl.weather_forecast.WeatherNearMeFragment;
import com.mapl.weather_forecast.database.CurrentWeatherSingleton;
import com.mapl.weather_forecast.R;
import com.mapl.weather_forecast.database.dao.CurrentWeatherDao;
import com.mapl.weather_forecast.database.model.CurrentWeather;
import com.mapl.weather_forecast.rest.OpenWeatherMap;
import com.mapl.weather_forecast.rest.entities.OpenWeatherMapModel;
import com.microsoft.appcenter.Flags;
import com.microsoft.appcenter.analytics.Analytics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class WeatherForecastService extends IntentService {
    private static final String EXTRA_LIST = "com.mapl.weather_forecast.services.extra.LIST";
    private static final String EXTRA_BOOL = "com.mapl.weather_forecast.services.extra.BOOL";
    public static final String EXTRA_LIST_SEND = "com.mapl.weather_forecast.services.extra.LIST_SEND";
    public static final String EXTRA_RESULT = "com.mapl.weather_forecast.services.extra.RESULT";

    public static String RESULT_OK = "RESULT_OK";
    public static String CONNECTION_ERROR = "CONNECTION_ERROR";

    private int messageId = 0;

    public WeatherForecastService() {
        super("WeatherForecastService");
    }

    public static void startWeatherForecastService(Context context, List<CurrentWeather> list, boolean changeDatabase) {
        Intent intent = new Intent(context, WeatherForecastService.class);
        intent.putExtra(EXTRA_LIST, (ArrayList<CurrentWeather>) list);
        intent.putExtra(EXTRA_BOOL, changeDatabase);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            List<CurrentWeather> list = (List<CurrentWeather>) intent.getSerializableExtra(EXTRA_LIST);
            boolean changeDatabase = intent.getBooleanExtra(EXTRA_BOOL,false);
            if (list != null) loadLocationData(list,changeDatabase);
        }
    }

    @SuppressLint("CheckResult")
    private void loadLocationData(final List<CurrentWeather> list, boolean changeDatabase) {
        CurrentWeatherDao currentWeatherDao = CurrentWeatherSingleton.getInstance().getCurrentWeatherDao();
        boolean clientError = false, serverError = false;
        for (final CurrentWeather currentWeather : list) {
            if (!serverError) {
                try {
                    Response<OpenWeatherMapModel> response = OpenWeatherMap.getSingleton().getAPI().loadWeather(
                            currentWeather.latitude.toString(), currentWeather.longitude.toString(),
                            "8b872d9c30ed844f1fa73c7172a8313f", "metric", Locale.getDefault().getLanguage())
                            .execute();
                    OpenWeatherMapModel model = response.body();
                    if (response.isSuccessful() && model != null) {
                        currentWeather.weatherId = model.weather[0].id;
                        currentWeather.sunrise = model.sys.sunrise * 1000;
                        currentWeather.sunset = model.sys.sunset * 1000;
                        currentWeather.description = model.weather[0].description;
                        currentWeather.temperature = Math.round(model.main.temp);
                        currentWeather.feelsLike = Math.round(model.main.feels_like);
                        currentWeather.pressure = model.main.pressure;
                        currentWeather.humidity = model.main.humidity;
                    } else {
                        HashMap<String, String> connectionError = new HashMap<>();
                        connectionError.put("Location", currentWeather.location +
                                " (" + currentWeather.latitude + ", " + currentWeather.longitude + ")");
                        connectionError.put(String.valueOf(response.code()), response.message());
                        Analytics.trackEvent("Error in getting weather data",
                                connectionError, Flags.CRITICAL);
                        sendNotification(response.code() + " " + response.message());
                        serverError = true;
                    }
                } catch (IOException e) {
                    sendNotification(getResources().getString(R.string.weather_data_false) + " " +
                            currentWeather.location);
                    clientError = true;
                }
            } else break;
        }
        if (changeDatabase) {
            if (!serverError && !clientError) {
                Observable.fromCallable(new CallableAddInDatabase(list, currentWeatherDao))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<CurrentWeather>>() {
                            @Override
                            public void accept(final List<CurrentWeather> list) {
                                sendBroadcast("RESULT_OK", list,
                                        SelectedLocationsFragment.BROADCAST_ACTION_WEATHER);
                            }
                        });
            } else {
                currentWeatherDao.getWeatherListSingle()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableSingleObserver<List<CurrentWeather>>() {
                            @Override
                            public void onSuccess(List<CurrentWeather> list) {
                                sendBroadcast("CONNECTION_ERROR", list,
                                        SelectedLocationsFragment.BROADCAST_ACTION_WEATHER);
                            }

                            @Override
                            public void onError(Throwable e) {
                                sendBroadcast("CONNECTION_ERROR", new ArrayList<CurrentWeather>(),
                                        SelectedLocationsFragment.BROADCAST_ACTION_WEATHER);
                            }
                        });
            }
        } else {
            if (!serverError && !clientError) sendBroadcast("RESULT_OK", list,
                    WeatherNearMeFragment.BROADCAST_ACTION_WEATHER_MY_LOCATION);
            else sendBroadcast("CONNECTION_ERROR", new ArrayList<CurrentWeather>(),
                    WeatherNearMeFragment.BROADCAST_ACTION_WEATHER_MY_LOCATION);
        }
    }

    class CallableAddInDatabase implements Callable<List<CurrentWeather>> {
        private final CurrentWeatherDao currentWeatherDao;
        private final List<CurrentWeather> list;

        CallableAddInDatabase(List<CurrentWeather> list, CurrentWeatherDao currentWeatherDao) {
            this.list = list;
            this.currentWeatherDao = currentWeatherDao;
        }

        @Override
        public List<CurrentWeather> call() {
            for (CurrentWeather currentWeather : list) {
                currentWeatherDao.insertCurrentWeather(currentWeather);
            }
            return currentWeatherDao.getWeatherList();
        }
    }

    private void sendBroadcast(String result, List<CurrentWeather> list, String action) {
        Intent broadcastIntent = new Intent(action);
        broadcastIntent.putExtra(EXTRA_RESULT, result);
        broadcastIntent.putExtra(EXTRA_LIST_SEND, (ArrayList<CurrentWeather>) list);
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