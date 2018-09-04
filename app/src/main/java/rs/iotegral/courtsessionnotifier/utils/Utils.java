package rs.iotegral.courtsessionnotifier.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Lauda on 8/28/2018 21:31.
 */
public class Utils {
    public static int getWeatherIcon(String icon, Context context) {
        return context.getResources().getIdentifier(icon, "drawable", context.getPackageName());
    }

    public static int getSyncInterval(Context c) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        return Integer.parseInt(pref.getString("news_sync_frequency", "900")); // default is 15 mins
    }

    public static String parseDate(String milliSeconds) {
        DateFormat formatter = SimpleDateFormat.getDateTimeInstance();
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliSeconds));
        return formatter.format(calendar.getTime());
    }
}
