package rs.iotegral.courtsessionnotifier.contracts;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lauda on 8/13/2018 21:11.
 */
public class DatabaseContract {
    public static final String CONTENT_AUTHORITY = "rs.iotegral.courtsessionnotifier.app";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_NEWS = "news_feed";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static String extractDate(String date) {
        // Example of date from API: /Date(1531838390953)/
        Pattern regex = Pattern.compile(".*\\( *(.*) *\\).*");
        Matcher matcher =  regex.matcher(date);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return date; // return original string in case regex fails
    }

    // Inner class that defines the table content for news
    public static final class NewsEntity implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEWS).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;

        // Table name
        public static final String TABLE_NAME = "news_feed";

        // Fields
        public static final String COLUMN_NEWS_GUID = "news_guid";
        public static final String COLUMN_NEWS_TITLE = "news_title";
        public static final String COLUMN_NEWS_DESCRIPTION = "news_description";
        public static final String COLUMN_NEWS_PUBLISHED = "news_published";
        public static final String COLUMN_NEWS_LAST_UPDATED = "news_last_updated";
        public static final String COLUMNA_NEWS_UNREAD = "news_unread";

        public static Uri buildNewsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildNewsData() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildNewsDataWithId(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(0);
        }
    }
}
