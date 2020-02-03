package com.mapl.weather_forecast.broadcastreceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import androidx.core.app.NotificationCompat;

import com.mapl.weather_forecast.R;

public class BatteryReceiver extends BroadcastReceiver {
    private int messageId = 0;
    private boolean check = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        if (level == 20 && check) {
            check = false;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "3")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Weather Forecast")
                    .setContentText("Low battery");
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(messageId++, builder.build());
        } else if (level >= 70) check = true;
    }
}
