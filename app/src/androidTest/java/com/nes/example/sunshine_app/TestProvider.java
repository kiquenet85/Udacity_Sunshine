package com.nes.example.sunshine_app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Map;
import java.util.Set;

import data.WeatherContract.LocationEntry;
import data.WeatherContract.WeatherEntry;
import data.WeatherDBHelper;

/**
 * Created by user on 09/09/2014.
 */
public class TestProvider extends AndroidTestCase {


    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    //--Weather data
    public static String testDateText = "20141205";
    public static String testLongDesc = "This is the test long description";
    public static String testShortDesc = "short Desc";
    public static Double testDegrees= 1.2;
    public static Double testHumidity = 1.1;
    public static Double testPressure = 1.3;
    public static int testLocKey = 1;
    public static  int testMaxTemp = 75;
    public static  int testMinTemp = 65;
    public static  int testWeatherId = 321;
    public static  Double testWindSpeed = -5.5;

    //--Location data
    public static  String testName = "North Pole";
    public static String testLocationSetting = "99785";
    public static Double testLatitude = 64.772;
    public static Double testLongitude = -147.355;

    public void testDeleteDb() throws Throwable {
        mContext.deleteDatabase(WeatherDBHelper.DATABASE_NAME);
    }

    public void testInsertReadProvider() {
        SQLiteDatabase db = new WeatherDBHelper(
                this.mContext).getWritableDatabase();

        ContentValues locationValues = createLocationValues();

        /*long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, locationValues);
        assert (locationRowId != -1);*/

        Uri locationInsertUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, locationValues);
        assertTrue(locationInsertUri != null);
        long locationRowId=ContentUris.parseId(locationInsertUri);
        Log.d(LOG_TAG, "New Row Id: " + locationRowId);

        String[] locationColumns = {
                LocationEntry.COLUMN_CITY_NAME,
                LocationEntry.COLUMN_LOCATION_SETTING,
                LocationEntry.COLUMN_COORD_LAT,
                LocationEntry.COLUMN_COORD_LON
        };

        /*Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,
                locationColumns,
                null, //columns for WHERE
                null, //values for WHERE
                null, //columns to group by
                null, //columns to filter by row groups
                null  //sort order
        );*/
        Cursor cursor=mContext.getContentResolver().query(LocationEntry.CONTENT_URI,
                locationColumns,
                null, //columns for WHERE
                null, //values for WHERE
               //-- null, //columns to group by -- not in Content Provider
               //-- null, //columns to filter by row groups --  not in Content Provider null
                null  //sort order
         );

       validateCursor(cursor, locationValues);

       ContentValues weatherValues = createWeatherValues(locationRowId);

        /*    long weatherRowId;
            weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
            assert (weatherRowId != -1);*/

        Uri weatherInsertUri = mContext.getContentResolver().insert(WeatherEntry.CONTENT_URI, weatherValues);
        assertTrue(weatherInsertUri != null);
        long weatherRowId=ContentUris.parseId(weatherInsertUri);
        Log.d(LOG_TAG, "New Row Id: " + weatherRowId);

            String[] weatherColumns = {
                    WeatherEntry.COLUMN_WEATHER_ID,
                    WeatherEntry.COLUMN_DATETEXT,
                    WeatherEntry.COLUMN_DEGREES,
                    WeatherEntry.COLUMN_HUMIDITY,
                    WeatherEntry.COLUMN_PRESSURE,
                    WeatherEntry.COLUMN_LOC_KEY,
                    WeatherEntry.COLUMN_LONG_DESC,
                    WeatherEntry.COLUMN_SHORT_DESC,
                    WeatherEntry.COLUMN_MAX_TEMP,
                    WeatherEntry.COLUMN_MIN_TEMP,
                    WeatherEntry.COLUMN_WIND_SPEED
            };

           /* cursor = db.query(
                    WeatherEntry.TABLE_NAME,
                    weatherColumns,
                    null, //columns for WHERE
                    null, //values for WHERE
                    null, //columns to group by
                    null, //columns to filter by row groups
                    null  //sort order
            );*/
        cursor=mContext.getContentResolver().query(WeatherEntry.CONTENT_URI,
                weatherColumns,
                null, //columns for WHERE
                null, //values for WHERE
                //-- null, //columns to group by -- not in Content Provider
                //-- null, //columns to filter by row groups --  not in Content Provider null
                null  //sort order
        );

        validateCursor(cursor, weatherValues);
        cursor.close();

        //-- Another way to implement the above query.
        cursor=mContext.getContentResolver().query(WeatherEntry.buildWeatherLocation(testLocationSetting),
                weatherColumns,
                null, //columns for WHERE
                null, //values for WHERE
                //-- null, //columns to group by -- not in Content Provider
                //-- null, //columns to filter by row groups --  not in Content Provider null
                null  //sort order
        );

        validateCursor(cursor, weatherValues);

        cursor.close();

        cursor=mContext.getContentResolver().query(WeatherEntry.buildWeatherLocationWithStartDate(testLocationSetting, testDateText),
                weatherColumns,
                null, //columns for WHERE
                null, //values for WHERE
                //-- null, //columns to group by -- not in Content Provider
                //-- null, //columns to filter by row groups --  not in Content Provider null
                null  //sort order
        );

        validateCursor(cursor, weatherValues);

        cursor.close();

        cursor=mContext.getContentResolver().query(WeatherEntry.buildWeatherLocationWithDate(testLocationSetting, testDateText),
                weatherColumns,
                null, //columns for WHERE
                null, //values for WHERE
                //-- null, //columns to group by -- not in Content Provider
                //-- null, //columns to filter by row groups --  not in Content Provider null
                null  //sort order
        );

        validateCursor(cursor, weatherValues);

        db.close();

    }

    static ContentValues createWeatherValues(long locationRowId) {

        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, testDateText);
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, testDegrees);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, testHumidity);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, testPressure);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, testMaxTemp);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, testMinTemp);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, testShortDesc);
        weatherValues.put(WeatherEntry.COLUMN_LONG_DESC, testLongDesc);
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, testWindSpeed);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, testWeatherId);


        return weatherValues;
    }

    static ContentValues createLocationValues() {
        // Create a new map of values, where column names are the keys
        ContentValues locationValues = new ContentValues();
        locationValues.put(LocationEntry.COLUMN_CITY_NAME, testName);
        locationValues.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        locationValues.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        locationValues.put(LocationEntry.COLUMN_COORD_LON, testLongitude);
        return locationValues;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }

    public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "94074";
        // content://com.example.android.sunshine.app/weather/94074
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocation(testLocation));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testDate = "20140612";
        // content://com.example.android.sunshine.app/weather/94074/20140612
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));
        // vnd.android.cursor.item/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/location/1
        type = mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));
        // vnd.android.cursor.item/com.example.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_ITEM_TYPE, type);
    }
}