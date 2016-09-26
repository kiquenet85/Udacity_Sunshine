package com.nes.example.sunshine_app;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 21/08/2014.
 */
public class ForecastFragment extends Fragment {

    private String LOG_TAG ;

    private ArrayAdapter listAdapter;
    public ForecastFragment() {
    }

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getBatteryStatus();
        //-- When is a fragment is necessary to call this method to create a Menu.
        LOG_TAG=((Object) this).getClass().getSimpleName();
        setHasOptionsMenu(true);
    }

    //http://developer.android.com/training/monitoring-device-state/battery-monitoring.html
    private int getBatteryStatus() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getActivity().registerReceiver(null, ifilter);

        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int level=batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;

        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        Toast.makeText(getActivity(),"Percentage: "+batteryPct+" Battery Level is: "+level+", CHARGING: "+isCharging+" USB Plug:"+usbCharge+" CHARGE Plug:"+acCharge,Toast.LENGTH_LONG).show();
        return status;
    }

    @Override
    public void onStart (){
        super.onStart();
        //updateWeather();

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.action_refresh){
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }





    /*String findKey(SharedPreferences sharedPreferences, String value) {
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null; // not found
*/
    public void updateWeather(){
        //new FetchWeatherTask().execute("94043");
        SharedPreferences settings= PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location=settings.getString(getString(R.string.pref_Location_key), getString(R.string.pref_location_default));
        String metricNumber= settings.getString(getString(R.string.pref_metric_key),getString(R.string.pref_metric_default));
        String metric= (metricNumber.equals("1")) ? "imperial" : "metric";
        String[] params={location,metric};
        new FetchWeatherTask().execute(params);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

       /*   Fake method.
            String[] forecastArray ={
                "Today - Sunny - 88/63",
                "Tommorrow - Funny - 70/63",
                "Weds - Cloudy - 68/63",
                "Thurs - Asteroids - 46/63",
                "Fry - Heavy Rain - 45/63",
                "Sat - HELP Thraped in Weatherstation - 64/63",
                "Sun - Sunny - 22/63"
        };

        //--This is to be able to call the clear method and add method on this list collection.
        List<String> weekForecast = new ArrayList<String> (Arrays.asList(forecastArray));*/

        List<String> weekForecast = new ArrayList<String> ();
        listAdapter=new ArrayAdapter(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,weekForecast);

        ListView listView=(ListView)rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecast=(String)listAdapter.getItem(position);
                Toast.makeText(view.getContext(),forecast,Toast.LENGTH_SHORT).show();
                //Intent intent= new Intent("com.nes.example.sunshine_app.DetailActivity");
                Intent intent= new Intent(getActivity(),DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT,forecast);
                startActivity(intent);
            }
        });
        return rootView;
    }

    private class FetchWeatherTask extends AsyncTask<String,Void,String[]>{

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        protected String[] doInBackground(String... params) {
            if (params==null || params.length<2){
                return null;
            }
            Log.v(FetchWeatherTask.class.getSimpleName(),"Forecast JSON param 'q': "+ params[0]);
            Log.v(FetchWeatherTask.class.getSimpleName(),"Forecast JSON param 'metric': "+ params[1]);

            return RESTService(params);
        }

        /*protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }*/

        protected void onPostExecute(String[] result) {
            if (result!=null){
                listAdapter.clear();
                for (String dayForecastStr: result){
                    listAdapter.add(dayForecastStr);
                }
            }
        }
    }

    public String[] RESTService(String [] params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String FORECAST_BASE_URL="http://api.openweathermap.org/data/2.5/forecast/daily?";
        String QUERY_PARAM="id";
        String FORMAT_PARAM="mode";
        String UNITS_PARAM="units";
        String DAYS_PARAM="cnt";
        String format="json";
        String units="metric";
        Integer numDays=7;

        Uri builtUri= Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM,params[0])
                .appendQueryParameter(FORMAT_PARAM,format)
                .appendQueryParameter(UNITS_PARAM,params[1])
                .appendQueryParameter(DAYS_PARAM,Integer.toString(numDays)).build();

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            Log.v(FetchWeatherTask.class.getSimpleName(),"Forecast URL: "+ builtUri.toString());
            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
               return null;
            }
            forecastJsonStr = buffer.toString();
            Log.v(FetchWeatherTask.class.getSimpleName(),"Forecast JSON String: "+ forecastJsonStr);
            return getWeatherDataFromJson(forecastJsonStr,numDays);
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
        } catch (JSONException e) {
            Log.e("PlaceholderFragment", "JSONError ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
    }

      /* The date/time conversion code is going to be moved outside the asynctask later,
     * so for convenience we're breaking it out into its own method now.
     */
    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DATETIME = "dt";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        String[] resultStrs = new String[numDays];
        for(int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime = dayForecast.getLong(OWM_DATETIME);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }

        for (String s: resultStrs){
            Log.v(FetchWeatherTask.class.getSimpleName(),"Forecast entry: "+ s);
        }
        return resultStrs;
    }
}