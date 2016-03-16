package tamk.tiko.com.weatherapp;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private int unit = 1;

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected double mLatitude = 0;
    protected double mLongitude = 0;
    protected boolean initText = true;

    protected String description;
    protected int descriptionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        buildGoogleApiClient();
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("unit", unit);
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

    public String getTempertureFromJson(JsonObject obj) {
        JsonElement element = obj.get("main");
        JsonObject tmp = element.getAsJsonObject();
        String temperature = tmp.get("temp").toString();

        float tempFloat = Float.parseFloat(temperature);
        tempFloat -= 272.15f;

        int tempInt = Math.round(tempFloat);

        // Used only if temperature will be shown with one decimal.
        //temperature = "" + String.format("%.1f", tempInt) + "°c";

        temperature = "" + tempInt;



        return temperature;
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

    public String getCityFromJson(JsonObject obj) {
        String city = obj.get("name").toString();
        city = city.replaceAll("^\"|\"$", "");

        return city;
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle data = getIntent().getExtras();
        if(data != null) {
            unit = data.getInt("unit");

            if(unit != 0) {
                switch (unit) {
                    case 1:
                        unitString = "°C";
                        break;
                    case 2:
                        unitString = " F";
                        break;
                }
            }
        }
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

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        TextView temp = (TextView) findViewById(R.id.tempertureText);
        bundle.putString("temperture", temp.getText().toString());
        Log.d("MYBUG", temp.getText().toString());
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);

        TextView temp = (TextView) findViewById(R.id.tempertureText);
        String tempText = bundle.getString("temperture");
        Log.d("MYBUG", tempText);
        temp.setText(tempText);
    }

    public void updateWeather(View view) {
        TextView text = (TextView)findViewById(R.id.tempertureText);
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
        String temperature = getTempertureFromJson(obj);
        getWeatherConditionFromJson(obj);

        desc.setText(description);
        text.setText(temperature);
        unitText.setText(unitString);
        city.setText(getCityFromJson(obj));

        Intent intent = getIntent();
        String name = intent.getStringExtra("cityId");
        city.setText(getCityFromJson(obj));
        updateWeatherIcon(descriptionId);
    }

    public void updateWeatherIcon(int id) {
        ImageView weatherIcon = (ImageView) findViewById(R.id.weathericon);
        // CONTINUE FROM HERE!
        if(true) {
            weatherIcon.setBackgroundResource(R.drawable.icon_sunny);
        }
    }

    public void forecast(View v) {
        Intent intent = new Intent(this, ForecastActivity.class);
        startActivity(intent);
    }
}
