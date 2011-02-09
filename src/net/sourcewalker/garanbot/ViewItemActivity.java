package net.sourcewalker.garanbot;

import net.sourcewalker.garanbot.data.GaranboItemsProvider;
import net.sourcewalker.garanbot.data.GaranbotDBMetaData;
import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * This activity should display one item from the database in a read-only
 * manner. It should have a button or menu item to bring the user to the
 * {@link EditItemActivity} where he can edit the item.
 * 
 * @author Xperimental
 */
public class ViewItemActivity extends Activity {

    private static final String TAG = "ViewItemActivity";
    private long itemId;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_view);

        String action = getIntent().getAction();
        if (action == null) {
            finish();
        }
        try {
            itemId = Long.parseLong(action);
        } catch (NumberFormatException e) {
            Log.w(TAG, "Invalid ID: " + action);
            finish();
        }

        Cursor cursor = managedQuery(ContentUris.withAppendedId(
                GaranboItemsProvider.CONTENT_URI, itemId),
                GaranbotDBMetaData.DEFAULT_PROJECTION, null, null, null);
        if (cursor.moveToFirst()) {
            // TODO show data in GUI
        } else {
            Toast.makeText(this, R.string.toast_view_itemnotfound,
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

}
