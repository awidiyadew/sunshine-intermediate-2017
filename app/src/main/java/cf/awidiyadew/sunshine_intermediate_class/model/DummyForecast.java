package cf.awidiyadew.sunshine_intermediate_class.model;

/**
 * Created by awidiyadew on 5/7/17.
 */

public class DummyForecast {

    private String day;
    private String forecast;
    private int maxTemp;
    private int minTemp;

    public DummyForecast(String day, String forecast, int maxTemp, int minTemp) {
        this.day = day;
        this.forecast = forecast;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getForecast() {
        return forecast;
    }

    public void setForecast(String forecast) {
        this.forecast = forecast;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public String getMaxTempWithDerajat(){
        return String.valueOf(maxTemp) + "\u00b0";
    }

    public String getMixTempWithDerajat(){
        return String.valueOf(minTemp) + "\u00b0";
    }

}

