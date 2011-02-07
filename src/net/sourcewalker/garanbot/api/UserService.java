package net.sourcewalker.garanbot.api;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

public class UserService {

    private final GaranboClient client;

    UserService(GaranboClient client) {
        this.client = client;
    }

    public User get() throws ClientException {
        try {
            HttpResponse response = client.get("/user");
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String content = client.readEntity(response);
                return User.fromJSON(content);
            } else {
                throw new ClientException("Got HTTP error: "
                        + response.getStatusLine().toString());
            }
        } catch (IOException e) {
            throw new ClientException("IO error: " + e.getMessage(), e);
        }
    }

}
