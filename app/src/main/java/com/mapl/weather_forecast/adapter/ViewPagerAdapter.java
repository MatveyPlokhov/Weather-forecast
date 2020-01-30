package com.mapl.weather_forecast.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mapl.weather_forecast.R;
import com.mapl.weather_forecast.model.CurrentWeather;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private Activity activity;
    private List<CurrentWeather> list;
    private Integer position = null;

    public ViewPagerAdapter(Activity activity, List<CurrentWeather> list) {
        this.activity = activity;
        if (list != null) {
            this.list = list;
        } else {
            this.list = new ArrayList<>();
        }
    }

    public ViewPagerAdapter(Activity activity, List<CurrentWeather> list, int position) {
        this.activity = activity;
        if (list != null) {
            this.list = list;
            this.position = position;
        } else {
            this.list = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_weather_forecast, parent, false);
        view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.location.setText(list.get(position).location);
        holder.description.setText(list.get(position).description);
        String temperature = activity.getResources().getString(R.string.temperature) + ": " + list.get(position).temperature;
        holder.temperature.setText(temperature);
        String feelsLike = activity.getResources().getString(R.string.feels_like) + ": " + list.get(position).feelsLike;
        holder.feelsLike.setText(feelsLike);
        String pressure = activity.getResources().getString(R.string.pressure) + ": " + list.get(position).pressure;
        holder.pressure.setText(pressure);
        String humidity = activity.getResources().getString(R.string.humidity) + ": " + list.get(position).humidity;
        holder.humidity.setText(humidity);
        holder.today.setImageResource(setWeatherImage(position));
    }

    private int setWeatherImage(int i) {
        int fullId = (int) list.get(i).weatherId;
        int id = fullId / 100;
        long currentTime = new Date().getTime();
        if (currentTime >= list.get(i).sunrise && currentTime < list.get(i).sunset) {
            switch (id) {
                case (2):
                    return R.drawable.thunderstorm;
                case (3):
                    return R.drawable.shower_rain;
                case (5):
                    if (fullId >= 500 && fullId <= 504)
                        return R.drawable.rain_d;
                    else if (fullId == 511)
                        return R.drawable.snow;
                    else
                        return R.drawable.shower_rain;
                case (6):
                    return R.drawable.snow;
                case (7):
                    return R.drawable.mist;
                case (8):
                    if (fullId == 800)
                        return R.drawable.clear_sky_d;
                    else if (fullId == 801)
                        return R.drawable.few_clouds_d;
                    else if (fullId == 802)
                        return R.drawable.scattered_clouds;
                    else if (fullId == 803 || fullId == 804)
                        return R.drawable.broken_clouds;
            }
        } else {
            switch (id) {
                case (2):
                    return R.drawable.thunderstorm;
                case (3):
                    return R.drawable.shower_rain;
                case (5):
                    if (fullId >= 500 && fullId <= 504)
                        return R.drawable.rain_n;
                    else if (fullId == 511)
                        return R.drawable.snow;
                    else
                        return R.drawable.shower_rain;
                case (6):
                    return R.drawable.snow;
                case (7):
                    return R.drawable.mist;
                case (8):
                    if (fullId == 800)
                        return R.drawable.clear_sky_n;
                    else if (fullId == 801)
                        return R.drawable.few_clouds_n;
                    else if (fullId == 802)
                        return R.drawable.scattered_clouds;
                    else if (fullId == 803 || fullId == 804)
                        return R.drawable.broken_clouds;
            }
        }
        return R.drawable.ic_launcher_foreground;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView background, today;
        TextView location, description, temperature, feelsLike, humidity, pressure;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.imageViewBackground);
            today = itemView.findViewById(R.id.imageViewWeatherToday);
            location = itemView.findViewById(R.id.textViewLocation);
            description = itemView.findViewById(R.id.textViewDescription);
            temperature = itemView.findViewById(R.id.textViewTemperature);
            feelsLike = itemView.findViewById(R.id.textViewFeelsLike);
            pressure = itemView.findViewById(R.id.textViewPressure);
            humidity = itemView.findViewById(R.id.textViewHumidity);
        }
    }
}