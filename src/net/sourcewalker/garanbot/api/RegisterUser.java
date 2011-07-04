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

/**
 * This is a specialized version of the {@link User} class for registering new
 * users. This class contains an additional password field and setters for all
 * all fields but can not be constructed from a JSON source.
 * 
 * @author Xperimental
 */
public class RegisterUser extends User {

    private String password;

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Create JSON representation of User object.
     * 
     * @param password
     *            Password to use for serialized object. <code>null</code> if no
     *            password should be set.
     * @return User object serialized as JSON String.
     * @throws ClientException
     *             When serialization fails.
     */
    @Override
    public String json() throws ClientException {
        final JSONObject result = new JSONObject();
        try {
            result.put("username", getUsername());
            result.put("name", getName());
            result.put("email", getEmail());
            result.put("password", getPassword());
        } catch (final JSONException e) {
            throw new ClientException("Error generating JSON for User: "
                    + e.getMessage(), e);
        }
        return result.toString();
    }

}
