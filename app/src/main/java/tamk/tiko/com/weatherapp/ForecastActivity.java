package tamk.tiko.com.weatherapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ForecastActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        this.overridePendingTransition(R.anim.fadein,R.anim.fadeout);


    }
}
