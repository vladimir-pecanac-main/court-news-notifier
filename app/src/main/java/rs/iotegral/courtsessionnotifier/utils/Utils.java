package rs.iotegral.courtsessionnotifier.utils;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Lauda on 8/28/2018 21:31.
 */
public class Utils {
    public static int getWeatherIcon(String icon, Context context) {
        return context.getResources().getIdentifier(icon, "drawable", context.getPackageName());
    }

    public static String parseDate(String milliSeconds) {
        DateFormat formatter = SimpleDateFormat.getDateTimeInstance();
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliSeconds));
        return formatter.format(calendar.getTime());
    }
}
