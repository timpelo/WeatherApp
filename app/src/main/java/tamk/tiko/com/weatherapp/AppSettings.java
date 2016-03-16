package tamk.tiko.com.weatherapp;

/**
 * Created by Jani on 26.2.2016.
 */
public class AppSettings {

    //1 = celsius, 2 = fahrenheit.
    private int unit;

    public AppSettings() {
        unit = 1;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }
}
