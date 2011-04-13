package net.sourcewalker.garanbot;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import net.sourcewalker.garanbot.api.BarcodeResult;
import net.sourcewalker.garanbot.api.ClientException;
import net.sourcewalker.garanbot.api.GaranboClient;
import net.sourcewalker.garanbot.api.Item;
import net.sourcewalker.garanbot.data.GaranboItemsProvider;
import net.sourcewalker.garanbot.data.GaranbotDBMetaData;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
    private static final int DIALOG_PROGRESS_BARCODE = 12;
    private static final int DIALOG_CONFIRM_BARCODE = 13;
    private static final int DIALOG_RESULT_BARCODE = 14;
    private static final DateFormat DATE_FORMAT = DateFormat
            .getDateInstance(DateFormat.SHORT);
    private static final String EXTRA_BARCODE = "barcode";
    private static final String EXTRA_RESULT = "result";

    private EditText nameField;
    private EditText manufacturerField;
    private EditText modelField;
    private EditText locationField;
    private EditText notesField;
    private EditText vendorField;
    private Button purchaseDateField;
    private Button endWarrentyDateField;
    private Item item;
    private BarcodeSearchTask barcodeTask;

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

        barcodeTask = new BarcodeSearchTask();

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
        Dialog dialog;
        switch (id) {
        case DIALOG_PURCHASE_DATE:
            dialog = createDateDialog(id, item.getPurchaseDate(),
                    setPurchaseDateListener);
            break;
        case DIALOG_WARRANTY_DATE:
            dialog = createDateDialog(id, item.getEndOfWarranty(),
                    setWarrantyDateListener);
            break;
        case DIALOG_PROGRESS_BARCODE:
            dialog = createProgressDialog();
            break;
        case DIALOG_CONFIRM_BARCODE:
            dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.edit_barcode_dialog_title)
                    .setMessage(R.string.edit_barcode_dialog_confirm)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes,
                            new BarcodeConfirmListener(bundle))
                    .setNegativeButton(android.R.string.no, null).create();
            break;
        case DIALOG_RESULT_BARCODE:
            dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.edit_barcode_dialog_title)
                    .setMessage(R.string.edit_barcode_dialog_result)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes,
                            new BarcodeApplyListener(bundle))
                    .setNegativeButton(android.R.string.no, null).create();
            break;
        default:
            throw new IllegalArgumentException("Unknown dialog: " + id);
        }
        return dialog;
    }

    private ProgressDialog createProgressDialog() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(getString(R.string.edit_barcode_dialog_title));
        dialog.setMessage(getString(R.string.edit_barcode_dialog_progress));
        dialog.setCancelable(true);
        dialog.setOnCancelListener(barcodeTask);
        return dialog;
    }

    private DatePickerDialog createDateDialog(final int dialogId,
            final Date date, final OnDateSetListener listener) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final DatePickerDialog dialog = new DatePickerDialog(this, listener,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY),
                cal.get(Calendar.DAY_OF_MONTH));
        dialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(final DialogInterface arg0) {
                removeDialog(dialogId);
            }
        });
        return dialog;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onPrepareDialog(int, android.app.Dialog)
     */
    @Override
    protected void onPrepareDialog(final int id, final Dialog dialog,
            final Bundle extras) {
        final String messageFormat, barcode;
        switch (id) {
        case DIALOG_CONFIRM_BARCODE:
            final AlertDialog confirmDialog = (AlertDialog) dialog;
            messageFormat = getString(R.string.edit_barcode_dialog_confirm);
            barcode = extras.getString(EXTRA_BARCODE);
            confirmDialog.setMessage(String.format(messageFormat, barcode));
            break;
        case DIALOG_PROGRESS_BARCODE:
            final ProgressDialog progressDialog = (ProgressDialog) dialog;
            messageFormat = getString(R.string.edit_barcode_dialog_progress);
            barcode = extras.getString(EXTRA_BARCODE);
            progressDialog.setMessage(String.format(messageFormat, barcode));
            break;
        case DIALOG_RESULT_BARCODE:
            final AlertDialog applyDialog = (AlertDialog) dialog;
            messageFormat = getString(R.string.edit_barcode_dialog_result);
            final BarcodeResult result = extras.getParcelable(EXTRA_RESULT);
            applyDialog.setMessage(String.format(messageFormat,
                    result.getName()));
            break;
        case DIALOG_PURCHASE_DATE:
        case DIALOG_WARRANTY_DATE:
            break;
        default:
            throw new IllegalArgumentException("Unknown dialog id: " + id);
        }
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
        case R.id.edit_scan:
            IntentIntegrator.initiateScan(this,
                    getString(R.string.scan_install_title),
                    getString(R.string.scan_install_message),
                    getString(android.R.string.yes),
                    getString(android.R.string.no),
                    IntentIntegrator.PRODUCT_CODE_TYPES);
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
        item.getLocalState().setDetailsChanged();

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

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    protected void onActivityResult(final int requestCode,
            final int resultCode, final Intent data) {
        final IntentResult scanResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data);
        if (scanResult != null) {
            final String contents = scanResult.getContents();
            final String formatName = scanResult.getFormatName();
            if (contents == null || formatName == null) {
                Toast.makeText(this, R.string.scan_cancelled, Toast.LENGTH_LONG);
            } else {
                startParseBarcode(contents);
            }
        }
    }

    /**
     * Show a progress dialog while the barcode is sent to the Garanbo server to
     * search for product details.
     * 
     * @param contents
     *            Barcode as String
     */
    private void startParseBarcode(final String contents) {
        final Bundle extras = new Bundle();
        extras.putString(EXTRA_BARCODE, contents);
        showDialog(DIALOG_CONFIRM_BARCODE, extras);
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

    public class BarcodeConfirmListener implements
            android.content.DialogInterface.OnClickListener {

        private final Bundle extras;

        public BarcodeConfirmListener(final Bundle extras) {
            this.extras = extras;
        }

        /*
         * (non-Javadoc)
         * @see
         * android.content.DialogInterface.OnClickListener#onClick(android.content
         * .DialogInterface, int)
         */
        @Override
        public void onClick(final DialogInterface dialog, final int which) {
            showDialog(DIALOG_PROGRESS_BARCODE, extras);

            final String barcode = extras.getString(EXTRA_BARCODE);
            barcodeTask.execute(barcode);
        }

    }

    private class BarcodeApplyListener implements
            android.content.DialogInterface.OnClickListener {

        private final Bundle extras;

        public BarcodeApplyListener(final Bundle extras) {
            this.extras = extras;
        }

        /*
         * (non-Javadoc)
         * @see
         * android.content.DialogInterface.OnClickListener#onClick(android.content
         * .DialogInterface, int)
         */
        @Override
        public void onClick(final DialogInterface dialog, final int which) {
            final BarcodeResult result = extras.getParcelable(EXTRA_RESULT);
            item.setName(result.getName());
            item.setManufacturer(result.getManufacturer());
            item.setItemType(result.getType());

            updateDisplay();
        }
    }

    private class BarcodeSearchTask extends
            AsyncTask<String, Void, BarcodeResult> implements OnCancelListener {

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected BarcodeResult doInBackground(final String... params) {
            final String barcode = params[0];
            final AccountManager accountManager = AccountManager
                    .get(EditItemActivity.this);
            final Account[] accounts = accountManager
                    .getAccountsByType(getString(R.string.account_type));
            if (accounts.length == 1) {
                final Account account = accounts[0];
                final String password = accountManager.getPassword(account);
                final GaranboClient client = new GaranboClient(account.name,
                        password);
                try {
                    return client.searchEan(barcode);
                } catch (ClientException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(BarcodeResult result) {
            dismissDialog(DIALOG_PROGRESS_BARCODE);

            if (result == null) {
                Toast.makeText(EditItemActivity.this,
                        R.string.toast_scan_not_found, Toast.LENGTH_LONG)
                        .show();
            } else {
                Bundle extras = new Bundle();
                extras.putParcelable(EXTRA_RESULT, result);
                showDialog(DIALOG_RESULT_BARCODE, extras);
            }
        }

        /*
         * (non-Javadoc)
         * @see
         * android.content.DialogInterface.OnCancelListener#onCancel(android
         * .content.DialogInterface)
         */
        @Override
        public void onCancel(DialogInterface dialog) {
            this.cancel(true);
        }

    }

}
