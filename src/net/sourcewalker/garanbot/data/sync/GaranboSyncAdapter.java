package net.sourcewalker.garanbot.data.sync;

import java.util.ArrayList;
import java.util.List;

import net.sourcewalker.garanbot.api.AuthenticationException;
import net.sourcewalker.garanbot.api.ClientException;
import net.sourcewalker.garanbot.api.GaranboClient;
import net.sourcewalker.garanbot.api.Item;
import net.sourcewalker.garanbot.api.ModificationOrigin;
import net.sourcewalker.garanbot.data.GaranboItemsProvider;
import net.sourcewalker.garanbot.data.GaranbotDBMetaData;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentUris;
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
                    syncResult.stats.numInserts++;
                }
            } else if (idList.size() == 0) {
                // Other simple solution (copy all to server)
                Log.d(TAG, "Only local content. Copy to server...");
                List<Item> localItems = getAllDbItems(provider);
                for (Item item : localItems) {
                    if (item.getModifiedAt() == ModificationOrigin.CREATED) {
                        item.setServerId(Item.UNKNOWN_ID);
                        int id = client.item().create(item);
                        item.setServerId(id);
                        provider.update(ContentUris.withAppendedId(
                                GaranboItemsProvider.CONTENT_URI_ITEMS,
                                item.getLocalId()), item.toContentValues(),
                                null, null);
                        Log.d(TAG, "Uploaded item: " + id);
                        syncResult.stats.numInserts++;
                    } else {
                        provider.delete(ContentUris.withAppendedId(
                                GaranboItemsProvider.CONTENT_URI_ITEMS,
                                item.getLocalId()), null, null);
                        Log.d(TAG, "Deleted invalid item: " + item.getName());
                        syncResult.stats.numDeletes++;
                    }
                }
            } else {
                // More complicated sync...
                Log.d(TAG, "Both have content: have to sync...");
            }
        } catch (AuthenticationException e) {
            Log.e(TAG, "Authentication error.");
            syncResult.stats.numAuthExceptions++;
        } catch (ClientException e) {
            Log.e(TAG, "Client exception: " + e);
            syncResult.stats.numIoExceptions++;
        } catch (RemoteException e) {
            Log.e(TAG, "DB exception: " + e);
            syncResult.stats.numIoExceptions++;
        }
        Log.d(TAG, "Sync ended.");
    }

    /**
     * Get a list of all database items.
     * 
     * @param provider
     *            Content provider to query.
     * @return List of all items in database.
     * @throws RemoteException
     *             If there is an error communicating with the database.
     * @throws ClientException
     *             If an item cannot be parsed.
     */
    private List<Item> getAllDbItems(ContentProviderClient provider)
            throws RemoteException, ClientException {
        List<Item> result = new ArrayList<Item>();
        Cursor query = provider.query(GaranboItemsProvider.CONTENT_URI_ITEMS,
                GaranbotDBMetaData.DEFAULT_PROJECTION, null, null, null);
        while (query.moveToNext()) {
            Item item = Item.fromCursor(query);
            result.add(item);
        }
        query.close();
        return result;
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
