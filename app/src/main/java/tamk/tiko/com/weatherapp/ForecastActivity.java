package tamk.tiko.com.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;

public class ForecastActivity extends AppCompatActivity {

    double mLongitude = 0;
    double mLatitude = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        this.overridePendingTransition(R.anim.fadein,R.anim.fadeout);

    }

    public void updateWeather(View view) {
        TextView text = (TextView)findViewById(R.id.tempertureText);
        TextView city = (TextView)findViewById(R.id.cityName);
        TextView desc = (TextView) findViewById(R.id.description);
        RestConnectionForeCast rest = new RestConnectionForeCast(this);
        TextView unitText = (TextView) findViewById(R.id.unitText);
        String restResult = null;
        try {
            restResult = rest.execute("" + mLatitude,"" + mLongitude).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        /*
        JsonObject obj = transformStringToJson(restResult);
        String temperature = getDay(obj);
        getWeatherConditionFromJson(obj);

        desc.setText(description);
        text.setText(temperature);
        unitText.setText(unitString);
        city.setText(getCityFromJson(obj));

        Intent intent = getIntent();
        String name = intent.getStringExtra("cityId");
        city.setText(getCityFromJson(obj));
        updateWeatherIcon(descriptionId);
        */
    }

    public JsonObject transformStringToJson(String jsonString) {
        JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();
        return obj;
    }
}
