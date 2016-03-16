package tamk.tiko.com.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener{
    private String unitString = "°C";
    private String city = "";
    private String temperatureC;
    private String temperatureF;
    private int unit = 1;
    private String description;
    private int descriptionId;

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected double mLatitude = 0;
    protected double mLongitude = 0;
    protected boolean initText = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        buildGoogleApiClient();
    }

    public void openSettings(View view) {
        saveSettings();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public JsonObject transformStringToJson(String jsonString) {
        JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();
        return obj;
    }

    public void getTempertureFromJson(JsonObject obj) {
        JsonElement element = obj.get("main");
        JsonObject tmp = element.getAsJsonObject();
        String temperature = tmp.get("temp").toString();
        float tempFloatC = 0;
        float tempFloatF = 0;

        float tempFloat = Float.parseFloat(temperature);
        tempFloatC = tempFloat - 272.15f;
        tempFloatF =  tempFloatC * 1.8f + 32;
        int tempCelInt = Math.round(tempFloatC);
        temperatureC = "" + tempCelInt;
        int tempFahInt = Math.round(tempFloatF);
        temperatureF = "" + tempFahInt;
    }

    public void getWeatherConditionFromJson(JsonObject obj) {
        JsonArray element = obj.getAsJsonArray("weather");
        JsonElement tmp = element.get(0);
        JsonObject obj2 = tmp.getAsJsonObject();

        String description = obj2.get("description").toString();
        int descriptionId = Integer.parseInt(obj2.get("id").toString());
        description = description.replaceAll("^\"|\"$", "");
        this.description = description;
        this.descriptionId = descriptionId;


    }

    public void getCityFromJson(JsonObject obj) {
        String city = obj.get("name").toString();
        city = city.replaceAll("^\"|\"$", "");

        this.city =  city;
    }

    public void loadSettings() {
        SharedPreferences sharedPref = getSharedPreferences("com.tamk.tiko.latest_data", MODE_PRIVATE);
        String unitTemp =  sharedPref.getString(getString(R.string.unit), "1");
        city = sharedPref.getString(getString(R.string.city), "N/A");
        String descTemp = sharedPref.getString(getString(R.string.condition_id), "1");
        descriptionId = Integer.parseInt(descTemp);
        description = sharedPref.getString(getString(R.string.condition), "N/A");
        String latitudeTemp = sharedPref.getString(getString(R.string.latitude), "0");
        String longitudeTemp = sharedPref.getString(getString(R.string.longitude), "0");
        mLatitude = Double.parseDouble(latitudeTemp);
        mLongitude = Double.parseDouble(longitudeTemp);
        unit = Integer.parseInt(unitTemp);

        if(unit != 0) {
            switch (unit) {
            case 1:
                unitString = "°C";
                break;
            case 2:
                unitString = "°F";
                break;
            }
        }
    }

    public void saveSettings() {
        SharedPreferences sharedPref = getSharedPreferences("com.tamk.tiko.latest_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.unit), "" + unit);
        editor.putString(getString(R.string.condition_id), "" + descriptionId);
        editor.putString(getString(R.string.condition), "" + description);
        editor.putString(getString(R.string.city), city);
        editor.putString(getString(R.string.fahrenheit), "" + temperatureF);
        editor.putString(getString(R.string.celsius), "" + temperatureC);
        editor.putString(getString(R.string.longitude), "" + mLongitude);
        editor.putString(getString(R.string.latitude), "" + mLatitude);
        editor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSettings();

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }catch(SecurityException e) {

        }

        if (mLastLocation != null) {
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();
            if(initText) {
                updateWeather(getWindow().findViewById(R.id.default_control_frame));
                initText = false;
            }
        } else {

        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void updateWeather(View view) {
        TextView temperatureText = (TextView)findViewById(R.id.tempertureText);
        TextView city = (TextView)findViewById(R.id.cityName);
        TextView desc = (TextView) findViewById(R.id.description);
        RestConnectionCurrent rest = new RestConnectionCurrent(this);
        TextView unitText = (TextView) findViewById(R.id.unitText);
        String restResult = null;
        try {
            restResult = rest.execute("" + mLatitude,"" + mLongitude).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        JsonObject obj = transformStringToJson(restResult);
        getTempertureFromJson(obj);
        getWeatherConditionFromJson(obj);
        getCityFromJson(obj);

        desc.setText(description);

        if(unit == 1) {
            temperatureText.setText(temperatureC);
        } else {
            temperatureText.setText(temperatureF);
        }
        unitText.setText(unitString);
        city.setText(this.city);

        Intent intent = getIntent();
        updateWeatherIcon(descriptionId);
    }

    public void updateWeatherIcon(int id) {
        ImageView weatherIcon = (ImageView) findViewById(R.id.weathericon);
        Log.d("ICON", " " + id);
        if(id >= 200 && id <= 232) {
            weatherIcon.setImageResource(R.drawable.icon_thunder);
        }
        else if(id >= 300 && id <= 321) {
            weatherIcon.setImageResource(R.drawable.icon_drizzle);
        }
        else if(id >= 500 && id <= 531) {
            weatherIcon.setImageResource(R.drawable.icon_rain);
        }
        else if(id >= 600 && id <= 622) {
            weatherIcon.setImageResource(R.drawable.icon_snow);
        }
        else if(id >= 701 && id <= 761) {
            weatherIcon.setImageResource(R.drawable.icon_mist);
        }
        else if(id == 800) {
            weatherIcon.setImageResource(R.drawable.icon_sunny);
        }
        else if(id >= 801 && id <= 804) {
            weatherIcon.setImageResource(R.drawable.icon_cloudy);
        }
        else{
            weatherIcon.setImageResource(R.drawable.icon_na);
        }
    }

    public void forecast(View v) {
        saveSettings();
        Intent intent = new Intent(this, ForecastActivity.class);
        startActivity(intent);
    }
}
