package net.sourcewalker.garanbot;

import java.util.Calendar;

import net.sourcewalker.garanbot.data.GaranboItemsProvider;
import net.sourcewalker.garanbot.data.GaranbotDBMetaData;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
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

    private Button applyEditButton;
    private Button cancelEditButton;
    private Button mPickDate;
    private int mYear;
    private int mMonth;
    private int mDay;

    static final int DATE_DIALOG_ID = 0;

    /**
     * OnClickListener for the apply edit button. It retrieves the data from the
     * editable fields and stores them to the database.
     */
    private final OnClickListener applyEditListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            String name = ((EditText) findViewById(R.id.item_name)).getText()
                    .toString();
            String manufacturer = ((EditText) findViewById(R.id.item_manufacturer))
                    .getText().toString();
            String itemtype = ((EditText) findViewById(R.id.item_itemtype))
                    .getText().toString();
            String location = ((EditText) findViewById(R.id.item_location))
                    .getText().toString();
            String notes = ((EditText) findViewById(R.id.item_notes)).getText()
                    .toString();
            String purchaseDate = ((Button) findViewById(R.id.item_purchasedate))
                    .getText().toString();
            String vendor = ((EditText) findViewById(R.id.item_vendor))
                    .getText().toString();

            ContentValues values = new ContentValues();

            values.put(GaranbotDBMetaData.NAME, name);
            values.put(GaranbotDBMetaData.MANUFACTURER, manufacturer);
            values.put(GaranbotDBMetaData.ITEMTYPE, itemtype);
            values.put(GaranbotDBMetaData.LOCATION, location);
            values.put(GaranbotDBMetaData.NOTES, notes);
            values.put(GaranbotDBMetaData.PURCHASEDATE, purchaseDate);
            values.put(GaranbotDBMetaData.VENDOR, vendor);

            Uri uri = getContentResolver().insert(
                    GaranboItemsProvider.CONTENT_URI, values);
            finish();
        }
    };

    /**
     * OnClickListener to cancel the item edit/creation.
     */
    private final OnClickListener cancelEditListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub

        }
    };

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
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDisplay();
        }
    };

    /**
     * 
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_item);

        // capture our View elements
        applyEditButton = (Button) findViewById(R.id.apply_button);
        cancelEditButton = (Button) findViewById(R.id.cancel_button);
        mPickDate = (Button) findViewById(R.id.item_purchasedate);

        // add a click listeners
        applyEditButton.setOnClickListener(applyEditListener);
        cancelEditButton.setOnClickListener(cancelEditListener);
        mPickDate.setOnClickListener(datePickListener);

        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // display the current date
        updateDisplay();

    }

    /**
     * Updates the date on the date picker button.
     */
    private void updateDisplay() {
        mPickDate.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(mMonth + 1).append("-").append(mDay).append("-")
                .append(mYear).append(" "));
    }

    /**
     * Displays the date picker dialog.
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
                    mDay);
        }
        return null;
    }

}
