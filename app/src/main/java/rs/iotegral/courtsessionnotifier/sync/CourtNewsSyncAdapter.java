package rs.iotegral.courtsessionnotifier.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import rs.iotegral.courtsessionnotifier.BuildConfig;
import rs.iotegral.courtsessionnotifier.R;
import rs.iotegral.courtsessionnotifier.activities.MainActivity;
import rs.iotegral.courtsessionnotifier.api.CourtNewsModel;
import rs.iotegral.courtsessionnotifier.api.Result;
import rs.iotegral.courtsessionnotifier.contracts.DatabaseContract;
import rs.iotegral.courtsessionnotifier.utils.Utils;

/**
 * Created by Lauda on 8/20/2018 22:26.
 */
public class CourtNewsSyncAdapter extends AbstractThreadedSyncAdapter {
    private final String LOG_TAG = CourtNewsSyncAdapter.class.getSimpleName();
    private Context mContext;
    private static final int NEWS_NOTIFICATION_ID = 3017;
    private static final int MAX_NEWS_COUNT = 5; // TODO: Switch to preferences
    private static final String NOTIFICATION_CHANNEL_ID = "court_news_notification_01";

    CourtNewsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting court news sync");

        mContext.sendBroadcast(new Intent().setAction("COURT_NEWS_SYNC_STARTED"));

        HttpURLConnection urlConnection = null;
        String jsonRespose;

        try {
            String BASE_URL = BuildConfig.CourtNewsApiUrl + MAX_NEWS_COUNT;
            BASE_URL = BASE_URL.replaceAll(" ", "%20");

            URL api_url = new URL(BASE_URL);

            // Create http request to endpoint and open the connection
            urlConnection = (HttpURLConnection) api_url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept-Encoding", "identity");
            urlConnection.setRequestProperty("Content-length", "0");
            urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
            urlConnection.setUseCaches(false);
            urlConnection.connect();

            InputStream content = (InputStream) urlConnection.getContent();

            StringBuilder builder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(content, "UTF-8"));
            String line;

            while ((line = bufferedReader.readLine()) != null)
                builder.append(line);

            jsonRespose = builder.toString();

            getNewsDataFromJson(jsonRespose);
            mContext.sendBroadcast(new Intent().setAction("COURT_NEWS_SYNC_FINISHED"));
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            ex.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private void getNewsDataFromJson(String jsonResponse) {
        CourtNewsModel model = new Gson().fromJson(jsonResponse, CourtNewsModel.class);

        Vector<ContentValues> latestNews = new Vector<>(MAX_NEWS_COUNT);

        // Get all to compare by Id // TODO: stinky implementation, implement proper comparer later
        Cursor allNews = mContext.getContentResolver().query(DatabaseContract.NewsEntity.CONTENT_URI, null, null, null, null);
        try {
            List<String> databaseDataIds = new ArrayList<>();
            List<String> apiDataIds = new ArrayList<>();

            if (Objects.requireNonNull(allNews).moveToFirst()) {
                do {
                    databaseDataIds.add(allNews.getString(1));
                } while (allNews.moveToNext());
            }

            for (int i = 0; i < model.getData().getResults().size(); i++) {
                apiDataIds.add(model.getData().getResults().get(i).getRowGuid());
                latestNews.add(generateNewsValue(model.getData().getResults().get(i)));
            }

            apiDataIds.removeAll(databaseDataIds);
            if (apiDataIds.size() == 0)
            {
                // There are no changes
                return;
            }
        } catch(Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        } finally {
            if (allNews != null && !allNews.isClosed())
                allNews.close();
        }

        if (latestNews.size() > 0) {
            // Delete old data
            mContext.getContentResolver().delete(DatabaseContract.NewsEntity.CONTENT_URI, null, null);

            // Insert new data
            ContentValues[] bulkData = new ContentValues[latestNews.size()];
            latestNews.toArray(bulkData);
            mContext.getContentResolver().bulkInsert(DatabaseContract.NewsEntity.CONTENT_URI, bulkData);
        }

        // Notification
        if (PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(mContext.getString(R.string.court_news_notification_value), Boolean.parseBoolean("true")))
            showNotification(latestNews.firstElement());
    }

    private void showNotification(ContentValues latestNews) {
        if (latestNews == null)
            return;

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Court news notification", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Court news notification channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            Objects.requireNonNull(notificationManager).createNotificationChannel(notificationChannel);
        }


        final Bitmap newsIcon = BitmapFactory.decodeResource(mContext.getResources(), Utils.getWeatherIcon("news_icon", mContext));

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)
                        .setAutoCancel(true)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setLargeIcon(newsIcon)
                        .setContentTitle(latestNews.getAsString(DatabaseContract.NewsEntity.COLUMN_NEWS_TITLE))
                        .setContentText(latestNews.getAsString(DatabaseContract.NewsEntity.COLUMN_NEWS_DESCRIPTION))
                        .setOngoing(false);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder
                .setVibrate(new long[]{0, 500, 500, 500})
                .setLights(Color.BLUE, 500, 1000);
        }

        TaskStackBuilder tsb = TaskStackBuilder.create(mContext);
        tsb.addNextIntent(new Intent(mContext, MainActivity.class));
        builder.setContentIntent(tsb.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));

        Objects.requireNonNull(notificationManager).notify(NEWS_NOTIFICATION_ID, builder.build());
    }

    private static ContentValues generateNewsValue(Result news) {
        ContentValues container = new ContentValues();
        container.put(DatabaseContract.NewsEntity.COLUMN_NEWS_GUID, news.getRowGuid());
        container.put(DatabaseContract.NewsEntity.COLUMN_NEWS_TITLE, news.getTitle());
        container.put(DatabaseContract.NewsEntity.COLUMN_NEWS_DESCRIPTION, news.getDescription());
        container.put(DatabaseContract.NewsEntity.COLUMN_NEWS_LAST_UPDATED, DatabaseContract.extractDate(news.getLastUpdated()));
        container.put(DatabaseContract.NewsEntity.COLUMN_NEWS_PUBLISHED, DatabaseContract.extractDate(news.getLastUpdated()));

        return container;
    }

    /**
     * Helper method to schedule periodic sync adapter
     *
     * @param context      context
     * @param syncInterval sync interval
     */
    public static void configurePeriodicSync(Context context, int syncInterval, boolean remove) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);

        if (remove)
            ContentResolver.removePeriodicSync(account, authority, Bundle.EMPTY);

        ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY, syncInterval);
    }

    /**
     * Helper method to make sync adapter sync immediately
     *
     * @param context context
     */
    public static void syncNow(Context context) {
        Bundle b = new Bundle();
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), b);
    }

    /**
     * Helper method to get a fake account for SyncAdapter needs. If not found, we make a new fake account.
     * In case we make an account, we call @onAccountCreated to get things initialized.
     *
     * @param context context
     * @return new account
     */
    private static Account getSyncAccount(Context context) {
        // Get Android account manager instance
        AccountManager accMgr = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create account
        Account newAcc = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        assert accMgr != null;
        if (accMgr.getPassword(newAcc) == null) {
            // Add the account and account type. No pass or user
            if (!accMgr.addAccountExplicitly(newAcc, "", null))
                return null;

           // In case android:syncable="true" isn't set in manifest <provider> element, call ContentResolver.setIsSyncable(...) here
            onAccountCreated(newAcc, context);
        }
        return newAcc;
    }

    /**
     * Helper method to get thins initialized after new fake account creation
     *
     * @param newAcc  new @Account
     * @param context context
     */
    private static void onAccountCreated(Account newAcc, Context context) {
        // After account creation, configure auto-sync
        final int sync = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("court_news_sync_frequency", "900"));
        CourtNewsSyncAdapter.configurePeriodicSync(context, sync, false);
        ContentResolver.setSyncAutomatically(newAcc, context.getString(R.string.content_authority), true);

        // Let's sync data now to get things started
        syncNow(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
