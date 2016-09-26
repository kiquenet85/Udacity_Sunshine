package data;

import android.database.sqlite.SQLiteQueryBuilder;

/**
 * Created by user on 19/09/2014.
 */
public class CrudOperations {
    public static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    static{
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sWeatherByLocationSettingQueryBuilder.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry._ID);
    }

    //-- QUERY BASED ON JOIN Location and Weather
    public static final String sLocationSettingSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";
    public static final String sLocationSettingWithStartDateSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATETEXT + " >= ? ";

    public static final String sLocationSettingAndDaySelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATETEXT + " = ? ";

    //-- DELETE --------------------------------------  Location
    //----------------------------------------------------------------------------------------------
    //-- http://www.sqlite.org/foreignkeys.html

    public static final String sLocationIdDelete =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry._ID + " = ? ";

    public static final String sLocationSettingDelete =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";

    public static final String sLocationSettingWithDateBeforeOfDelete =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATETEXT + " < ? ";

    public static final String sLocationSettingWithStartDateDelete =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATETEXT + " >= ? ";

    public static final String sLocationSettingAndDayDelete=
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATETEXT + " = ? ";

    //-- DELETE --------------------------------------  Weather
    //----------------------------------------------------------------------------------------------

    public static final String sWeathersOfLocationSettingDelete=
            WeatherContract.WeatherEntry.TABLE_NAME+
                    "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY + " = ? ";

    public static final String sWeathersOfLocationSettingWithDateBeforeOfDelete =
            WeatherContract.WeatherEntry.TABLE_NAME+
                    "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATETEXT + " < ? ";

    public static final String sWeathersOfLocationSettingWithStartDateDelete =
            WeatherContract.WeatherEntry.TABLE_NAME+
                    "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATETEXT + " >= ? ";

    public static final String sWeathersOfLocationSettingAndDayDelete=
            WeatherContract.WeatherEntry.TABLE_NAME+
                    "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATETEXT + " = ? ";
}
