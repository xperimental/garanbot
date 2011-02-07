package net.sourcewalker.garanbot;

import net.sourcewalker.garanbot.api.ClientException;
import net.sourcewalker.garanbot.api.GaranboClient;
import net.sourcewalker.garanbot.data.Prefs;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This activity presents a simple login screen to the user. When he clicks
 * Login the entered data is validated and if it is correct the next activity is
 * displayed. If the login was successful this activity should not be reachable
 * using the back button. This activity will be launched from the
 * {@link AccountManager} once the account management has been implemented.
 * 
 * @author Xperimental
 */
public class LoginActivity extends Activity {

    private Prefs preferences;
    private Button loginButton;
    private EditText usernameField;
    private EditText passwordField;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);

        preferences = new Prefs(this);

        loginButton = (Button) findViewById(R.id.loginbutton);
        loginButton.setOnClickListener(new LoginListener());

        usernameField = (EditText) findViewById(R.id.username);
        usernameField.setText(preferences.getUsername());

        passwordField = (EditText) findViewById(R.id.password);
        passwordField.setText(preferences.getPassword());
    }

    /**
     * Enable or disable interactive GUI elements in activity.
     * 
     * @param enabled
     *            If true, elements will be enabled.
     */
    protected void enableGui(boolean enabled) {
        loginButton.setEnabled(enabled);
        usernameField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
    }

    public class LoginListener implements OnClickListener {

        /*
         * (non-Javadoc)
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        public void onClick(View v) {
            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();

            CredentialsTestTask task = new CredentialsTestTask();
            task.execute(username, password);
        }
    }

    public class CredentialsTestTask extends AsyncTask<String, Void, Boolean> {

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
            enableGui(false);
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Boolean doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            GaranboClient client = new GaranboClient(username, password);
            Boolean result = false;
            try {
                client.item().list();
                result = true;
            } catch (ClientException e) {
                // Any exception causes login to fail.
            }
            return result;
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Boolean result) {
            setProgressBarIndeterminateVisibility(false);
            enableGui(true);

            if (result) {
                preferences.setUsername(usernameField.getText().toString());
                preferences.setPassword(passwordField.getText().toString());

                startActivity(new Intent(LoginActivity.this,
                        ItemListActivity.class));
            } else {
                Toast.makeText(LoginActivity.this, R.string.toast_loginfailed,
                        Toast.LENGTH_LONG).show();
            }
        }

    }

}
