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
import com.mapl.weather_forecast.database.model.CurrentWeather;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private static ViewPagerAdapter instance;
    private Activity activity;
    private List<CurrentWeather> list;

    private ViewPagerAdapter(Activity activity, List<CurrentWeather> list) {
        this.activity = activity;
        if (list != null) {
            this.list = list;
        } else {
            this.list = new ArrayList<>();
        }
    }

    public static ViewPagerAdapter getInstance(Activity activity, List<CurrentWeather> list) {
        if (instance == null) instance = new ViewPagerAdapter(activity, list);
        return instance;
    }

    public void refreshData(List<CurrentWeather> list) {
        this.list = list;
        notifyDataSetChanged();
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
        holder.description.setText(list.get(position).description);
        String temperature = activity.getResources().getString(R.string.temperature) + ": " + list.get(position).temperature;
        holder.temperature.setText(temperature);
        String feelsLike = activity.getResources().getString(R.string.feels_like) + ": " + list.get(position).feelsLike;
        holder.feelsLike.setText(feelsLike);
        String pressure = activity.getResources().getString(R.string.pressure) + ": " + list.get(position).pressure;
        holder.pressure.setText(pressure);
        String humidity = activity.getResources().getString(R.string.humidity) + ": " + list.get(position).humidity;
        holder.humidity.setText(humidity);
        setWeatherImage(position, holder.todayIcon, holder.backgroundView);
    }

    private void setWeatherImage(int i, ImageView todayIcon, View background) {
        int id = (int) list.get(i).weatherId;
        Integer iconId = null, gradient = null;
        long currentTime = new Date().getTime();
        if (currentTime >= list.get(i).sunrise && currentTime < list.get(i).sunset) {
            if (id >= 200 && id <= 232) {
                if (id <= 202) iconId = R.drawable.ic_weather_mix_rainfall;
                if (id == 210 || id == 211) iconId = R.drawable.ic_weather_scattered_thunderstorm;
                if (id == 212 || id == 221) iconId = R.drawable.ic_weather_severe_thunderstorm;
                if (id >= 230) iconId = R.drawable.ic_weather_mix_rainfall;
                gradient = R.drawable.gr_thunderstorm;
            } else if (id >= 300 && id <= 321) {
                if (id == 300) iconId = R.drawable.ic_weather_scattered_showers;
                if (id == 301) iconId = R.drawable.ic_weather_drizzle;
                if (id == 302) iconId = R.drawable.ic_weather_rain;
                if (id == 310 || id == 311) iconId = R.drawable.ic_weather_drizzle;
                if (id == 312 || id == 313) iconId = R.drawable.ic_weather_rain;
                if (id == 314 || id == 321) iconId = R.drawable.ic_weather_heavy_rain;
                gradient = R.drawable.gr_clouds;
            } else if (id >= 500 && id <= 531) {
                if (id == 500) iconId = R.drawable.ic_weather_scattered_showers;
                if (id == 501) iconId = R.drawable.ic_weather_drizzle;
                if (id == 502 || id == 503) iconId = R.drawable.ic_weather_rain;
                if (id == 504) iconId = R.drawable.ic_weather_heavy_rain;
                if (id == 511) iconId = R.drawable.ic_weather_sleet;
                if (id == 520 || id == 521) iconId = R.drawable.ic_weather_rain;
                if (id == 522 || id == 531) iconId = R.drawable.ic_weather_heavy_rain;
                gradient = R.drawable.gr_clouds;
            } else if (id >= 600 && id <= 622) {
                if (id <= 602) iconId = R.drawable.ic_weather_snow;
                if (id >= 611 && id <= 616) iconId = R.drawable.ic_weather_sleet;
                if (id == 620 || id == 621) iconId = R.drawable.ic_weather_snow;
                if (id == 622) iconId = R.drawable.ic_weather_blizzard;
                gradient = R.drawable.gr_snow;
            } else if (id >= 701 && id <= 781) {
                if (id == 701) iconId = R.drawable.ic_weather_fog;
                if (id == 711) iconId = R.drawable.ic_weather_smoke;
                if (id == 721) iconId = R.drawable.ic_weather_haze;
                if (id == 731) iconId = R.drawable.ic_weather_dust;
                if (id == 741) iconId = R.drawable.ic_weather_fog;
                if (id == 751 || id == 761) iconId = R.drawable.ic_weather_dust;
                if (id == 762) iconId = R.drawable.ic_weather_smoke;
                if (id == 771) iconId = R.drawable.ic_weather_breezy;
                if (id == 781) iconId = R.drawable.ic_weather_tornado;
                gradient = R.drawable.gr_clouds;
            } else if (id == 800) {
                iconId = R.drawable.ic_weather_mostly_sunny;
                gradient = R.drawable.gr_clear;
            } else if (id >= 801 && id <= 804) {
                if (id == 801) iconId = R.drawable.ic_weather_party_cloudy;
                if (id == 802) iconId = R.drawable.ic_weather_mostly_cloudy;
                if (id == 803) iconId = R.drawable.ic_weather_mostly_cloudy;
                if (id == 804) iconId = R.drawable.ic_weather_smoke;
                gradient = R.drawable.gr_clouds;
            }
        } else {
            if (id >= 200 && id <= 232) {
                if (id <= 202) iconId = R.drawable.ic_weather_mix_rainfall_night;
                if (id == 210 || id == 211)
                    iconId = R.drawable.ic_weather_scattered_thunderstorm_night;
                if (id == 212 || id == 221)
                    iconId = R.drawable.ic_weather_severe_thunderstorm_night;
                if (id >= 230) iconId = R.drawable.ic_weather_mix_rainfall_night;
                gradient = R.drawable.gr_thunderstorm;
            } else if (id >= 300 && id <= 321) {
                if (id == 300) iconId = R.drawable.ic_weather_scattered_showers_night;
                if (id == 301) iconId = R.drawable.ic_weather_drizzle_night;
                if (id == 302) iconId = R.drawable.ic_weather_rain_nght;
                if (id == 310 || id == 311) iconId = R.drawable.ic_weather_drizzle_night;
                if (id == 312 || id == 313) iconId = R.drawable.ic_weather_rain_nght;
                if (id == 314 || id == 321) iconId = R.drawable.ic_weather_heavy_rain_night;
                gradient = R.drawable.gr_clouds;
            } else if (id >= 500 && id <= 531) {
                if (id == 500) iconId = R.drawable.ic_weather_scattered_showers_night;
                if (id == 501) iconId = R.drawable.ic_weather_drizzle_night;
                if (id == 502 || id == 503) iconId = R.drawable.ic_weather_rain_nght;
                if (id == 504) iconId = R.drawable.ic_weather_heavy_rain_night;
                if (id == 511) iconId = R.drawable.ic_weather_sleet_night;
                if (id == 520 || id == 521) iconId = R.drawable.ic_weather_rain_nght;
                if (id == 522 || id == 531) iconId = R.drawable.ic_weather_heavy_rain_night;
                gradient = R.drawable.gr_clouds;
            } else if (id >= 600 && id <= 622) {
                if (id <= 602) iconId = R.drawable.ic_weather_snow_night;
                if (id >= 611 && id <= 616) iconId = R.drawable.ic_weather_sleet_night;
                if (id == 620 || id == 621) iconId = R.drawable.ic_weather_snow_night;
                if (id == 622) iconId = R.drawable.ic_weather_blizzard_night;
                gradient = R.drawable.gr_snow;
            } else if (id >= 701 && id <= 781) {
                if (id == 701) iconId = R.drawable.ic_weather_fog_night;
                if (id == 711) iconId = R.drawable.ic_weather_smoke;
                if (id >= 721 && id <= 761) iconId = R.drawable.ic_weather_fog_night;
                if (id == 762) iconId = R.drawable.ic_weather_smoke;
                if (id == 771) iconId = R.drawable.ic_weather_breezy;
                if (id == 781) iconId = R.drawable.ic_weather_tornado;
                gradient = R.drawable.gr_clouds;
            } else if (id == 800) {
                iconId = R.drawable.ic_weather_clear_night;
                gradient = R.drawable.gr_clear;
            } else if (id >= 801 && id <= 804) {
                if (id == 801) iconId = R.drawable.ic_weather_party_cloudy_night;
                if (id == 802) iconId = R.drawable.ic_weather_mostly_cloudy_night;
                if (id == 803) iconId = R.drawable.ic_weather_mostly_cloudy;
                if (id == 804) iconId = R.drawable.ic_weather_smoke;
                gradient = R.drawable.gr_clouds;
            }
        }
        if (iconId != null) {
            todayIcon.setImageResource(iconId);
            background.setBackgroundResource(gradient);
        } else {
            todayIcon.setImageResource(R.drawable.ic_error);
            background.setBackgroundResource(R.drawable.gr_thunderstorm);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView todayIcon;
        View backgroundView;
        TextView description, temperature, feelsLike, humidity, pressure;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            todayIcon = itemView.findViewById(R.id.imageViewWeatherToday);
            description = itemView.findViewById(R.id.textViewDescription);
            temperature = itemView.findViewById(R.id.textViewTemperature);
            feelsLike = itemView.findViewById(R.id.textViewFeelsLike);
            pressure = itemView.findViewById(R.id.textViewPressure);
            humidity = itemView.findViewById(R.id.textViewHumidity);
            backgroundView = itemView.findViewById(R.id.backgroundView);
        }
    }
}