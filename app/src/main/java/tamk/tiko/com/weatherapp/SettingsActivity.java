package tamk.tiko.com.weatherapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Settings activity. Contains radio buttons fro changing temperature unit between celsius
 * and fahrenheit.
 *
 * @author Jani Timonen
 * @version 1.0
 * @since 1.7
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * Object containing all settings attributes.
     */
    private AppSettings appSettings;

    /**
     * Called when activity is created.
     *
     * @param savedInstanceState bundle containing data of current instance.
     */
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

    /**
     * Called when Android back button is pressed.
     */
    @Override
    public void onBackPressed() {
        goToMainActivity();
    }

    /**
     * Returns to main activity and saves settings to xml.
     */
    private void goToMainActivity() {
        changeUnit();
        saveSettings();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("unit", appSettings.getUnit());
        startActivity(intent);
    }

    /**
     * Changes unit according radio buttons.
     */
    private void changeUnit() {
        RadioButton buttonC = (RadioButton) findViewById(R.id.radioC);

        if(buttonC.isChecked()) {
            appSettings.setUnit(1);
        } else {
            appSettings.setUnit(2);
        }
    }

    /**
     * Called when activity is resumed.
     */
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

    /**
     * Called when activity is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
        saveSettings();
    }

    /**
     * Saves unit settings to preferences xml.
     */
    public void saveSettings() {
        SharedPreferences sharedPref = getSharedPreferences("com.tamk.tiko.latest_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.unit), "" + appSettings.getUnit());
        editor.commit();
    }

    /**
     * Loads unit settings from preferences xml.
     */
    public void loadSettings() {
        SharedPreferences sharedPref = getSharedPreferences("com.tamk.tiko.latest_data", MODE_PRIVATE);
        String temp =  sharedPref.getString(getString(R.string.unit), "1");
        appSettings.setUnit(Integer.parseInt(temp));
    }
}
