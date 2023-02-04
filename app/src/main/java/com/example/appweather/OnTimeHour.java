package com.example.appweather;



public class OnTimeHour {
    private String time;
    private String Temp;

    public String getTime() {
        return time.split("T")[1].split("\\+")[0];
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemp() {
        return Temp+"°";
    }

    public void setTemp(String temp) {
        Temp = temp;
    }
}
