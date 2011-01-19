package net.sourcewalker.garanbot.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;

import android.util.Log;

public class ItemService {

    private final GaranboClient client;

    ItemService(GaranboClient client) {
        this.client = client;
    }

    public List<Integer> list() {
        try {
            HttpResponse response = client.get("/item");
            String content = client.readEntity(response);
            Log.d("ItemService", content);
        } catch (IOException e) {

        }
        return new ArrayList<Integer>();
    }

    public Item get(int id) {
        return null;
    }

}
