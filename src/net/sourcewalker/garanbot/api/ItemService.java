package net.sourcewalker.garanbot.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ItemService {

    private final GaranboClient client;

    ItemService(GaranboClient client) {
        this.client = client;
    }

    public List<Integer> list() throws ClientException {
        List<Integer> result = new ArrayList<Integer>();
        try {
            HttpResponse response = client.get("/item");
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String content = client.readEntity(response);
                JSONObject jsonContent = (JSONObject) new JSONTokener(content)
                        .nextValue();
                JSONArray refArray = jsonContent.getJSONArray("ref");
                for (int i = 0; i < refArray.length(); i++) {
                    String refString = refArray.getString(i);
                    int refId = Integer.parseInt(refString.split("/")[1]);
                    result.add(refId);
                }
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

    public Item get(int id) {
        return null;
    }

}
