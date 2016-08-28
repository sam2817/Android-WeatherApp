package com.example.weatherapp.weatherapp;

import java.awt.font.NumericShaper;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by SamuelAaron on 2016-08-21.
 */

// Maintains one day's weather information
public class Weather {
    public final String dayOfWeek;
    public final String minTemp;
    public final String maxTemp;
    public final String humidity;
    public final String description;
    public final String iconURL;

    // Constructor
    public Weather(long timeStamp, double minTemp, double maxTemp, double humidity, String description, String iconName) {

        // NumberFormat to format double temperatures rounded to integers
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);

        this.dayOfWeek = convertTimeStampToDay(timeStamp);
        this.minTemp = numberFormat.format(minTemp) + "\u00B0F";
        this.maxTemp = numberFormat.format(maxTemp) + "\u00B0F";
        this.humidity = NumberFormat.getPercentInstance().format(humidity / 100.0);
        this.description = description;
        this.iconURL = "http://openweathermap.org/img/w/" + iconName + ".png";
    }

    // Convert timeStamp to a day's name (e.g., Monday, Tuesday, ...)
    private static String convertTimeStampToDay(long timeStamp) {
        Calendar calendar = Calendar.getInstance(); // Create Calendar
        calendar.setTimeInMillis(timeStamp * 1000); // Set time
        TimeZone tz = TimeZone.getDefault(); // Get device's time zone

        // Adjust time for device's time zone
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));

        // SimpleDateFormat that returns the day's name
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE");
        return dateFormatter.format(calendar.getTime());
    }
}
