package net.sourcewalker.garanbot;

import net.sourcewalker.garanbot.data.GaranbotDBHelper;
import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class GaranbotItems extends ListActivity {

    private GaranbotDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new GaranbotDBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // insert two entries in table
        db.execSQL("INSERT INTO " + GaranbotDBHelper.TABLE_NAME + " (name) "
                + "VALUES ('firstItem')");
        db.execSQL("INSERT INTO " + GaranbotDBHelper.TABLE_NAME + " (name) "
                + "VALUES ('secondItem')");

        // query entries for display in list view
        Cursor c = db.query(GaranbotDBHelper.TABLE_NAME, new String[] { "_id",
                "name" }, null, null, null, null, null);
        startManagingCursor(c);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item, c, new String[] { "_id",
                        "name" }, new int[] { android.R.id.text1,
                        android.R.id.text2 });

        this.setListAdapter(adapter);

    }

}
