package com.mapl.weather_forecast.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class WeatherForecast extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "weatherForecastDb";
    public static final String TABLE_WEATHER_FORECAST = "weatherForecast";

    private static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LON = "lon";

    public WeatherForecast(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_WEATHER_FORECAST + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_LAT + " REAL,"
                + KEY_LON + " REAL" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists weatherForecast");
        onCreate(db);
    }
}
