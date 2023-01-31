package com.example.appweather;

public class DayInformation {
    private String day;
    private String forestWeatherText;
    private String TempMax;
    private String TempMin;

    public String getDay() {
        return day.split("-")[1]+"-"+day.split("-")[2];
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getForestWeatherText() {
        return forestWeatherText;
    }

    public void setForestWeatherText(String forestWeatherText) {
        this.forestWeatherText = forestWeatherText;
    }

    public String getTempMax() {
        return TempMax+" /";
    }

    public void setTempMax(String tempMax) {
        TempMax = tempMax;
    }

    public String getTempMin() {
        return TempMin+"℃";
    }

    public void setTempMin(String tempMin) {
        TempMin = tempMin;
    }
}
