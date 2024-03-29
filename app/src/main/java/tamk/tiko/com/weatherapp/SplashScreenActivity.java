package tamk.tiko.com.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Splash screen for application.
 *
 * @author Jani Timonen
 * @version 1.0
 * @since 1.7
 */
public class SplashScreenActivity extends Activity {

    /**
     * Duration of wait.
     */
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /**
     * Called when activity is created. Shows splash screen for given time and then moves to
     * main activity.
     *
     * @param icicle bundle containing instance data.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash_screen);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                SplashScreenActivity.this.startActivity(mainIntent);
                SplashScreenActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
