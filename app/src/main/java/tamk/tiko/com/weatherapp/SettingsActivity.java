package tamk.tiko.com.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {
    private AppSettings appSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appSettings = new AppSettings();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Switch unitSwitch = (Switch) findViewById(R.id.unitSwitch);
        Switch localSwitch = (Switch) findViewById(R.id.localSwitch);
        Button backButton = (Button) findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });
    }

    private void goToMainActivity() {
        changeUnit();
        checkLocalWeatherSetting();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("unit", appSettings.getUnit());
        intent.putExtra("cityId", appSettings.getSelectedCityId());
        intent.putExtra("useLocal", appSettings.isUseLocalWeather());
        startActivity(intent);
    }

    private void changeUnit() {
        Switch unitSwitch = (Switch) findViewById(R.id.unitSwitch);
        if(unitSwitch.isChecked()) {
            appSettings.setUnit(2);
        } else {
            appSettings.setUnit(1);
        }
    }

    private void checkLocalWeatherSetting() {
        Switch localSwitch = (Switch) findViewById(R.id.localSwitch);
        appSettings.setUseLocalWeather(localSwitch.isChecked());
    }

}
