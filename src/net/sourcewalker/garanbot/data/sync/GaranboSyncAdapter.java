package net.sourcewalker.garanbot.data.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * This class provides the sync adapter used to merge the local database with
 * the Garanbo server.
 * 
 * @author Xperimental
 */
public class GaranboSyncAdapter extends AbstractThreadedSyncAdapter {

    public GaranboSyncAdapter(Context context) {
        super(context, true);
    }

    /*
     * (non-Javadoc)
     * @see
     * android.content.AbstractThreadedSyncAdapter#onPerformSync(android.accounts
     * .Account, android.os.Bundle, java.lang.String,
     * android.content.ContentProviderClient, android.content.SyncResult)
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
            ContentProviderClient provider, SyncResult syncResult) {
        // TODO Auto-generated method stub

    }

}
