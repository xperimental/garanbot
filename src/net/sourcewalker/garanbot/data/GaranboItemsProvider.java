package net.sourcewalker.garanbot.data;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * This content provider communicates with the local database, which stores the
 * garanbo items.
 * 
 * @author pieronczyk
 */
public class GaranboItemsProvider extends ContentProvider {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + GaranbotDBMetaData.AUTHORITY + "/items");

    private static UriMatcher matcher;
    private GaranbotDBHelper dbHelper;
    private static final String TAG = "GaranbotProvider";
    private static final HashMap<String, String> projectionMap;
    private static final int MATCH_LIST = 1;

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(GaranbotDBMetaData.AUTHORITY, "items", MATCH_LIST);

        projectionMap = new HashMap<String, String>();
        for (String col : GaranbotDBMetaData.DEFAULT_PROJECTION) {
            projectionMap.put(col, col);
        }
    }

    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getType(Uri arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri arg0, ContentValues arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new GaranbotDBHelper(getContext());
        return true;
    }

    /**
     * Queries the database for garanbo items and returns a cursor object which
     * points to the requested data.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query");
        // Build basic sql query
        SQLiteQueryBuilder query = new SQLiteQueryBuilder();
        query.setTables(GaranbotDBMetaData.TABLE_NAME);
        query.setProjectionMap(projectionMap);
        if (sortOrder == null) {
            sortOrder = GaranbotDBMetaData.DEFAULT_SORT_ORDER;
        }
        if (matcher.match(uri) != MATCH_LIST) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = query.query(db, projection, selection, selectionArgs, null,
                null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
        // TODO Auto-generated method stub
        return 0;
    }

}
