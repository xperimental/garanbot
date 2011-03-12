package net.sourcewalker.garanbot;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import net.sourcewalker.garanbot.api.ClientException;
import net.sourcewalker.garanbot.api.Item;
import net.sourcewalker.garanbot.data.GaranboItemsProvider;
import net.sourcewalker.garanbot.data.GaranbotDBMetaData;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

/**
 * This activity should enable the user to create and edit items in the local
 * database. When the user chooses to save the (new) item he will be brought
 * back to the list activity and a synchronization with the servers should be
 * scheduled.
 * 
 * @author Xperimental
 */
public class EditItemActivity extends Activity {

    private static final String TAG = "EditItemActivity";
    private static final int DATE_DIALOG_ID = 0;
    private static final DateFormat DATE_FORMAT = DateFormat
            .getDateInstance(DateFormat.SHORT);

    private EditText nameField;
    private EditText manufacturerField;
    private EditText modelField;
    private EditText locationField;
    private EditText notesField;
    private EditText vendorField;
    private Button purchaseDateField;
    private Button endWarrentyDateField;
    private Item item;

    /**
     * If true, item already exists in database and is only edited.
     */
    private boolean editItem = false;

    /**
     * OnClickListener to launch a date picker if the purchase date button is
     * clicked.
     */
    private final OnClickListener datePickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            showDialog(DATE_DIALOG_ID);
        }

    };

    /**
     * Callback method, which is executed when the user sets the date in the
     * date picker dialog. The callback updates the text display of the date
     * picker button to the new date.
     */
    private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                int dayOfMonth) {
            removeDialog(DATE_DIALOG_ID);
            Calendar cal = Calendar.getInstance();
            cal.setTime(item.getPurchaseDate());
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            item.setPurchaseDate(cal.getTime());
            updateDisplay();
        }
    };

    /**
     * 
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_edit);

        // capture our View elements
        nameField = (EditText) findViewById(R.id.item_edit_name);
        manufacturerField = (EditText) findViewById(R.id.item_edit_manufacturer);
        modelField = (EditText) findViewById(R.id.item_edit_model);
        locationField = (EditText) findViewById(R.id.item_edit_location);
        notesField = (EditText) findViewById(R.id.item_edit_notes);
        vendorField = (EditText) findViewById(R.id.item_edit_vendor);
        purchaseDateField = (Button) findViewById(R.id.item_edit_purchase);
        endWarrentyDateField = (Button) findViewById(R.id.item_edit_endwarranty);

        // add a click listeners
        purchaseDateField.setOnClickListener(datePickListener);

        item = new Item(Item.UNKNOWN_ID);
        final String action = getIntent().getAction();
        if (action != null && action.length() > 0) {
            final Uri itemUri = Uri.parse(action);
            loadItemData(itemUri);
        }

        // display the current date
        updateDisplay();
    }

    /**
     * @param itemUri
     */
    private void loadItemData(Uri itemUri) {
        Cursor cursor = managedQuery(itemUri,
                GaranbotDBMetaData.DEFAULT_PROJECTION, null, null, null);
        if (cursor.moveToFirst()) {
            try {
                item = Item.fromCursor(cursor);
                editItem = true;
            } catch (ClientException e) {
                Log.e(TAG, "Can't parse item: " + e.toString());
            }
        }
    }

    /**
     * Updates the date on the date picker button.
     */
    private void updateDisplay() {
        nameField.setText(item.getName());
        manufacturerField.setText(item.getManufacturer());
        modelField.setText(item.getItemType());
        locationField.setText(item.getLocation());
        notesField.setText(item.getNotes());
        vendorField.setText(item.getVendor());
        purchaseDateField.setText(DATE_FORMAT.format(item.getPurchaseDate()));
        endWarrentyDateField
                .setText(DATE_FORMAT.format(item.getEndOfWarranty()));
    }

    /**
     * Displays the date picker dialog.
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            Calendar cal = Calendar.getInstance();
            cal.setTime(item.getPurchaseDate());
            return new DatePickerDialog(this, mDateSetListener,
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.edit_options, menu);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.edit_save:
            saveItem();
            break;
        case R.id.edit_cancel:
            finish();
            break;
        default:
            throw new IllegalArgumentException("Unknown menu item: " + item);
        }
        return true;
    }

    /**
     * Creates a new item in the database.
     */
    private void saveItem() {
        String name = nameField.getText().toString();
        String manufacturer = manufacturerField.getText().toString();
        String itemtype = modelField.getText().toString();
        String location = locationField.getText().toString();
        String notes = notesField.getText().toString();
        String vendor = vendorField.getText().toString();

        item.setName(name);
        item.setManufacturer(manufacturer);
        item.setItemType(itemtype);
        item.setLocation(location);
        item.setNotes(notes);
        item.setVendor(vendor);
        item.setLastModified(new Date());

        if (editItem) {
            getContentResolver().update(
                    ContentUris.withAppendedId(
                            GaranboItemsProvider.CONTENT_URI_ITEMS,
                            item.getLocalId()), item.toContentValues(), null,
                    null);
        } else {
            getContentResolver().insert(GaranboItemsProvider.CONTENT_URI_ITEMS,
                    item.toContentValues());
        }
        finish();
    }

}
