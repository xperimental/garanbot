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
package net.sourcewalker.garanbot.data.sync;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourcewalker.garanbot.api.AuthenticationException;
import net.sourcewalker.garanbot.api.ClientException;
import net.sourcewalker.garanbot.api.GaranboClient;
import net.sourcewalker.garanbot.api.Item;
import net.sourcewalker.garanbot.data.GaranboItemsProvider;
import net.sourcewalker.garanbot.data.GaranbotDBMetaData;
import net.sourcewalker.garanbot.data.ImageCache;
import net.sourcewalker.garanbot.data.LocalState;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class provides the sync adapter used to merge the local database with
 * the Garanbo server.
 * 
 * @author Xperimental
 */
public class GaranboSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String BROADCAST_ACTION = "net.sourcewalker.garanbot.broadcast.sync";
    public static final String EXTRA_RUNNING = "running";

    private static final String TAG = "GaranboSyncAdapter";

    private final AccountManager accountManager;

    public GaranboSyncAdapter(Context context) {
        super(context, true);

        this.accountManager = AccountManager.get(context);
    }

    /*
     * (non-Javadoc)
     * @see
     * android.content.AbstractThreadedSyncAdapter#onPerformSync(android.accounts
     * .Account, android.os.Bundle, java.lang.String,
     * android.content.ContentProviderClient, android.content.SyncResult)
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
            ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "Sync started.");
        sendStatusBroadcast(true);
        String username = account.name;
        String password = accountManager.getPassword(account);
        GaranboClient client = new GaranboClient(username, password);
        try {
            List<Integer> idList = client.item().list();
            if (getDbCount(provider) == 0) {
                // First time synchronization, just save all items from server
                Log.d(TAG, "Nothing in DB. Simple copy...");
                for (Integer id : idList) {
                    Item item = client.item().get(id);
                    provider.insert(GaranboItemsProvider.CONTENT_URI_ITEMS,
                            item.toContentValues());
                    Log.d(TAG, "Saved item: " + id);
                    syncResult.stats.numInserts++;
                }
            } else if (idList.size() == 0) {
                // Other simple solution (copy all to server)
                Log.d(TAG, "Only local content. Copy to server...");
                List<Item> localItems = getAllDbItems(provider);
                for (Item item : localItems) {
                    item.setServerId(Item.UNKNOWN_ID);
                    int id = client.item().create(item);
                    item.setServerId(id);
                    provider.update(ContentUris.withAppendedId(
                            GaranboItemsProvider.CONTENT_URI_ITEMS,
                            item.getLocalId()), item.toContentValues(), null,
                            null);
                    Log.d(TAG, "Uploaded item: " + id);
                    syncResult.stats.numInserts++;
                }
            } else {
                // More complicated sync...
                Log.d(TAG, "Both have content: have to sync...");
                Map<Integer, List<Integer>> localIdMap = getLocalIdMap(provider);
                synchronizeItems(client, provider, idList, localIdMap,
                        syncResult);
            }
        } catch (AuthenticationException e) {
            Log.e(TAG, "Authentication error.");
            syncResult.stats.numAuthExceptions++;
        } catch (ClientException e) {
            Log.e(TAG, "Client exception: " + e);
            syncResult.stats.numIoExceptions++;
        } catch (RemoteException e) {
            Log.e(TAG, "DB exception: " + e);
            syncResult.stats.numIoExceptions++;
        }
        Log.d(TAG, "Sync ended.");
        sendStatusBroadcast(false);
    }

    private void sendStatusBroadcast(boolean running) {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(EXTRA_RUNNING, running);
        getContext().sendBroadcast(intent);
    }

    /**
     * Do a bi-directional sync with the server.
     * 
     * @param client
     *            Client object for communicating with the server.
     * @param provider
     *            Content provider to use.
     * @param idList
     *            List of items on server.
     * @param localIdMap
     *            For mapping server IDs to local IDs.
     * @param syncResult
     *            Used for synchronization statistics.
     * @throws ClientException
     *             If communication with the server fails.
     * @throws RemoteException
     *             If communication with the database fails.
     */
    private void synchronizeItems(GaranboClient client,
            ContentProviderClient provider, List<Integer> idList,
            Map<Integer, List<Integer>> localIdMap, SyncResult syncResult)
            throws RemoteException, ClientException {
        if (localIdMap.containsKey(Item.UNKNOWN_ID)) {
            // First upload all new items...
            Log.d(TAG, "Sync: local-only items...");
            List<Integer> newItems = localIdMap.remove(Item.UNKNOWN_ID);
            for (Integer id : newItems) {
                Item localItem = getLocalItem(provider, id);
                int serverId = client.item().create(localItem);
                localItem.setServerId(serverId);
                localItem.setLocalState(new LocalState());
                provider.update(ContentUris.withAppendedId(
                        GaranboItemsProvider.CONTENT_URI_ITEMS, id), localItem
                        .toContentValues(), null, null);
                syncResult.stats.numInserts++;
                Log.d(TAG, "Uploaded local-only: " + id + " --> " + serverId);
            }
        }
        for (int serverId : idList) {
            Log.d(TAG, "Sync server item " + serverId);
            if (localIdMap.containsKey(serverId)) {
                // Item exists locally...
                Log.d(TAG, "  Local available.");
                List<Integer> localIdList = localIdMap.remove(serverId);
                if (localIdList.size() != 1) {
                    // Something is wrong...
                    Log.e(TAG, "More than one local item for server ID: "
                            + serverId + " , " + localIdList);
                }
                int localId = localIdList.get(0);
                Item localItem = getLocalItem(provider, localId);
                if (localItem.getLocalState().isDeleted()) {
                    Log.d(TAG, "  Deleted locally.");
                    client.item().delete(serverId);
                    provider.delete(ContentUris.withAppendedId(
                            GaranboItemsProvider.CONTENT_URI_ITEMS, localId),
                            null, null);
                } else {
                    Item serverItem = client.item().getIfNewer(serverId,
                            localItem.getLastModified());
                    if (serverItem == null) {
                        // Server copy not modified
                        Log.d(TAG, "  Server item not modified.");
                        if (localItem.getLocalState().changed()) {
                            // Local copy modified -> upload
                            Log.d(TAG, "  Client item modified -> upload.");
                            if (localItem.getLocalState().pictureChanged()) {
                                uploadItemPicture(client, localItem);
                            }
                            client.item().update(localItem);
                            serverItem = client.item().get(serverId);
                            provider.update(ContentUris.withAppendedId(
                                    GaranboItemsProvider.CONTENT_URI_ITEMS,
                                    localId), serverItem.toContentValues(),
                                    null, null);
                            syncResult.stats.numUpdates++;
                        } else {
                            Log.d(TAG, "  Both versions not modified.");
                        }
                    } else {
                        // Server copy modified
                        Log.d(TAG, "  Server item modified.");
                        if (localItem.getLocalState().changed()) {
                            // Both modified -> conflict (see GBOT-22)
                            Log.w(TAG, "  CONFLICT: Both modified.");
                        } else {
                            // Local not modified -> download
                            Log.d(TAG, "  Server item modified -> download.");
                            provider.update(ContentUris.withAppendedId(
                                    GaranboItemsProvider.CONTENT_URI_ITEMS,
                                    localId), serverItem.toContentValues(),
                                    null, null);
                            ImageCache.deleteImage(getContext(), localId);
                            syncResult.stats.numUpdates++;
                        }
                    }
                }
            } else {
                // Item is new...
                Log.d(TAG, "  No local item. Creating...");
                Item serverItem = client.item().get(serverId);
                Uri itemUri = provider.insert(
                        GaranboItemsProvider.CONTENT_URI_ITEMS,
                        serverItem.toContentValues());
                syncResult.stats.numInserts++;
                Log.d(TAG, "  New local item: " + itemUri);
            }
        }
        // All remaining entries in map can be deleted...
        while (!localIdMap.isEmpty()) {
            Log.d(TAG, "Deleting items not on server anymore...");
            int serverId = localIdMap.keySet().iterator().next();
            List<Integer> localIdList = localIdMap.remove(serverId);
            for (int localId : localIdList) {
                provider.delete(ContentUris.withAppendedId(
                        GaranboItemsProvider.CONTENT_URI_ITEMS, localId), null,
                        null);
                syncResult.stats.numDeletes++;
                Log.d(TAG, "Deleted: " + localId);
            }
        }
    }

    /**
     * Upload a locally saved picture to the server. The picture is read from
     * the local {@link ImageCache}.
     * 
     * @param client
     *            Client object to use for server communication.
     * @param localItem
     *            Item to upload picture for.
     * @throws ClientException
     *             If the upload failed.
     */
    private void uploadItemPicture(GaranboClient client, final Item localItem)
            throws ClientException {
        final int id = localItem.getLocalId();
        File pictureFile = ImageCache.getFile(getContext(), id);
        client.item().uploadPicture(id, pictureFile);
    }

    /**
     * Retrieve an item from the local database.
     * 
     * @param provider
     *            Content provider to query.
     * @param id
     *            Local ID of item.
     * @return Item with specified ID or <code>null</code> if not found.
     * @throws RemoteException
     *             If communication with database fails.
     * @throws ClientException
     *             If the item can not be parsed.
     */
    private Item getLocalItem(ContentProviderClient provider, Integer id)
            throws RemoteException, ClientException {
        Cursor cursor = provider.query(ContentUris.withAppendedId(
                GaranboItemsProvider.CONTENT_URI_ITEMS, id), null, null, null,
                null);
        if (cursor.moveToFirst()) {
            Item result = Item.fromCursor(cursor);
            cursor.close();
            return result;
        } else {
            return null;
        }
    }

    /**
     * Return a map that maps server IDs to local item IDs. The value list
     * should only contain one item for each server ID, unless the key is
     * {@link Item#UNKNOWN_ID}.
     * 
     * @param provider
     *            Content provider to query.
     * @return Map to get local items from server ID.
     * @throws RemoteException
     *             If communication with local database fails.
     */
    private Map<Integer, List<Integer>> getLocalIdMap(
            ContentProviderClient provider) throws RemoteException {
        Map<Integer, List<Integer>> idMap = new HashMap<Integer, List<Integer>>();
        Cursor cursor = provider.query(
                GaranboItemsProvider.CONTENT_URI_ITEMS_ALL, new String[] {
                        GaranbotDBMetaData._ID, GaranbotDBMetaData.SERVER_ID },
                null, null, null);
        while (cursor.moveToNext()) {
            int localId = cursor.getInt(0);
            int serverId = cursor.getInt(1);
            if (!idMap.containsKey(serverId)) {
                idMap.put(serverId, new ArrayList<Integer>());
            }
            idMap.get(serverId).add(localId);
        }
        cursor.close();
        return idMap;
    }

    /**
     * Get a list of all database items.
     * 
     * @param provider
     *            Content provider to query.
     * @return List of all items in database.
     * @throws RemoteException
     *             If there is an error communicating with the database.
     * @throws ClientException
     *             If an item cannot be parsed.
     */
    private List<Item> getAllDbItems(ContentProviderClient provider)
            throws RemoteException, ClientException {
        List<Item> result = new ArrayList<Item>();
        Cursor query = provider.query(
                GaranboItemsProvider.CONTENT_URI_ITEMS_ALL,
                GaranbotDBMetaData.DEFAULT_PROJECTION, null, null, null);
        while (query.moveToNext()) {
            Item item = Item.fromCursor(query);
            result.add(item);
        }
        query.close();
        return result;
    }

    /**
     * Returns the number of items in the local database.
     * 
     * @param provider
     *            Content provider to query.
     * @return Number of items in local database.
     * @throws RemoteException
     *             If there is an error communicating with the database.
     */
    private int getDbCount(ContentProviderClient provider)
            throws RemoteException {
        Cursor query = provider.query(
                GaranboItemsProvider.CONTENT_URI_ITEMS_ALL,
                new String[] { GaranbotDBMetaData._ID }, null, null, null);
        int result = query.getCount();
        query.close();
        return result;
    }
}
