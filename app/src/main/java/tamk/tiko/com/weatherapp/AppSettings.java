package tamk.tiko.com.weatherapp;

/**
 * Created by Jani on 26.2.2016.
 */
public class AppSettings {

    //1 = celsius, 2 = fahrenheit.
    private int unit;
    private boolean useLocalWeather;
    private String selectedCityId;

    public AppSettings() {
        unit = 1;
        useLocalWeather = true;
        selectedCityId = null;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public boolean isUseLocalWeather() {
        return useLocalWeather;
    }

    public void setUseLocalWeather(boolean useLocalWeather) {
        this.useLocalWeather = useLocalWeather;
    }

    public String getSelectedCityId() {
        return selectedCityId;
    }

    public void setSelectedCityId(String selectedCityId) {
        this.selectedCityId = selectedCityId;
    }
}
