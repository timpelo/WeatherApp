package tamk.tiko.com.weatherapp;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.ExecutionException;

/**
 * Main activity. Contains data of current weather of current location. Handles updating data from
 * openweathermap.org.
 *
 * @author Jani Timonen
 * @version 1.0
 * @since 1.7
 */
public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener{
    /**
     * String containing curren unit symbol.
     */
    private String unitString = "°C";

    /**
     * String containing name of current city.
     */
    private String city = "";

    /**
     * Temperature as celsius.
     */
    private String temperatureC;

    /**
     * Temperature as fahrenheit.
     */
    private String temperatureF;

    /**
     * Unit id for temperature.
     */
    private int unit = 1;

    /**
     * Description for current weather.
     */
    private String description;

    /**
     * If of current weather description.
     */
    private int descriptionId;

    /**
     * Client for Google API.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Latest location.
     */
    protected Location mLastLocation;

    /**
     * Latitude of current location.
     */
    protected double mLatitude = 0;

    /**
     * Longitude of current location.
     */
    protected double mLongitude = 0;


    protected boolean initText = true;

    /**
     * Animation for refresh button.
     */
    RotateAnimation r;

    /**
     * Rotation degrees for animation.
     */
    private static final float ROTATE_FROM = 0.0f;

    /**
     * Rotation degrees for animation.
     */
    private static final float ROTATE_TO = -2.5f * 360.0f;

    /**
     * Refresh button.
     */
    ImageView refreshButton;

    /**
     * On create method called when activity is created.
     *
     * @param savedInstanceState bundle containing instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        buildGoogleApiClient();
        refreshButton = (ImageView) findViewById(R.id.refreshButton);
        r = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        r.setDuration((long) 1250);
        r.setRepeatCount(0);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshButton.startAnimation(r);
                updateWeather(refreshButton);
            }
        });
    }

    /**
     * Changes view to settings activity.
     *
     * @param view current view.
     */
    public void openSettings(View view) {
        saveSettings();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Builds Google API Client for getting location.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Transform json string to json object.
     *
     * @param jsonString json as string.
     * @return json as object.
     */
    public JsonObject transformStringToJson(String jsonString) {
        JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();
        return obj;
    }

    /**
     * Finds temperature from json object and stores values to variables.
     *
     * @param obj json object containing info of weather.
     */
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

    /**
     * Finds weather condition and weather condition id from json object.
     *
     * @param obj json object containing weather condition and condition id.
     */
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

    /**
     * Finds city name from json object and stores it to variable.
     *
     * @param obj json object containing weather information.
     */
    public void getCityFromJson(JsonObject obj) {
        String city = obj.get("name").toString();
        city = city.replaceAll("^\"|\"$", "");
        this.city =  city;
    }

    /**
     * Loads settings from xml and sets them to correct variables. Changes unit string according
     * to unit id.
     */
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

        Log.d("PREF", description);
        Log.d("PREF", city);

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

    /**
     * Saves current settings to xml file.
     */
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

    /**
     * Called when activity is resumed.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadSettings();
    }

    /**
     * Called if Google API connection does success.
     *
     * @param connectionHint bundle containing connection data.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        if(initText) {
            updateWeather(getWindow().findViewById(R.id.default_control_frame));
            initText = false;
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("location")
                        .setMessage("message")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    /**
     * Called if connection to Google API is lost.
     *
     * @param cause id for disconnection cause.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        mGoogleApiClient.connect();
    }

    /**
     * Called if connection fails.
     *
     * @param connectionResult result for connection failure.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * Called when Android back button is pressed. Exits application.
     */
    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        saveSettings();
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    /**
     * Called when activity is started.
     */
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    /**
     * Called when activity is stopped.
     */
    @Override
    protected void onStop() {
        super.onStop();
        saveSettings();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void updateLocation() {
        if (checkLocationPermission()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                mLatitude = mLastLocation.getLatitude();
                mLongitude = mLastLocation.getLongitude();
            }
        }
    }

    /**
     * Updates weather from openweathermap.org.
     *
     * @param view current view.
     */
    public void updateWeather(View view) {
        updateLocation();
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

    /**
     * Updates weather icon according weather description id.
     *
     * @param id weather description id.
     */
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

    /**
     * Changes activity to forecast.
     *
     * @param v current view
     */
    public void forecast(View v) {
        saveSettings();
        Intent intent = new Intent(this, ForecastActivity.class);
        startActivity(intent);
    }
}
