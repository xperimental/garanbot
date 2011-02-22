package net.sourcewalker.garanbot.data;

import java.io.File;
import java.io.IOException;

import net.sourcewalker.garanbot.R;
import net.sourcewalker.garanbot.api.ClientException;
import net.sourcewalker.garanbot.api.GaranboClient;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
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
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        int itemId = Integer.parseInt(intent.getAction());
        File cacheFile = ImageCache.getFile(this, itemId);
        if (cacheFile.exists()) {
            Log.d(TAG, "Duplicate download for: " + itemId);
        } else {
            AccountManager accountManager = AccountManager.get(this);
            Account[] accounts = accountManager
                    .getAccountsByType(getString(R.string.account_type));
            if (accounts.length == 0) {
                Log.e(TAG, "No account to download from!");
            } else {
                String username = accounts[0].name;
                String password = accountManager.getPassword(accounts[0]);
                GaranboClient client = new GaranboClient(username, password);
                try {
                    Bitmap result = client.item().getPicture(itemId);
                    ImageCache.saveImage(this, itemId, result);
                    getContentResolver().notifyChange(
                            ContentUris.withAppendedId(
                                    GaranboItemsProvider.CONTENT_URI_ITEMS,
                                    itemId), null);
                } catch (ClientException e) {
                    Log.e(TAG, "Error downloading image: " + e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, "IO Error while saving image: " + e.getMessage());
                }
            }
        }
    }

}
