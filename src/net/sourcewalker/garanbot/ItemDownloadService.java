package net.sourcewalker.garanbot;

import java.util.List;

import net.sourcewalker.garanbot.api.ClientException;
import net.sourcewalker.garanbot.api.GaranboClient;
import net.sourcewalker.garanbot.api.Item;
import net.sourcewalker.garanbot.data.GaranboItemsProvider;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Simple service which replaces the current database content with the Items
 * saved on the Garanbo server.
 * 
 * @author Xperimental
 */
public class ItemDownloadService extends IntentService {

    private static final String TAG = "ItemDownloadService";

    public ItemDownloadService() {
        super("ItemDownloadServiceThread");
    }

    /*
     * (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager
                .getAccountsByType(getString(R.string.account_type));
        if (accounts.length > 0) {
            String username = accounts[0].name;
            String password = accountManager.getPassword(accounts[0]);
            GaranboClient client = new GaranboClient(username, password);
            try {
                List<Integer> itemList = client.item().list();
                if (itemList.size() > 0) {
                    // Empty database
                    getContentResolver().delete(
                            GaranboItemsProvider.CONTENT_URI, null, null);
                    for (Integer id : itemList) {
                        Item item = client.item().get(id);
                        ContentValues values = item.toContentValues();
                        getContentResolver().insert(
                                GaranboItemsProvider.CONTENT_URI, values);
                    }
                }
            } catch (ClientException e) {
                Log.e(TAG, "Client error: " + e.getMessage());
            }
        }
    }

}
