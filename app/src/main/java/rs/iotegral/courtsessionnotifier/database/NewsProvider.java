package rs.iotegral.courtsessionnotifier.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Objects;

import rs.iotegral.courtsessionnotifier.contracts.DatabaseContract;

/**
 * Created by Lauda on 8/20/2018 21:01.
 */
public class NewsProvider extends ContentProvider {
    private NewsDatabaseHelper mNewsDatabaseHelper;
    private static final int LATEST_NEWS = 100;
    private static final int CURRENT_NEWS = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder sNewsTableQueryBuilder;

    @Override
    public boolean onCreate() {
        mNewsDatabaseHelper = new NewsDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case LATEST_NEWS: {
                cursor = getLatestNews(projection, sortOrder);
                break;
            }
            case CURRENT_NEWS: {
                cursor = getLatestNewsById(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case LATEST_NEWS:
                return DatabaseContract.NewsEntity.CONTENT_TYPE;
            case CURRENT_NEWS:
                return DatabaseContract.NewsEntity.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI type: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mNewsDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri value;

        switch (match) {
            case LATEST_NEWS: {
                long id = db.insert(DatabaseContract.NewsEntity.TABLE_NAME, null, values);

                if (id > 0)
                    value = DatabaseContract.NewsEntity.buildNewsUri(id);
                else
                    throw new SQLException("Failed to insert a new row: " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown insert URI: " + uri);
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null, false);
        return value;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mNewsDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        selection = selection == null ? "1" : selection;

        switch (match) {
            case LATEST_NEWS:
                rowsDeleted = db.delete(DatabaseContract.NewsEntity.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown delete URI: " + uri);
        }

        if (rowsDeleted != 0)
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null, false);

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mNewsDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case LATEST_NEWS:
                rowsUpdated = db.update(DatabaseContract.NewsEntity.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown update URI: " + uri);
        }

        if (rowsUpdated != 0)
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null, false);

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mNewsDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count = 0;

        switch (match) {
            case LATEST_NEWS:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long id = db.insert(DatabaseContract.NewsEntity.TABLE_NAME, null, value);

                        if (id != -1)
                            count++;
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }

                Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null, false);

                return count;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    // Private methods
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DatabaseContract.PATH_NEWS + "/*", LATEST_NEWS);
        matcher.addURI(authority, DatabaseContract.PATH_NEWS + "/*/#", LATEST_NEWS);
        matcher.addURI(authority, DatabaseContract.PATH_NEWS, LATEST_NEWS);

        return matcher;
    }

    // Queries
    private Cursor getLatestNews(String[] projection, String sortOrder) {
        return sNewsTableQueryBuilder.query(
                mNewsDatabaseHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getLatestNewsById(Uri uri, String[] projection, String sortOrder) {
        return sNewsTableQueryBuilder.query(
                mNewsDatabaseHelper.getReadableDatabase(),
                projection,
                DatabaseContract.NewsEntity.TABLE_NAME + "." + DatabaseContract.NewsEntity._ID + " = ? ",
                new String[] { DatabaseContract.NewsEntity.getIdFromUri(uri) },
                null,
                null,
                sortOrder
        );
    }

    // Builders
    static {
        sNewsTableQueryBuilder = new SQLiteQueryBuilder();
        sNewsTableQueryBuilder.setTables(DatabaseContract.NewsEntity.TABLE_NAME);
    }
}
