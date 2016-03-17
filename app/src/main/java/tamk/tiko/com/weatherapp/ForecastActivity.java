package tamk.tiko.com.weatherapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Activity for showing 3 days forecast for user.
 *
 * @author Juho Lahtinen
 * @version 1.0
 * @since 1.7
 */
public class ForecastActivity extends AppCompatActivity {

    /**
     * Longitude value from gps.
     */
    double mLongitude = 0;

    /**
     * Latitude value from gps
     */
    double mLatitude = 0;

    /**
     * City name of current location.
     */
    String city = "";

    /**
     * Unit id for temperature.
     */
    int unit = 0;

    /**
     * Symbol for temperature unit.
     */
    String unitString = "";

    /**
     * Celsius temperatures for 3 day forecast.
     */
    ArrayList<Integer> tmpsC = null;

    /**
     * Fahrenheit temperatures for 3 day forecast.
     */
    ArrayList<Integer> tmpsF = null;

    /**
     * Descriptions for 3 day forecast.
     */
    ArrayList<String> descs = null;

    /**
     * Description ids for 3 day forecast.
     */
    ArrayList<Integer> descsIds = null;

    /**
     * On create method for this activity.
     *
     * @param savedInstanceState bundle containing instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        loadSettings();
        updateWeather();
    }

    /**
     * Used for loading settings from preferences xml.
     */
    public void loadSettings() {
        SharedPreferences sharedPref = getSharedPreferences("com.tamk.tiko.latest_data", MODE_PRIVATE);
        String unitTemp =  sharedPref.getString(getString(R.string.unit), "1");
        city = sharedPref.getString(getString(R.string.city), "N/A");

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

        TextView city = (TextView)findViewById(R.id.cityName);
        city.setText(this.city);

        TextView unitText1 = (TextView) findViewById(R.id.unitText1);
        TextView unitText2 = (TextView) findViewById(R.id.unitText2);
        TextView unitText3 = (TextView) findViewById(R.id.unitText3);
        unitText1.setText(unitString);
        unitText2.setText(unitString);
        unitText3.setText(unitString);

        TextView day1 = (TextView) findViewById(R.id.day1);
        TextView day2 = (TextView) findViewById(R.id.day2);
        TextView day3 = (TextView) findViewById(R.id.day3);

        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        weekDay = dayFormat.format(calendar.getTime());
        day1.setText(weekDay);
        calendar.add(Calendar.DATE, 1);
        weekDay = dayFormat.format(calendar.getTime());
        day2.setText(weekDay);
        calendar.add(Calendar.DATE, 1);
        weekDay = dayFormat.format(calendar.getTime());
        day3.setText(weekDay);
    }


    /**
     * Calls weather update from openweathermap.org using REST connection.
     */
    public void updateWeather() {
        TextView temp1 = (TextView)findViewById(R.id.tempertureText1);
        TextView temp2 = (TextView)findViewById(R.id.tempertureText2);
        TextView temp3 = (TextView)findViewById(R.id.tempertureText3);
        TextView desc1 = (TextView) findViewById(R.id.description1);
        TextView desc2 = (TextView) findViewById(R.id.description2);
        TextView desc3 = (TextView) findViewById(R.id.description3);
        ImageView img1 = (ImageView) findViewById(R.id.weathericon1);
        ImageView img2 = (ImageView) findViewById(R.id.weathericon2);
        ImageView img3 = (ImageView) findViewById(R.id.weathericon3);

        RestConnectionForeCast rest = new RestConnectionForeCast(this);
        String restResult = null;
        try {
            restResult = rest.execute("" + mLatitude,"" + mLongitude).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        JsonObject obj = transformStringToJson(restResult);

        getTmpsAndDescs(obj);

        updateWeatherIcon(descsIds.get(0), img1);
        updateWeatherIcon(descsIds.get(1), img2);
        updateWeatherIcon(descsIds.get(2), img3);

        desc1.setText(descs.get(0));
        desc2.setText(descs.get(1));
        desc3.setText(descs.get(2));

        if(unitString.equals("°C")){
            temp1.setText(tmpsC.get(0).toString());
            temp2.setText(tmpsC.get(1).toString());
            temp3.setText(tmpsC.get(2).toString());
        }else {
            temp1.setText(tmpsF.get(0).toString());
            temp2.setText(tmpsF.get(1).toString());
            temp3.setText(tmpsF.get(2).toString());
        }
    }

    /**
     * Transform temperatures and descriptions from json to arrays.
     *
     * @param obj json object containing weather data.
     */
    public void getTmpsAndDescs(JsonObject obj) {
        tmpsC = new ArrayList<Integer>();
        tmpsF = new ArrayList<Integer>();
        descsIds = new ArrayList<Integer>();
        descs = new ArrayList<String>();

        JsonArray arr = obj.getAsJsonArray("list");

        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        String date = dateFormat.format(cal.getTime());

        for (int i = 1; i<4; i++){
            JsonArray tmp = arr.get(i * 8).getAsJsonObject().getAsJsonArray("weather");
            String desc  = tmp.get(0).getAsJsonObject().get("description").toString();
            String descId  = tmp.get(0).getAsJsonObject().get("id").toString();
            JsonElement element = arr.get(i * 8).getAsJsonObject().get("main");
            JsonObject temper = element.getAsJsonObject();
            String temperature = temper.get("temp").toString();
            transFormTemp(Double.parseDouble(temperature));
            desc = desc.replaceAll("^\"|\"$", "");
            descs.add(desc);
            descsIds.add(Integer.parseInt(descId));
        }
    }

    /**
     * Transforms temperature to celsius and fahrenheit.
     *
     * @param tempDouble temperature as kelvin.
     */
    public void transFormTemp(double tempDouble) {
        float tempFloat = (float) tempDouble;
        float tempFloatC = 0;
        float tempFloatF = 0;

        tempFloatC = tempFloat - 272.15f;
        tempFloatF =  tempFloatC * 1.8f + 32;
        int tempCelInt = Math.round(tempFloatC);
        tmpsC.add(tempCelInt);
        int tempFahInt = Math.round(tempFloatF);
        tmpsF.add(tempFahInt);
    }

    /**
     * Creates new array lists.
     *
     * @param obj
     */
    public void getDescs(JsonObject obj) {
        descs = new ArrayList<String>();
        descsIds = new ArrayList<Integer>();

    }

    /**
     * Converst string from openweathermap.org to json object.
     *
     * @param jsonString json in string format.
     * @return json object containing weather data.
     */
    public JsonObject transformStringToJson(String jsonString) {
        JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();
        return obj;
    }

    /**
     * Updates weather icon with description id.
     *
     * @param id id of weather condition.
     * @param weatherIcon imageview where icon will be set.
     */
    public void updateWeatherIcon(int id, ImageView weatherIcon) {
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
}
