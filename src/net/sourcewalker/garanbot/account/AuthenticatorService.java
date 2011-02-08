package net.sourcewalker.garanbot.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * This service provides the interface to the account authenticator used by the
 * systems' account manager.
 * 
 * @author Xperimental
 */
public class AuthenticatorService extends Service {

    private GaranboAuthenticator authenticator;

    /*
     * (non-Javadoc)
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();

        authenticator = new GaranboAuthenticator(this);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }

}
