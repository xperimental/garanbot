package net.sourcewalker.garanbot;

import net.sourcewalker.garanbot.data.GaranbotDBHelper;
import net.sourcewalker.garanbot.data.GaranbotDBMetaData;
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
        db
                .execSQL("INSERT INTO "
                        + GaranbotDBMetaData.TABLE_NAME
                        + " (name,manufacturer,itemtype,vendor,location,notes,haspicture,purchasedate,endofwarranty) "
                        + "VALUES ('firstItem','matchbox','toy','schinacher','fn','green car', 0,'2002-12-24','2004-12-24')");

        // query entries for display in list view
        Cursor c = db.query(GaranbotDBMetaData.TABLE_NAME, new String[] {
                "_id", "name" }, null, null, null, null, null);
        startManagingCursor(c);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item, c, new String[] { "_id",
                        "name" }, new int[] { android.R.id.text1,
                        android.R.id.text2 });

        this.setListAdapter(adapter);

    }
}
