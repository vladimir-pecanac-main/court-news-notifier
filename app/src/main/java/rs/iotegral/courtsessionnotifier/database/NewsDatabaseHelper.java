package rs.iotegral.courtsessionnotifier.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import rs.iotegral.courtsessionnotifier.contracts.DatabaseContract;

/**
 * Created by Lauda on 8/13/2018 21:13.
 */
public class NewsDatabaseHelper extends SQLiteOpenHelper {
    // Database schema version (update manually on schema change!)
    private static final int DATABASE_VERSION = 1;

    // Database name
    private static final String DATABASE_NAME = "rs.iotegral.courtsessionnotifier.db";

    NewsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table schema
        final String SQL_CREATE_NEWS_TABLE = "CREATE TABLE " + DatabaseContract.NewsEntity.TABLE_NAME + " (" +
                DatabaseContract.NewsEntity._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DatabaseContract.NewsEntity.COLUMN_NEWS_GUID + " TEXT UNIQUE NOT NULL, " +
                DatabaseContract.NewsEntity.COLUMN_NEWS_TITLE + " TEXT NOT NULL DEFAULT 'N/A', " +
                DatabaseContract.NewsEntity.COLUMN_NEWS_DESCRIPTION + " TEXT DEFAULT 'N/A', " +
                DatabaseContract.NewsEntity.COLUMN_NEWS_PUBLISHED + " DEFAULT CURRENT_TIMESTAMP, " +
                DatabaseContract.NewsEntity.COLUMN_NEWS_LAST_UPDATED + " DEFAULT CURRENT_TIMESTAMP, " +
                DatabaseContract.NewsEntity.COLUMNA_NEWS_UNREAD + " INTEGER DEFAULT 0" +
                " );";

        db.execSQL(SQL_CREATE_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This is a cache database and there is no need to keep data if schema has been changed.
        // Drop all
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.NewsEntity.TABLE_NAME);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
