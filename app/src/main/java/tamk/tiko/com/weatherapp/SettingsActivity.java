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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);


        appSettings = new AppSettings();
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        Button infoButton = (Button) findViewById(R.id.infoButton);
        Button backButton = (Button) findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        goToMainActivity();
    }

    private void goToMainActivity() {
        changeUnit();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("unit", appSettings.getUnit());
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

}
