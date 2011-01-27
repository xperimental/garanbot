package net.sourcewalker.garanbot;

import net.sourcewalker.garanbot.api.Item;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO Get items from database
        Item[] dummyItems = new Item[3];
        dummyItems[0] = new Item(1);
        dummyItems[1] = new Item(2);
        dummyItems[2] = new Item(3);
        setListAdapter(new ArrayAdapter<Item>(this,
                android.R.layout.simple_list_item_1, dummyItems));
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
