package net.sourcewalker.garanbot.data.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * This service is started by the platform when a synchronization is started.
 * 
 * @author Xperimental
 */
public class SyncService extends Service {

    private GaranboSyncAdapter syncAdapter;

    /*
     * (non-Javadoc)
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();

        syncAdapter = new GaranboSyncAdapter(this);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent arg0) {
        return syncAdapter.getSyncAdapterBinder();
    }

}
