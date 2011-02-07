package net.sourcewalker.garanbot.data;

import net.sourcewalker.garanbot.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Easy access to user preferences saved in the default shared preferences for
 * this application.
 * 
 * @author Xperimental
 */
public class Prefs {

    private final SharedPreferences preferences;
    private final String usernameKey;
    private final String usernameDefault;
    private final String passwordKey;
    private final String passwordDefault;

    public Prefs(Context context) {
        this.preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        this.usernameKey = context.getString(R.string.prefs_username_key);
        this.usernameDefault = context
                .getString(R.string.prefs_username_default);
        this.passwordKey = context.getString(R.string.prefs_password_key);
        this.passwordDefault = context
                .getString(R.string.prefs_password_default);
    }

    public String getUsername() {
        return preferences.getString(usernameKey, usernameDefault);
    }

    public void setUsername(String newValue) {
        setString(usernameKey, newValue);
    }

    private void setString(String key, String value) {
        Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPassword() {
        return preferences.getString(passwordKey, passwordDefault);
    }

    public void setPassword(String newValue) {
        setString(passwordKey, newValue);
    }
}
