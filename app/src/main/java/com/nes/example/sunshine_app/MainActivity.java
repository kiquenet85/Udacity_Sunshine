package com.nes.example.sunshine_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {


    private String LOG_TAG ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LOG_TAG=((Object) this).getClass().getSimpleName();
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }else if (id==R.id.action_location_in_map){
            showLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLocationInMap() {
        SharedPreferences settings= PreferenceManager.getDefaultSharedPreferences(this);
        String locationValue=settings.getString(getString(R.string.pref_Location_key), getString(R.string.pref_location_default));

        if (locationValue!=null && findCorrespondingLocationToValue(locationValue)!=null){
            Uri uri=findCorrespondingLocationToValue(locationValue);

            Intent intent=new Intent(android.content.Intent.ACTION_VIEW,
                    uri);
            Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
            if (intent.resolveActivity(this.getPackageManager())!=null){
                startActivity(intent);
            }else{
                String msg="Is not possible to see location in MAP, there are not available apps";
                Log.d(LOG_TAG, msg);
                Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
            }
        }
    }

    //http://stackoverflow.com/questions/7168522/starting-google-maps-app-with-provided-location-and-marker
    private Uri findCorrespondingLocationToValue(String value) {

        Uri uri=null;

        String[] carIds = getResources().getStringArray(R.array.pref_location_list_values);

        for (int i = 0; i < carIds.length; i++) {
            String listValue=carIds[i];
            //4.334858, -74.350432 Biblioteca Fusa
            if (listValue.equals(value) && listValue.equals("5375480")){
                uri= Uri.parse("geo:4.334858,-74.350432?q=Mountain+View,+USA");
            }
            if (listValue.equals(value) && listValue.equals("3688689")){
                uri= Uri.parse("geo:4.334858,-74.350432?q=Bogota+Cundinamarca,+Colombia");
            }
            if (listValue.equals(value) && listValue.equals("3682274")){
                uri= Uri.parse("geo:4.334858,-74.350432?q=Fusagasuga+Cundinamarca,+Colombia");
            }else{
                uri= Uri.parse("geo:4.334858,-74.350432?q=4.334858,-74.350432");
            }
        }

        return uri;

    }


}
