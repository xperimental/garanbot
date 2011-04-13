package net.sourcewalker.garanbot;

import net.sourcewalker.garanbot.api.ClientException;
import net.sourcewalker.garanbot.api.Item;
import net.sourcewalker.garanbot.data.GaranboItemsProvider;
import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public final class ItemUtilities {

    private static final String TAG = "ItemUtilities";

    private ItemUtilities() {
    }

    public static void deleteItem(final Activity activity, final long itemId) {
        final Uri itemUri = ContentUris.withAppendedId(
                GaranboItemsProvider.CONTENT_URI_ITEMS, itemId);
        final Cursor cursor = activity.managedQuery(itemUri, null, null, null,
                null);
        try {
            if (cursor.moveToFirst()) {
                final Item item = Item.fromCursor(cursor);
                item.getLocalState().setDeleted();
                final int count = activity.getContentResolver().update(itemUri,
                        item.toContentValues(), null, null);
                if (count != 1) {
                    Toast.makeText(activity, R.string.toast_list_deleteerror,
                            Toast.LENGTH_LONG).show();
                }
            }
        } catch (ClientException e) {
            Log.e(TAG, "Error parsing item: " + e);
        }
    }

}
