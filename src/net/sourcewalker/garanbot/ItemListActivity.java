package net.sourcewalker.garanbot;

import java.io.IOException;

import net.sourcewalker.garanbot.api.Item;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import net.sourcewalker.garanbot.data.GaranboItemsProvider;
import net.sourcewalker.garanbot.data.GaranbotDBMetaData;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.SimpleCursorAdapter;

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

    private String accountType;
    private AccountManager accountManager;
    private final String[] projection = new String[] { "_id", "name" };

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountType = getString(R.string.account_type);
        accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(accountType);
        if (accounts.length == 0) {
            startCreateAccount();
        }
        // query content provider to receive all garanbo items
        Cursor cursor = managedQuery(GaranboItemsProvider.CONTENT_URI,
                projection, null, null, GaranbotDBMetaData.DEFAULT_SORT_ORDER);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item, cursor, new String[] {
                        "_id", "name" }, new int[] { android.R.id.text1,
                        android.R.id.text2 });
        setListAdapter(adapter);
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
        startActivity(new Intent(this, ViewItemActivity.class));
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
        case R.id.menu_settings:
            startActivity(new Intent(this, SettingsActivity.class));
            break;
        }
        return true;
    }

}
