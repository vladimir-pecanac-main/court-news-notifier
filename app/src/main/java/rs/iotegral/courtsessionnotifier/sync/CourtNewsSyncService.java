package rs.iotegral.courtsessionnotifier.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Lauda on 8/20/2018 22:25.
 */
public class CourtNewsSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static CourtNewsSyncAdapter sCourtNewsSyncAdapter = null;
    private static final String sLogTag = CourtNewsSyncService.class.getSimpleName();

    @Override
    public void onCreate() {
        Log.d(sLogTag, "CourtNewsSyncService initialized");

        synchronized (sSyncAdapterLock) {
            if (sCourtNewsSyncAdapter == null)
                sCourtNewsSyncAdapter = new CourtNewsSyncAdapter(getApplicationContext(), true);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sCourtNewsSyncAdapter.getSyncAdapterBinder();
    }
}
