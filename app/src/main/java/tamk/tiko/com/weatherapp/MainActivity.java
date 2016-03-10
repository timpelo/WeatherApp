package tamk.tiko.com.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private String unitString = "°C";
    private int unit = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        ImageView refresh = (ImageView) findViewById(R.id.refreshButton);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text = (TextView)findViewById(R.id.tempertureText);
                TextView city = (TextView)findViewById(R.id.cityName);
                RestConnection rest = new RestConnection();
                String restResult = null;
                try {
                    restResult = rest.execute("634963").get();
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
                city.setText(name);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
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
}
