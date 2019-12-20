package com.mapl.weather_forecast.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class WeatherForecast extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "weatherForecastDb";
    public static final String TABLE_WEATHER_FORECAST = "weatherForecast";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_TEMPERATURE_DAY_1 = "temperatureDay1";
    public static final String KEY_TEMPERATURE_NIGHT_1 = "temperatureNight1";
    public static final String KEY_TEMPERATURE_DAY_2 = "temperatureDay2";
    public static final String KEY_TEMPERATURE_NIGHT_2 = "temperatureNight2";
    public static final String KEY_TEMPERATURE_DAY_3 = "temperatureDay3";
    public static final String KEY_TEMPERATURE_NIGHT_3 = "temperatureNight3";
    public static final String KEY_TEMPERATURE_DAY_4 = "temperatureDay4";
    public static final String KEY_TEMPERATURE_NIGHT_4 = "temperatureNight4";
    public static final String KEY_TEMPERATURE_DAY_5 = "temperatureDay5";
    public static final String KEY_TEMPERATURE_NIGHT_5 = "temperatureNight5";
    public static final String KEY_TEMPERATURE_DAY_6 = "temperatureDay6";
    public static final String KEY_TEMPERATURE_NIGHT_6 = "temperatureNight6";
    public static final String KEY_TEMPERATURE_DAY_7 = "temperatureDay7";
    public static final String KEY_TEMPERATURE_NIGHT_7 = "temperatureNight7";

    public WeatherForecast(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_WEATHER_FORECAST + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_TEMPERATURE_DAY_1 + " INTEGER,"
                + KEY_TEMPERATURE_NIGHT_1 + " INTEGER,"
                + KEY_TEMPERATURE_DAY_2 + " INTEGER,"
                + KEY_TEMPERATURE_NIGHT_2 + " INTEGER,"
                + KEY_TEMPERATURE_DAY_3 + " INTEGER,"
                + KEY_TEMPERATURE_NIGHT_3 + " INTEGER,"
                + KEY_TEMPERATURE_DAY_4 + " INTEGER,"
                + KEY_TEMPERATURE_NIGHT_4 + " INTEGER,"
                + KEY_TEMPERATURE_DAY_5 + " INTEGER,"
                + KEY_TEMPERATURE_NIGHT_5 + " INTEGER,"
                + KEY_TEMPERATURE_DAY_6 + " INTEGER,"
                + KEY_TEMPERATURE_NIGHT_6 + " INTEGER,"
                + KEY_TEMPERATURE_DAY_7 + " INTEGER,"
                + KEY_TEMPERATURE_NIGHT_7 + " INTEGER" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists weatherForecast");
        onCreate(db);
    }
}
