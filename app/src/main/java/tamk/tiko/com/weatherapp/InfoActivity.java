package tamk.tiko.com.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info);
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        String versionNumber = "0.1";
        TextView version = (TextView) findViewById(R.id.versionText);
        version.setText("Version: " + versionNumber);
    }
}
