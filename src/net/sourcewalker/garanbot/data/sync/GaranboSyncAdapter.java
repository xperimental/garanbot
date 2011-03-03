package net.sourcewalker.garanbot.data.sync;

import java.io.IOException;
import java.util.List;

import net.sourcewalker.garanbot.api.ClientException;
import net.sourcewalker.garanbot.api.GaranboClient;
import net.sourcewalker.garanbot.api.Item;
import net.sourcewalker.garanbot.data.GaranboItemsProvider;
import net.sourcewalker.garanbot.data.GaranbotDBMetaData;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class provides the sync adapter used to merge the local database with
 * the Garanbo server.
 * 
 * @author Xperimental
 */
public class GaranboSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "GaranboSyncAdapter";

    private final AccountManager accountManager;

    public GaranboSyncAdapter(Context context) {
        super(context, true);

        this.accountManager = AccountManager.get(context);
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
        Log.d(TAG, "Sync started.");
        String username = account.name;
        String password = accountManager.getPassword(account);
        GaranboClient client = new GaranboClient(username, password);
        try {
            List<Integer> idList = client.item().list();
            if (getDbCount(provider) == 0) {
                // First time synchronization, just save all items from server
                Log.d(TAG, "Nothing in DB. Simple copy...");
                for (Integer id : idList) {
                    Item item = client.item().get(id);
                    provider.insert(GaranboItemsProvider.CONTENT_URI_ITEMS,
                            item.toContentValues());
                    Log.d(TAG, "Saved item: " + id);
                }
            } else if (idList.size() == 0) {
                // Other simple solution (copy all to server)
                Log.d(TAG, "Only local content. Copy to server...");
            } else {
                // More complicated sync...
                Log.d(TAG, "Both have content: have to sync...");
            }
        } catch (ClientException e) {
            Log.e(TAG, "Client exception: " + e);
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                syncResult.stats.numIoExceptions++;
            } else {
                syncResult.stats.numAuthExceptions++;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "DB exception: " + e);
            syncResult.stats.numIoExceptions++;
        }
        Log.d(TAG, "Sync ended.");
    }

    /**
     * Returns the number of items in the local database.
     * 
     * @param provider
     *            Content provider to query.
     * @return Number of items in local database.
     * @throws RemoteException
     *             If there is an error communicating with the database.
     */
    private int getDbCount(ContentProviderClient provider)
            throws RemoteException {
        Cursor query = provider.query(GaranboItemsProvider.CONTENT_URI_ITEMS,
                new String[] { GaranbotDBMetaData._ID }, null, null, null);
        int result = query.getCount();
        query.close();
        return result;
    }
}
