package net.sourcewalker.garanbot.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GaranbotDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "GaranbotDBHelper";
    private static final String DATABASE_NAME = "garanbot.db";
    private static final int DATABASE_VERSION = 9;

    private Context context;

    public GaranbotDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GaranbotDBMetaData.SCHEMA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database, which will destroy all old data.");
        db.execSQL("DROP TABLE IF EXISTS " + GaranbotDBMetaData.TABLE_NAME);
        ImageCache.clearCache(context);
        onCreate(db);
    }

}
