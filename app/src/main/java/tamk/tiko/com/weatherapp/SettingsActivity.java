package tamk.tiko.com.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;
import java.util.Set;

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
        saveSettings();

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
        loadSettings();

        RadioButton c = (RadioButton) findViewById(R.id.radioC);
        RadioButton f = (RadioButton) findViewById(R.id.radioF);
        switch (appSettings.getUnit()) {
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
    @Override
    public void onPause() {
        super.onPause();
        saveSettings();



    }

    public void saveSettings() {
        SharedPreferences sharedPref = getSharedPreferences("com.tamk.tiko.latest_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.unit), "" + appSettings.getUnit());
        editor.commit();
    }

    public void loadSettings() {
        SharedPreferences sharedPref = getSharedPreferences("com.tamk.tiko.latest_data", MODE_PRIVATE);
        String temp =  sharedPref.getString(getString(R.string.unit), "1");
        appSettings.setUnit(Integer.parseInt(temp));
    }

}
