package tamk.tiko.com.weatherapp;

import android.location.Location;
import android.location.LocationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener{
    private String unitString = "°C";
    private int unit = 1;

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected double mLatitude = 0;
    protected double mLongitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        buildGoogleApiClient();

        ImageView refresh = (ImageView) findViewById(R.id.refreshButton);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text = (TextView)findViewById(R.id.tempertureText);
                TextView city = (TextView)findViewById(R.id.cityName);
                RestConnectionCurrent rest = new RestConnectionCurrent();
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

                text.setText(temperature);
                city.setText(getCityFromJson(obj));

                Intent intent = getIntent();
                String name = intent.getStringExtra("cityId");
                city.setText(getCityFromJson(obj));
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
    }

    public void initTexts(){
        TextView text = (TextView)findViewById(R.id.tempertureText);
        TextView city = (TextView)findViewById(R.id.cityName);
        RestConnectionCurrent rest = new RestConnectionCurrent();
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

        text.setText(temperature);
        city.setText(getCityFromJson(obj));

        Intent intent = getIntent();
        String name = intent.getStringExtra("cityId");
        city.setText(getCityFromJson(obj));
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

        temperature = "" + tempInt + unitString;



        return temperature;
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
}
