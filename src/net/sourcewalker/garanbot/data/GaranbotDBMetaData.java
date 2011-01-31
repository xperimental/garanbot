package net.sourcewalker.garanbot.data;

import android.provider.BaseColumns;

public class GaranbotDBMetaData implements BaseColumns {

    public static final String TABLE_NAME = "items";

    public static final String DEFAULT_SORT_ORDER = _ID + " ASC";
    public static final String NAME = "name";
    public static final String MANUFACTURER = "manufacturer";
    public static final String ITEMTYPE = "itemtype";
    public static final String VENDOR = "vendor";
    public static final String LOCATION = "location";
    public static final String NOTES = "notes";
    public static final String HASPICTURE = "haspicture";
    public static final String PURCHASEDATE = "purchasedate";
    public static final String ENDOFWARRANTY = "endofwarranty";

    static final String SCHEMA = "CREATE TABLE " + TABLE_NAME + "(" + _ID
            + " integer primary key, " + NAME + " TEXT, " + MANUFACTURER
            + " TEXT, " + ITEMTYPE + " TEXT, " + VENDOR + " TEXT, " + LOCATION
            + " TEXT, " + NOTES + " TEXT, " + HASPICTURE + " INTEGER, "
            + PURCHASEDATE + " NUMERIC, " + ENDOFWARRANTY + " NUMERIC);";

    public static final String[] DEFAULT_PROJECTION = new String[] { _ID, NAME,
            MANUFACTURER, ITEMTYPE, VENDOR, LOCATION, NOTES, HASPICTURE,
            PURCHASEDATE, ENDOFWARRANTY };

    public static final String AUTHORITY = "net.sourcewalker.garanbot";

}