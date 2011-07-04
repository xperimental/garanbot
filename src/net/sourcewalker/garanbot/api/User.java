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

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Contains user information.
 * 
 * @author Xperimental
 */
public class User {

    protected String username;
    protected String name;
    protected String email;

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    /**
     * User objects can only be created from a JSON source.
     */
    protected User() {
    }

    /**
     * Try to parse JSON content to User object.
     * 
     * @param content
     *            String with JSON content.
     * @return User object when successfully parsed.
     * @throws ClientException
     *             If there are parsing errors.
     */
    public static User fromJSON(final String content) throws ClientException {
        try {
            final JSONObject object = (JSONObject) new JSONTokener(content)
                    .nextValue();
            final User result = new User();
            result.username = object.getString("username");
            result.name = object.getString("name");
            result.email = object.getString("email");
            return result;
        } catch (JSONException e) {
            throw new ClientException("Error parsing User: " + e.getMessage(),
                    e);
        }
    }

    /**
     * Create JSON representation of User object.
     * 
     * @return User object serialized as JSON String.
     * @throws ClientException
     *             When serialization fails.
     */
    public String json() throws ClientException {
        final JSONObject result = new JSONObject();
        try {
            result.put("username", getUsername());
            result.put("name", getName());
            result.put("email", getEmail());
        } catch (final JSONException e) {
            throw new ClientException("Error generating JSON for User: "
                    + e.getMessage(), e);
        }
        return result.toString();
    }

}
