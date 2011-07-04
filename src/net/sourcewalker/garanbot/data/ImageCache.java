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
import java.io.FileOutputStream;
import java.io.IOException;

import net.sourcewalker.garanbot.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

/**
 * This class provides access to the item image cache on the device SD card.
 * 
 * @author Xperimental
 */
public class ImageCache {

    private static final String TAG = "ImageCache";

    /**
     * Returns the File object for an item's image. Note that the file does not
     * have to exist.
     * 
     * @param context
     *            Context to base path on.
     * @param itemId
     *            ID of item.
     * @return File object for image.
     */
    public static File getFile(Context context, long itemId) {
        return new File(context.getExternalFilesDir(null),
                Long.toString(itemId) + ".jpeg");
    }

    /**
     * Returns the File object for the default image. The default image will be
     * extracted from the resources the first time this method is called.
     * 
     * @param context
     *            Context to base path on.
     * @return File object for default image.
     */
    public static File getDefaultImageFile(Context context) {
        File result = new File(context.getExternalFilesDir(null), "default.png");
        if (!result.exists()) {
            BitmapDrawable drawable = (BitmapDrawable) context.getResources()
                    .getDrawable(R.drawable.item_nopicture);
            Bitmap bitmap = drawable.getBitmap();
            try {
                FileOutputStream stream = new FileOutputStream(result);
                bitmap.compress(CompressFormat.PNG, 100, stream);
                stream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error writing default image: " + e.getMessage());
            }
        }
        return result;
    }

    /**
     * Saves an image on the SD card.
     * 
     * @param context
     *            Context to base path on.
     * @param itemId
     *            ID of item.
     * @param bitmap
     *            Image to save on SD card.
     * @throws IOException
     *             If writing was not successful.
     */
    public static void saveImage(Context context, int itemId, Bitmap bitmap)
            throws IOException {
        File file = getFile(context, itemId);
        FileOutputStream stream = new FileOutputStream(file);
        bitmap.compress(CompressFormat.JPEG, 90, stream);
        stream.close();
    }

    /**
     * Delete an image from the cache.
     * 
     * @param context
     *            Context to base path on.
     * @param itemId
     *            ID of item.
     */
    public static void deleteImage(Context context, int itemId) {
        File file = getFile(context, itemId);
        file.delete();
    }

    /**
     * Clear all images from the cache.
     * 
     * @param context
     *            Context to base path on.
     */
    public static void clearCache(Context context) {
        File filesDir = context.getExternalFilesDir(null);
        for (File image : filesDir.listFiles()) {
            image.delete();
        }
    }

}
