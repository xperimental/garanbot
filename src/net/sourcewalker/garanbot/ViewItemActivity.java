package net.sourcewalker.garanbot;

import java.text.DateFormat;

import net.sourcewalker.garanbot.api.ClientException;
import net.sourcewalker.garanbot.api.GaranboClient;
import net.sourcewalker.garanbot.api.Item;
import net.sourcewalker.garanbot.data.GaranboItemsProvider;
import net.sourcewalker.garanbot.data.GaranbotDBMetaData;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
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
    private ImageView imageView;
    private TextView nameField;
    private TextView manufacturerField;
    private TextView modelField;
    private TextView vendorField;
    private TextView locationField;
    private TextView purchaseField;
    private TextView warrantyField;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.item_view);

        imageView = (ImageView) findViewById(R.id.item_view_image);
        nameField = (TextView) findViewById(R.id.item_view_name);
        manufacturerField = (TextView) findViewById(R.id.item_view_manufacturer);
        modelField = (TextView) findViewById(R.id.item_view_model);
        vendorField = (TextView) findViewById(R.id.item_view_vendor);
        locationField = (TextView) findViewById(R.id.item_view_location);
        purchaseField = (TextView) findViewById(R.id.item_view_purchase);
        warrantyField = (TextView) findViewById(R.id.item_view_endwarranty);

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
            Item dbItem;
            try {
                dbItem = Item.fromCursor(cursor);
                setData(dbItem);
            } catch (ClientException e) {
                Log.e(TAG, "Can't create Item: " + e.getMessage(), e);
                finish();
            }
        } else {
            Toast.makeText(this, R.string.toast_view_itemnotfound,
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setData(Item source) {
        DateFormat dateFormat = DateFormat.getDateInstance();
        nameField.setText(source.getName());
        manufacturerField.setText(source.getManufacturer());
        modelField.setText(source.getItemType());
        vendorField.setText(source.getVendor());
        locationField.setText(source.getLocation());
        purchaseField.setText(dateFormat.format(source.getPurchaseDate()));
        warrantyField.setText(dateFormat.format(source.getEndOfWarranty()));
        if (source.hasPicture()) {
            GetPictureTask task = new GetPictureTask();
            task.execute(source.getId());
        }
    }

    /**
     * Direct download of item picture until ContentProvider can provide that
     * data.
     * 
     * @author Xperimental
     */
    public class GetPictureTask extends AsyncTask<Integer, Void, Bitmap> {

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBarIndeterminateVisibility(true);
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Bitmap doInBackground(Integer... params) {
            int itemId = params[0];
            AccountManager accountManager = AccountManager
                    .get(ViewItemActivity.this);
            Account[] accounts = accountManager
                    .getAccountsByType(getString(R.string.account_type));
            Bitmap result = null;
            if (accounts.length > 0) {
                String username = accounts[0].name;
                String password = accountManager.getPassword(accounts[0]);
                GaranboClient client = new GaranboClient(username, password);
                try {
                    result = client.item().getPicture(itemId);
                } catch (ClientException e) {
                    // display error
                }
            }
            return result;
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            setProgressBarIndeterminateVisibility(false);
            if (result != null) {
                imageView.setImageBitmap(result);
            } else {
                Toast.makeText(ViewItemActivity.this,
                        R.string.toast_view_pictureerror, Toast.LENGTH_LONG)
                        .show();
            }
            super.onPostExecute(result);
        }

    }

}
