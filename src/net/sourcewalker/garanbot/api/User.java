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

    private String username;
    private String name;
    private String email;

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    /**
     * User objects can only be created from a JSON source.
     */
    private User() {
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
    public static User fromJSON(String content) throws ClientException {
        try {
            JSONObject object = (JSONObject) new JSONTokener(content)
                    .nextValue();
            User result = new User();
            result.setUsername(object.getString("username"));
            result.setName(object.getString("name"));
            result.setEmail(object.getString("email"));
            return result;
        } catch (JSONException e) {
            throw new ClientException("Error parsing User: " + e.getMessage(),
                    e);
        }
    }

}
