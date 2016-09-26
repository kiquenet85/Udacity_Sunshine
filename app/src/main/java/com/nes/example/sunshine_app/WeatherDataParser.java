package com.nes.example.sunshine_app;

/**
 * Created by user on 22/08/2014.
 */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataParser {

    /**
     * Given a string of the form returned by the api call:
     * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
     * retrieve the maximum temperature for the day indicated by dayIndex
     * (Note: 0-indexed, so 0 would refer to the first day).
     */
    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException {
        JSONObject myJson = new JSONObject(weatherJsonStr);
        //System.out.println(myJson);
        JSONArray lst=myJson.getJSONArray("list");
        Double MAX=-1D;

        /*for (int i=0;i<lst.length();i++){
            MAX=lst.getJSONObject(i).getJSONObject("temp").getDouble("max");
            System.out.println(MAX);
        }*/
        MAX=lst.getJSONObject(dayIndex).getJSONObject("temp").getDouble("max");
        return MAX;
    }

}
