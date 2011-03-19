package net.sourcewalker.garanbot.api;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.util.Log;

public class GaranboClient {

    private static final String TAG = "GaranboClient";

    private final String username;
    private final String password;
    private final DefaultHttpClient client;
    private final UserService userService;
    private final ItemService itemService;

    public GaranboClient(String username, String password) {
        this.username = username;
        this.password = password;

        client = new DefaultHttpClient();

        userService = new UserService(this);
        itemService = new ItemService(this);
    }

    /**
     * Returns true, if the service is available.
     * 
     * @return True, if service is available.
     */
    public boolean ping() {
        try {
            HttpResponse response = get("/ping");
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return true;
            }
        } catch (IOException e) {
        } catch (AuthenticationException e) {
            // Ping should never give an authentication error!
            Log.e(TAG, "Authentication error during ping!");
        }
        return false;
    }

    public UserService user() {
        return userService;
    }

    public ItemService item() {
        return itemService;
    }

    protected HttpResponse get(String path) throws IOException,
            AuthenticationException {
        return get(path, new Header[0]);
    }

    protected HttpResponse get(String path, Header[] additionalHeaders)
            throws IOException, AuthenticationException {
        HttpGet request = new HttpGet(ApiConstants.BASE + path);
        prepareRequest(request, additionalHeaders);
        HttpResponse response = client.execute(request);
        checkAuthenticationError(response);
        return response;
    }

    /**
     * Check if the response contains an authentication error and throw
     * exception if it does.
     * 
     * @param response
     *            Response to check.
     * @throws AuthenticationException
     *             If response contains authentication error.
     */
    private void checkAuthenticationError(HttpResponse response)
            throws AuthenticationException {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
            throw new AuthenticationException();
        }
    }

    private void prepareRequest(HttpRequestBase request) {
        prepareRequest(request, new Header[0]);
    }

    private void prepareRequest(HttpRequestBase request,
            Header[] additionalHeaders) {
        request.addHeader("Content-type", "application/json");
        request.addHeader("open-api", ApiConstants.KEY);
        request.addHeader("Authorization", getAuthHeader());
        for (Header h : additionalHeaders) {
            request.addHeader(h);
        }
    }

    private String getAuthHeader() {
        byte[] input = String.format("%s:%s", username, password).getBytes();
        return "Basic " + Base64.encodeToString(input, Base64.NO_WRAP);
    }

    protected String readEntity(HttpResponse response) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream stream = response.getEntity().getContent();
            byte[] buffer = new byte[1024];
            int read;
            do {
                read = stream.read(buffer);
                if (read != -1) {
                    if (read == buffer.length) {
                        sb.append(EncodingUtils.getString(buffer, "utf-8"));
                    } else {
                        sb.append(EncodingUtils.getString(buffer, 0, read,
                                "utf-8"));
                    }
                }
            } while (read != -1);
        } catch (IOException e) {
            sb.append("ERROR: " + e.getMessage());
        }
        return sb.toString();
    }

    /**
     * Delete a server entity.
     * 
     * @param path
     *            URL of entity relative to base URL.
     * @return HTTP Response returned by server.
     * @throws IOException
     *             When there was an error communicating with the server.
     */
    protected HttpResponse delete(String path) throws IOException {
        HttpDelete request = new HttpDelete(ApiConstants.BASE + path);
        prepareRequest(request);
        return client.execute(request);
    }

    /**
     * Execute HTTP PUT with URL and provided content.
     * 
     * @param path
     *            Path to PUT to.
     * @param jsonData
     *            String with content to send.
     * @return HTTP Response return by server.
     * @throws IOException
     *             When there was an error communicating with the server.
     */
    public HttpResponse put(String path, String jsonData) throws IOException {
        HttpPut request = new HttpPut(ApiConstants.BASE + path);
        request.setEntity(new StringEntity(jsonData));
        prepareRequest(request);
        return client.execute(request);
    }

    /**
     * Search for product information using an EAN code.
     * 
     * @param eanCode
     *            EAN Code as String
     */
    public BarcodeResult searchEan(final String eanCode) throws ClientException {
        BarcodeResult result = null;
        try {
            HttpResponse response = get("/ean/" + eanCode);
            final int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
            case HttpStatus.SC_OK:
                String content = readEntity(response);
                JSONObject resultJson = new JSONObject(content)
                        .optJSONObject("eanResultList");
                if (resultJson != null) {
                    result = new BarcodeResult(resultJson);
                }
                break;
            case HttpStatus.SC_NOT_FOUND:
                break;
            default:
                throw new ClientException("Got HTTP status: "
                        + response.getStatusLine());
            }
        } catch (IOException e) {
            throw new ClientException("Error during communication: "
                    + e.getMessage(), e);
        } catch (JSONException e) {
            throw new ClientException(
                    "Error parsing result: " + e.getMessage(), e);
        }
        return result;
    }

}
