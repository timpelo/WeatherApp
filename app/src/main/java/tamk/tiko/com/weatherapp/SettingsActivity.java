package tamk.tiko.com.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
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

}
