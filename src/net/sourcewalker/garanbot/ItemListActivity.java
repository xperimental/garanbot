package net.sourcewalker.garanbot;

import java.io.IOException;

import net.sourcewalker.garanbot.api.Item;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
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

    private String accountType;
    private AccountManager accountManager;

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

        // TODO Get items from database
        Item[] dummyItems = new Item[3];
        Item item1 = new Item(1);
        item1.setName("tolles Teil1");
        item1.setManufacturer("hersteller");
        dummyItems[0] = item1;

        Item item2 = new Item(2);
        item2.setName("tolles Teil2");
        item2.setManufacturer("hersteller2");
        dummyItems[1] = item2;

        Item item3 = new Item(3);
        item3.setName("tolles Teil3");
        item3.setManufacturer("hersteller3");
        dummyItems[2] = item3;

        setListAdapter(new ItemsAdapter(this, dummyItems));
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
