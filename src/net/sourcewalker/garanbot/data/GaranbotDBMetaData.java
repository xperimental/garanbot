//
// Copyright 2011 Thomas Gumprecht, Robert Jacob, Thomas Pieronczyk
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package net.sourcewalker.garanbot.data;

import android.provider.BaseColumns;

public final class GaranbotDBMetaData implements BaseColumns {

    /**
     * Class contains only constants.
     */
    private GaranbotDBMetaData() {
    }

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

    /**
     * This column contains the date the item was last modified <b>on the
     * server</b>. It stays unchanged if the item is modified on the device. Use
     * {@link #LOCAL_STATE} for this purpose.
     */
    public static final String LAST_MODIFIED = "modified";
    public static final String SERVER_ID = "serverId";
    public static final String LOCAL_STATE = "localState";

    /**
     * This column is not actually stored in the database but instead only
     * provided by the {@link GaranboItemsProvider}. Therefore it is also not
     * included in the {@link #DEFAULT_PROJECTION}.
     */
    public static final String IMAGE_URI = "image";

    public static final String SCHEMA = "CREATE TABLE " + TABLE_NAME + "("
            + _ID + " integer primary key, " + NAME + " TEXT, " + MANUFACTURER
            + " TEXT, " + ITEMTYPE + " TEXT, " + VENDOR + " TEXT, " + LOCATION
            + " TEXT, " + NOTES + " TEXT, " + HASPICTURE + " INTEGER, "
            + VISIBILITY + " INTEGER, " + PURCHASEDATE + " TEXT, "
            + ENDOFWARRANTY + " TEXT, " + LAST_MODIFIED + " TEXT, " + SERVER_ID
            + " INTEGER, " + LOCAL_STATE + " INTEGER);";

    public static final String[] DEFAULT_PROJECTION = new String[] { _ID, NAME,
            MANUFACTURER, ITEMTYPE, VENDOR, LOCATION, NOTES, HASPICTURE,
            VISIBILITY, PURCHASEDATE, ENDOFWARRANTY, LAST_MODIFIED, SERVER_ID,
            LOCAL_STATE };

    public static final String AUTHORITY = "net.sourcewalker.garanbot";

}
