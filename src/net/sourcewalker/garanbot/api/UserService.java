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

    public boolean create(final RegisterUser user) throws ClientException {
        try {
            final String jsonData = user.json();
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
