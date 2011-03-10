package net.sourcewalker.garanbot.data;

import android.provider.BaseColumns;

public final class GaranbotDBMetaData implements BaseColumns {

    public static final String TABLE_NAME = "items";

    public static final String DEFAULT_SORT_ORDER = _ID + " ASC";
    public static final String NAME = "name";
    public static final String MANUFACTURER = "manufacturer";
    public static final String ITEMTYPE = "itemtype";
    public static final String VENDOR = "vendor";
    public static final String LOCATION = "location";
    public static final String NOTES = "notes";
    public static final String HASPICTURE = "haspicture";
    public static final String VISIBILITY = "visibility";
    public static final String PURCHASEDATE = "purchasedate";
    public static final String ENDOFWARRANTY = "endofwarranty";
    public static final String LAST_MODIFIED = "modified";
    public static final String SERVER_ID = "serverId";
    public static final String DELETED = "deleted";

    /**
     * This column is not actually stored in the database but instead only
     * provided by the {@link GaranboItemsProvider}. Therefore it is also not
     * included in the {@link #DEFAULT_PROJECTION}.
     */
    public static final String IMAGE_URI = "image";

    static final String SCHEMA = "CREATE TABLE " + TABLE_NAME + "(" + _ID
            + " integer primary key, " + NAME + " TEXT, " + MANUFACTURER
            + " TEXT, " + ITEMTYPE + " TEXT, " + VENDOR + " TEXT, " + LOCATION
            + " TEXT, " + NOTES + " TEXT, " + HASPICTURE + " INTEGER, "
            + VISIBILITY + " INTEGER, " + PURCHASEDATE + " TEXT, "
            + ENDOFWARRANTY + " TEXT, " + LAST_MODIFIED + " TEXT, " + SERVER_ID
            + " INTEGER, " + DELETED + " INTEGER);";

    public static final String[] DEFAULT_PROJECTION = new String[] { _ID, NAME,
            MANUFACTURER, ITEMTYPE, VENDOR, LOCATION, NOTES, HASPICTURE,
            VISIBILITY, PURCHASEDATE, ENDOFWARRANTY, LAST_MODIFIED, SERVER_ID,
            DELETED };

    public static final String AUTHORITY = "net.sourcewalker.garanbot";

}
