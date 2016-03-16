package tamk.tiko.com.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Button;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    private AppSettings appSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        String versionNumber = getResources().getString(R.string.version);
        TextView version = (TextView) findViewById(R.id.versionText);
        version.setText("Version: " + versionNumber);


        appSettings = new AppSettings();
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
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
        RadioButton buttonC = (RadioButton) findViewById(R.id.radioC);

        if(buttonC.isChecked()) {
            appSettings.setUnit(1);
        } else {
            appSettings.setUnit(2);
        }
    }

    public void onResume() {
        super.onResume();

        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            int unit = extras.getInt("unit");
            RadioButton c = (RadioButton) findViewById(R.id.radioC);
            RadioButton f = (RadioButton) findViewById(R.id.radioF);

            switch (unit) {
                case 1:
                    c.setChecked(true);
                    break;
                case 2:
                    f.setChecked(true);
                    break;
                default:
                    c.setChecked(true);
                    break;
            }
        }
    }

}
