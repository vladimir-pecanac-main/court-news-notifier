package rs.iotegral.courtsessionnotifier.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Lauda on 8/20/2018 22:17.
 */
public class CourtSessionAuthenticatorService extends Service {
    private CourtSessionAuthenticator mAccountAuth;

    @Override
    public void onCreate() {
        mAccountAuth = new CourtSessionAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAccountAuth.getIBinder();
    }
}
