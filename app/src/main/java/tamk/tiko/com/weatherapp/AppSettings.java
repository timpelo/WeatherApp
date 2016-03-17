package tamk.tiko.com.weatherapp;

/**
 * Settings class for containing settings for application
 *
 * @author Jani Timonen
 * @version 1.0
 * @since 1.7
 */
public class AppSettings {

    /**
     * Unit for temperature. 1 = celsius, 2 = fahrenheit.
     */
    private int unit;

    /**
     * Default constructor.
     */
    public AppSettings() {
        unit = 1;
    }

    /**
     * Returns unit id.
     *
     * @return unit id.
     */
    public int getUnit() {
        return unit;
    }

    /**
     * Sets unit id.
     *
     * @param unit unit id.
     */
    public void setUnit(int unit) {
        this.unit = unit;
    }
}
