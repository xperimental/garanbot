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

import java.io.File;
import java.io.IOException;

import net.sourcewalker.garanbot.R;
import net.sourcewalker.garanbot.api.ClientException;
import net.sourcewalker.garanbot.api.GaranboClient;
import net.sourcewalker.garanbot.api.Item;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * This service downloads item images asynchronously and stores them in the
 * {@link ImageCache}. The content resolver is notified once the download is
 * finished, so that the GUI is updated.
 * 
 * @author Xperimental
 */
public class ImageDownloadService extends IntentService {

    private static final String TAG = "ImageDownloadService";

    private AccountManager accountManager;

    /**
     * Starts the download of an image.
     * 
     * @param context
     *            Context to start service from.
     * @param itemId
     *            ID of item to download image for.
     */
    public static void downloadImage(Context context, long itemId) {
        Intent service = new Intent(context, ImageDownloadService.class);
        service.setAction(Long.toString(itemId));
        context.startService(service);
    }

    public ImageDownloadService() {
        super("ImageDownloadThread");
    }

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();

        accountManager = AccountManager.get(this);
    }

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        int itemId = Integer.parseInt(intent.getAction());
        File cacheFile = ImageCache.getFile(this, itemId);
        if (cacheFile.exists()) {
            Log.d(TAG, "Duplicate download for: " + itemId);
        } else {
            // Get item
            Cursor cursor = getContentResolver().query(
                    ContentUris.withAppendedId(
                            GaranboItemsProvider.CONTENT_URI_ITEMS, itemId),
                    null, null, null, null);
            try {
                if (cursor.moveToFirst()) {
                    final Item item = Item.fromCursor(cursor);
                    int serverId = item.getServerId();
                    if (serverId == Item.UNKNOWN_ID) {
                        Log.d(TAG,
                                "Item not on server, so no picture available: "
                                        + itemId);
                    } else {
                        Account[] accounts = accountManager
                                .getAccountsByType(getString(R.string.account_type));
                        if (accounts.length == 0) {
                            Log.e(TAG, "No account to download from!");
                        } else {
                            String username = accounts[0].name;
                            String password = accountManager
                                    .getPassword(accounts[0]);
                            GaranboClient client = new GaranboClient(username,
                                    password);
                            Bitmap result = client.item().getPicture(serverId);
                            if (result == null) {
                                Log.d(TAG, String.format(
                                        "No picture on server for: %d (%d)",
                                        serverId, itemId));
                            } else {
                                ImageCache.saveImage(this, itemId, result);
                                getContentResolver()
                                        .notifyChange(
                                                ContentUris.withAppendedId(
                                                        GaranboItemsProvider.CONTENT_URI_ITEMS,
                                                        itemId), null);
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "Item not found in database: " + itemId);
                }
            } catch (ClientException e) {
                Log.e(TAG, "Error communicating with server: " + e);
            } catch (IOException e) {
                Log.e(TAG, "Error saving picture: " + e);
            } finally {
                cursor.close();
            }
        }
    }

}
