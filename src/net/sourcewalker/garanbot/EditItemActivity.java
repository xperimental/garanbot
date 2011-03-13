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
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
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
    private static final int DIALOG_PURCHASE_DATE = 10;
    private static final int DIALOG_WARRANTY_DATE = 11;
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
     * Callback method, which is executed when the user sets the date in the
     * date picker dialog. The callback updates the text display of the date
     * picker button to the new date.
     */
    private final DatePickerDialog.OnDateSetListener setPurchaseDateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(final DatePicker view, final int year,
                final int monthOfYear, final int dayOfMonth) {
            removeDialog(DIALOG_PURCHASE_DATE);
            final Calendar cal = Calendar.getInstance();
            cal.setTime(item.getPurchaseDate());
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            item.setPurchaseDate(cal.getTime());
            updateDisplay();
        }
    };

    /**
     * Callback method, which is executed when the user sets the date in the
     * date picker dialog. The callback updates the text display of the date
     * picker button to the new date.
     */
    private final DatePickerDialog.OnDateSetListener setWarrantyDateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(final DatePicker view, final int year,
                final int monthOfYear, final int dayOfMonth) {
            removeDialog(DIALOG_PURCHASE_DATE);
            final Calendar cal = Calendar.getInstance();
            cal.setTime(item.getEndOfWarranty());
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            item.setEndOfWarranty(cal.getTime());
            updateDisplay();
        }
    };

    /**
     * 
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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
        purchaseDateField.setOnClickListener(new DateDialogButtonListener(
                DIALOG_PURCHASE_DATE));
        endWarrentyDateField.setOnClickListener(new DateDialogButtonListener(
                DIALOG_WARRANTY_DATE));

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
     * Load the item from the database into the local variable.
     * 
     * @param itemUri
     *            Content URI of database item.
     */
    private void loadItemData(final Uri itemUri) {
        final Cursor cursor = managedQuery(itemUri,
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
        purchaseDateField.setText(formatDate(item.getPurchaseDate()));
        endWarrentyDateField.setText(formatDate(item.getEndOfWarranty()));
    }

    private String formatDate(final Date date) {
        synchronized (DATE_FORMAT) {
            return DATE_FORMAT.format(date);
        }
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateDialog(int, android.os.Bundle)
     */
    @Override
    protected Dialog onCreateDialog(final int id, final Bundle bundle) {
        DatePickerDialog dialog;
        final Calendar cal = Calendar.getInstance();
        switch (id) {
        case DIALOG_PURCHASE_DATE:
            cal.setTime(item.getPurchaseDate());
            dialog = new DatePickerDialog(this, setPurchaseDateListener,
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH));
            break;
        case DIALOG_WARRANTY_DATE:
            cal.setTime(item.getEndOfWarranty());
            dialog = new DatePickerDialog(this, setWarrantyDateListener,
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY),
                    cal.get(Calendar.DAY_OF_MONTH));
            break;
        default:
            throw new IllegalArgumentException("Unknown dialog: " + id);
        }
        dialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(final DialogInterface arg0) {
                removeDialog(id);
            }
        });
        return dialog;
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
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case R.id.edit_save:
            saveItem();
            break;
        case R.id.edit_cancel:
            setResult(RESULT_CANCELED);
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
        item.setName(nameField.getText().toString());
        item.setManufacturer(manufacturerField.getText().toString());
        item.setItemType(modelField.getText().toString());
        item.setLocation(locationField.getText().toString());
        item.setNotes(notesField.getText().toString());
        item.setVendor(vendorField.getText().toString());
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
        setResult(RESULT_OK);
        finish();
    }

    private class DateDialogButtonListener implements OnClickListener {

        private final int dialogId;

        public DateDialogButtonListener(final int dialogId) {
            this.dialogId = dialogId;
        }

        @Override
        public void onClick(final View v) {
            showDialog(dialogId);
        }

    }

}
