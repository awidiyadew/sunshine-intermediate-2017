package cf.awidiyadew.sunshine_intermediate_class.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cf.awidiyadew.sunshine_intermediate_class.model.ApiResponse;
import cf.awidiyadew.sunshine_intermediate_class.model.City;
import cf.awidiyadew.sunshine_intermediate_class.model.ListForecast;
import cf.awidiyadew.sunshine_intermediate_class.model.Temp;
import cf.awidiyadew.sunshine_intermediate_class.model.WeatherItem;


/**
 * Created by awidiyadew on 5/20/17.
 */

public class ForecastDBHelper extends SQLiteOpenHelper {

    private static final String TAG = ForecastDBHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "sunshine.db";
    private static final int DATABASE_VERSION = 1;

    public ForecastDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_DATABASE_SQL = "CREATE TABLE " + ForecastContract.ForecastEntry.TABLE_NAME + " ("
                + ForecastContract.ForecastEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ForecastContract.ForecastEntry.COLUMN_CITY_NAME + " TEXT, "
                + ForecastContract.ForecastEntry.COLUMN_EPOCH_TIME + " INTEGER, "
                + ForecastContract.ForecastEntry.COLUMN_MAX_TEMP + " REAL, "
                + ForecastContract.ForecastEntry.COLUMN_MIN_TEMP + " REAL, "
                + ForecastContract.ForecastEntry.COLUMN_HUMIDITY + " INTEGER, "
                + ForecastContract.ForecastEntry.COLUMN_PRESSURE + " REAL, "
                + ForecastContract.ForecastEntry.COLUMN_WEATHER_ID + " INTEGER, "
                + ForecastContract.ForecastEntry.COLUMN_WEATHER_MAIN + " TEXT, "
                + ForecastContract.ForecastEntry.COLUMN_WEATHER_DESCRIPTION + " TEXT, "
                + ForecastContract.ForecastEntry.COLUMN_WIND_SPEED + " REAL, "
                + ForecastContract.ForecastEntry.COLUMN_TIMESTAMP + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')));";

        db.execSQL(CREATE_DATABASE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + ForecastContract.ForecastEntry.TABLE_NAME);
        onCreate(db);
    }

    public void saveForecast(City city, ListForecast data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ForecastContract.ForecastEntry.COLUMN_CITY_NAME, city.getName());
        cv.put(ForecastContract.ForecastEntry.COLUMN_EPOCH_TIME, data.getDt());
        cv.put(ForecastContract.ForecastEntry.COLUMN_MAX_TEMP, data.getTemp().getMax());
        cv.put(ForecastContract.ForecastEntry.COLUMN_MIN_TEMP, data.getTemp().getMin());
        cv.put(ForecastContract.ForecastEntry.COLUMN_PRESSURE, data.getPressure());
        cv.put(ForecastContract.ForecastEntry.COLUMN_HUMIDITY, data.getHumidity());
        cv.put(ForecastContract.ForecastEntry.COLUMN_WEATHER_ID, data.getWeather().get(0).getId());
        cv.put(ForecastContract.ForecastEntry.COLUMN_WEATHER_MAIN, data.getWeather().get(0).getMain());
        cv.put(ForecastContract.ForecastEntry.COLUMN_WEATHER_DESCRIPTION, data.getWeather().get(0).getDescription());
        cv.put(ForecastContract.ForecastEntry.COLUMN_WIND_SPEED, data.getSpeed());

        long result = db.insert(ForecastContract.ForecastEntry.TABLE_NAME, null, cv);
        Log.i(TAG, "saveForecast result -> " + result);
        db.close();
    }

    public ApiResponse getSavedForecast(String city) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ListForecast> weatherItems = new ArrayList<>();

        //create City, which will be added to DailyForecast
        City resultCity = new City();
        resultCity.setName(city);

        //create DailyForecast, which will be filled with WeatherItem
        ApiResponse result = new ApiResponse();
        //set city to DailyForecast
        result.setCity(resultCity);
        //set 0-sized list of WeatherItem
        result.setList(weatherItems);

        Cursor cursor = db.query(ForecastContract.ForecastEntry.TABLE_NAME,
                null,
                ForecastContract.ForecastEntry.COLUMN_CITY_NAME + "=?",
                new String[]{city},
                null,
                null,
                null,
                null);
        int total = cursor.getCount();
        if (total > 0) {
            if (cursor.moveToFirst()) {
                do {
                    // PREPARING MODEL
                    //create list weathers, which is only one item inside
                    List<WeatherItem> listWeatherItems = new ArrayList<>();
                    //create the WeatherItem object, will be inserted to list above
                    WeatherItem weathers = new WeatherItem();
                    //add the WeatherItem object above to list
                    listWeatherItems.add(weathers);

                    //create temp object
                    Temp temp = new Temp();

                    //create WeatherItem object, which will be used to store 16 day forecast
                    ListForecast item = new ListForecast();
                    //set WeatherItem list to WeatherItem
                    item.setWeather(listWeatherItems);
                    //set Temp to WeatherItem
                    item.setTemp(temp);

                    //getting all data from cursor, and set it to WeatherItem
                    item.setDt(cursor.getInt(cursor.getColumnIndex(ForecastContract.ForecastEntry.COLUMN_EPOCH_TIME)));
                    item.getWeather().get(0).setId(cursor.getInt(cursor.getColumnIndex(ForecastContract.ForecastEntry.COLUMN_WEATHER_ID)));
                    item.getWeather().get(0).setDescription(cursor.getString(cursor.getColumnIndex(ForecastContract.ForecastEntry.COLUMN_WEATHER_DESCRIPTION)));
                    item.getTemp().setMax(cursor.getDouble(cursor.getColumnIndex(ForecastContract.ForecastEntry.COLUMN_MAX_TEMP)));
                    item.getTemp().setMin(cursor.getDouble(cursor.getColumnIndex(ForecastContract.ForecastEntry.COLUMN_MIN_TEMP)));

                    item.setHumidity(cursor.getInt(cursor.getColumnIndex(ForecastContract.ForecastEntry.COLUMN_HUMIDITY)));
                    item.setPressure(cursor.getDouble(cursor.getColumnIndex(ForecastContract.ForecastEntry.COLUMN_PRESSURE)));
                    item.setSpeed(cursor.getDouble(cursor.getColumnIndex(ForecastContract.ForecastEntry.COLUMN_WIND_SPEED)));

                    //finally, add WeatherItem to DailyForecast
                    result.getList().add(item);
                } while (cursor.moveToNext());
            }
        } else {
            //data not found
            Log.w(TAG, "getSavedForecast not found any data!");
        }
        cursor.close();
        db.close();

        Log.d(TAG, "result -> " + result.toString());
        return result;
    }

    public boolean isDataAlreadyExist(String city) {
        SQLiteDatabase db = this.getReadableDatabase();
        /*Cursor cursor = db.query(
                true,
                ForecastContract.ForecastEntry.TABLE_NAME,
                null,
                ForecastContract.ForecastEntry.COLUMN_CITY_NAME + " LIKE ?",
                new String[]{"%" + city + "%"},
                null,
                null,
                null,
                null);*/

        final String sql = "SELECT * FROM "
                + ForecastContract.ForecastEntry.TABLE_NAME
                + " WHERE "
                + ForecastContract.ForecastEntry.COLUMN_CITY_NAME
                + " LIKE '%" + city + "%';";

        Cursor cursor = db.rawQuery(sql,null);

        int total = cursor.getCount();
        Log.d(TAG,"isDataAlreadyExist total -> "+total);
        cursor.close();
        db.close();
        return total > 0;
    }

    public void deleteForUpdate(String city) {
        SQLiteDatabase db = this.getWritableDatabase();
        final String sql = "DELETE FROM "
                + ForecastContract.ForecastEntry.TABLE_NAME
                + " WHERE "
                + ForecastContract.ForecastEntry.COLUMN_CITY_NAME
                + " LIKE '%" + city + "%';";
        db.execSQL(sql);
        db.close();
    }
}
