package com.mapl.weather_forecast.broadcastreceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.core.app.NotificationCompat;

import com.mapl.weather_forecast.R;

public class NetworkReceiver extends BroadcastReceiver {
    private int messageId = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!checkInternet(context)) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "4")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Weather Forecast")
                    .setContentText("No connection");
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(messageId++, builder.build());
        }
    }

    boolean checkInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
