package net.sourcewalker.garanbot.api;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

public class UserService {

    private final GaranboClient client;

    UserService(final GaranboClient client) {
        this.client = client;
    }

    public User get() throws ClientException {
        try {
            final HttpResponse response = client.get("/user");
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                final String content = client.readEntity(response);
                return User.fromJSON(content);
            } else {
                throw new ClientException("Got HTTP error: "
                        + response.getStatusLine().toString());
            }
        } catch (IOException e) {
            throw new ClientException("IO error: " + e.getMessage(), e);
        }
    }

    public boolean create(final User user, final String password)
            throws ClientException {
        try {
            final String jsonData = user.json(password);
            final HttpResponse response = client.put("/user", jsonData);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                return true;
            } else {
                throw new ClientException("Got HTTP status: "
                        + response.getStatusLine());
            }
        } catch (IOException e) {
            throw new ClientException("IO error: " + e.getMessage(), e);
        }
    }
}
