package tamk.tiko.com.weatherapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        temperature = "" + tempInt + "°C";



        return temperature;
    }

    public String getCityFromJson(JsonObject obj) {
        String city = obj.get("name").toString();
        city = city.replaceAll("^\"|\"$", "");

        return city;
    }
}
