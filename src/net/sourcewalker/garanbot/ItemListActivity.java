package net.sourcewalker.garanbot;

import java.io.IOException;

import net.sourcewalker.garanbot.api.ClientException;
import net.sourcewalker.garanbot.api.Item;
import net.sourcewalker.garanbot.data.GaranboItemsProvider;
import net.sourcewalker.garanbot.data.GaranbotDBMetaData;
import net.sourcewalker.garanbot.data.sync.GaranboSyncAdapter;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * This activity displays the contents of the local database. The local database
 * is periodically synchronized with the Garanbo servers. Additionally the user
 * can start the synchronization manually. This activity will be the
 * "main activity" of the application. Therefore it should have a menu with
 * items not only for editing the list contents but also for application-wide
 * settings.
 * 
 * @author Xperimental
 */
public class ItemListActivity extends ListActivity {

    private static final String TAG = "ItemListActivity";

    private String accountType;
    private AccountManager accountManager;
    private SyncStatusReceiver syncReceiver;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        registerForContextMenu(getListView());

        accountType = getString(R.string.account_type);
        accountManager = AccountManager.get(this);
        if (getAccount() == null) {
            startCreateAccount();
        }

        syncReceiver = new SyncStatusReceiver();

        // query content provider to receive all garanbo items
        Cursor cursor = managedQuery(GaranboItemsProvider.CONTENT_URI_ITEMS,
                new String[] { GaranbotDBMetaData._ID, GaranbotDBMetaData.NAME,
                        GaranbotDBMetaData.MANUFACTURER,
                        GaranbotDBMetaData.IMAGE_URI }, null, null,
                GaranbotDBMetaData.DEFAULT_SORT_ORDER);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.list_item, cursor, new String[] {
                        GaranbotDBMetaData.NAME,
                        GaranbotDBMetaData.MANUFACTURER,
                        GaranbotDBMetaData.IMAGE_URI }, new int[] {
                        R.id.firstLine, R.id.secondLine, R.id.icon });
        setListAdapter(adapter);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

        syncReceiver.setEnabled(true);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        syncReceiver.setEnabled(false);

        super.onPause();
    }

    private Account getAccount() {
        Account[] accounts = accountManager.getAccountsByType(accountType);
        if (accounts.length == 0) {
            return null;
        } else {
            return accounts[0];
        }
    }

    /**
     * Requests the creation of a new account in the account manager.
     */
    private void startCreateAccount() {
        AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {

            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                boolean created = false;
                try {
                    Bundle result = future.getResult();
                    String username = (String) result
                            .get(AccountManager.KEY_ACCOUNT_NAME);
                    if (username != null) {
                        created = true;
                    }
                } catch (OperationCanceledException e) {
                } catch (AuthenticatorException e) {
                } catch (IOException e) {
                }
                if (!created) {
                    Toast.makeText(ItemListActivity.this,
                            R.string.toast_needaccount, Toast.LENGTH_LONG)
                            .show();
                    finish();
                }
            }
        };
        accountManager.addAccount(accountType, null, null, null, this,
                callback, null);
    }

    /*
     * (non-Javadoc)
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView,
     * android.view.View, int, long)
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent viewIntent = new Intent(this, ViewItemActivity.class);
        viewIntent.setAction(Long.toString(id));
        startActivity(viewIntent);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_add:
            startActivity(new Intent(this, EditItemActivity.class));
            break;
        case R.id.menu_load:
            Account account = getAccount();
            if (account != null) {
                ContentResolver.requestSync(account,
                        GaranbotDBMetaData.AUTHORITY, new Bundle());
            }
            break;
        case R.id.menu_settings:
            startActivity(new Intent(this, SettingsActivity.class));
            break;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
     * android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.list_context, menu);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenuInfo menuInfo = item.getMenuInfo();
        switch (item.getItemId()) {
        case R.id.menu_delete:
            if (menuInfo instanceof AdapterContextMenuInfo) {
                AdapterContextMenuInfo adapterInfo = (AdapterContextMenuInfo) menuInfo;
                deleteItem(adapterInfo.id);
            }
            break;
        default:
            throw new IllegalArgumentException("Unknown menu item: " + item);
        }
        return true;
    }

    /**
     * Delete the selected item from the local database.
     * 
     * @param id
     *            Id of item to delete.
     */
    private void deleteItem(long id) {
        Uri itemUri = ContentUris.withAppendedId(
                GaranboItemsProvider.CONTENT_URI_ITEMS, id);
        Cursor cursor = managedQuery(itemUri, null, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                Item item = Item.fromCursor(cursor);
                item.setDeleted(true);
                int count = getContentResolver().update(itemUri,
                        item.toContentValues(), null, null);
                if (count != 1) {
                    Toast.makeText(this, R.string.toast_list_deleteerror,
                            Toast.LENGTH_LONG).show();
                }
            }
        } catch (ClientException e) {
            Log.e(TAG, "Error parsing item: " + e);
        }
    }

    private class SyncStatusReceiver extends BroadcastReceiver {

        private final IntentFilter filter;

        public SyncStatusReceiver() {
            filter = new IntentFilter(GaranboSyncAdapter.BROADCAST_ACTION);
        }

        /*
         * (non-Javadoc)
         * @see
         * android.content.BroadcastReceiver#onReceive(android.content.Context,
         * android.content.Intent)
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean running = intent.getExtras().getBoolean(
                    GaranboSyncAdapter.EXTRA_RUNNING);
            setProgressBarIndeterminateVisibility(running);
        }

        public void setEnabled(boolean value) {
            if (value) {
                registerReceiver(this, filter);
            } else {
                unregisterReceiver(this);
            }
        }

    }

}
