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
package net.sourcewalker.garanbot.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Base64InputStream;

public class ItemService {

    private static final String LAST_MODIFIED_HEADER = "Last-Modified";

    private final GaranboClient client;

    ItemService(GaranboClient client) {
        this.client = client;
    }

    public List<Integer> list() throws ClientException {
        List<Integer> result = new ArrayList<Integer>();
        try {
            HttpResponse response = client.get("/item");
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                String content = client.readEntity(response);
                JSONObject jsonContent = (JSONObject) new JSONTokener(content)
                        .nextValue();
                JSONArray refArray = jsonContent.optJSONArray("ref");
                if (refArray == null) {
                    String ref = jsonContent.optString("ref");
                    if (ref == null) {
                        throw new ClientException(
                                "No valid ref element found: " + content);
                    } else {
                        result.add(Integer.parseInt(ref.split("/")[1]));
                    }
                } else {
                    for (int i = 0; i < refArray.length(); i++) {
                        String refString = refArray.getString(i);
                        int refId = Integer.parseInt(refString.split("/")[1]);
                        result.add(refId);
                    }
                }
            } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                // No items on server.
            } else {
                throw new ClientException("Got HTTP error: "
                        + response.getStatusLine().toString());
            }
        } catch (IOException e) {
            throw new ClientException("IO error: " + e.getMessage(), e);
        } catch (JSONException e) {
            throw new ClientException("JSON Parser error: " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            throw new ClientException("Error parsing id: " + e.getMessage(), e);
        }
        return result;
    }

    public Item get(int id) throws ClientException {
        try {
            HttpResponse response = client.get("/item/" + id);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return getItemFromResponse(response);
            } else {
                throw new ClientException("Got HTTP error: "
                        + response.getStatusLine().toString());
            }
        } catch (IOException e) {
            throw new ClientException("IO error: " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            throw new ClientException("Error parsing id: " + e.getMessage(), e);
        }
    }

    /**
     * Create an Item object from a HTTP response.
     * 
     * @param response
     *            Response to parse.
     * @return Item object parsed from response.
     * @throws ClientException
     *             If there was an error parsing the Item.
     */
    private Item getItemFromResponse(final HttpResponse response)
            throws ClientException {
        final String content = client.readEntity(response);
        final Header lastModifiedHeader = response
                .getFirstHeader(LAST_MODIFIED_HEADER);
        return Item.fromJSON(content, lastModifiedHeader.getValue());
    }

    public Item getIfNewer(final int itemId, final Date modifiedDate)
            throws ClientException {
        try {
            Header[] headers = new Header[] { new BasicHeader(
                    "If-Modified-Since",
                    ApiConstants.HTTP_DATE_FORMAT.format(modifiedDate)) };
            HttpResponse response = client.get("/item/" + itemId, headers);
            switch (response.getStatusLine().getStatusCode()) {
            case HttpStatus.SC_OK:
                return getItemFromResponse(response);
            case HttpStatus.SC_NOT_MODIFIED:
                return null;
            default:
                throw new ClientException("Got HTTP status: "
                        + response.getStatusLine().toString());
            }
        } catch (IOException e) {
            throw new ClientException("IO error: " + e.getMessage(), e);
        }
    }

    public Bitmap getPicture(int id) throws ClientException {
        try {
            HttpResponse response = client.get("/item/" + id + "/picture");
            int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
            case HttpStatus.SC_OK:
                Base64InputStream stream = new Base64InputStream(response
                        .getEntity().getContent(), Base64.DEFAULT);
                Bitmap result = BitmapFactory.decodeStream(stream);
                if (result == null) {
                    throw new ClientException("Picture could not be decoded!");
                }
                return result;
            case HttpStatus.SC_NOT_FOUND:
                return null;
            default:
                throw new ClientException("Got HTTP error: "
                        + response.getStatusLine().toString());
            }
        } catch (IOException e) {
            throw new ClientException("IO error: " + e.getMessage(), e);
        }
    }

    public void uploadPicture(final int id, final File picture)
            throws ClientException {
        try {
            final FileInputStream stream = new FileInputStream(picture);
            final HttpResponse response = client.put(
                    "/item/" + id + "/picture", "image/jpeg", stream,
                    picture.length());
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new ClientException("Got HTTP error: "
                        + response.getStatusLine().toString());
            }
        } catch (IOException e) {
            throw new ClientException("IO error: " + e.getMessage(), e);
        }
    }

    public void delete(int id) throws ClientException {
        try {
            HttpResponse response = client.delete("/item/" + id);
            if (!(response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT)) {
                throw new ClientException("Got HTTP error: "
                        + response.getStatusLine().toString());
            }
        } catch (IOException e) {
            throw new ClientException("IO error: " + e.getMessage(), e);
        }
    }

    public int create(Item itemData) throws ClientException {
        try {
            if (itemData.getServerId() != Item.UNKNOWN_ID) {
                throw new ClientException(
                        "Can't create Item which already has an ID!");
            }
            String jsonData = itemData.json().toString();
            HttpResponse response = client.put("/item", jsonData);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                Header locationHeader = response.getFirstHeader("Location");
                Uri itemUri = Uri.parse(locationHeader.getValue());
                return (int) ContentUris.parseId(itemUri);
            } else {
                throw new ClientException("Got HTTP status: "
                        + response.getStatusLine().toString());
            }
        } catch (IOException e) {
            throw new ClientException("IO error: " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            throw new ClientException("Error parsing new item id: "
                    + e.getMessage(), e);
        }
    }

    public void update(Item itemData) throws ClientException {
        try {
            if (itemData.getServerId() == Item.UNKNOWN_ID) {
                throw new ClientException("Can't update Item with no ID!");
            }
            String jsonData = itemData.json().toString();
            HttpResponse response = client.put("/item", jsonData);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
                throw new ClientException("Got HTTP error: "
                        + response.getStatusLine().toString());
            }
        } catch (IOException e) {
            throw new ClientException("IO error: " + e.getMessage(), e);
        }
    }

}
