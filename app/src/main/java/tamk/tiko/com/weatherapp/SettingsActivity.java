package tamk.tiko.com.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {
    private AppSettings appSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("unit", appSettings.getUnit());
        intent.putExtra("cityId", appSettings.getSelectedCityId());
        intent.putExtra("useLocal", appSettings.isUseLocalWeather());

        final Switch unitSwitch = (Switch) findViewById(R.id.unitSwitch);
        unitSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUnit(unitSwitch);
            }
        });

        // Moves back to main activity.
        startActivity(intent);
    }

    private void changeUnit(Switch unitSwitch) {
        if(unitSwitch.isChecked()) {
            appSettings.setUnit(2);
        } else {
            appSettings.setUnit(1);
        }
    }

}
