package net.sourcewalker.garanbot.account;

import net.sourcewalker.garanbot.R;
import net.sourcewalker.garanbot.api.AuthenticationException;
import net.sourcewalker.garanbot.api.ClientException;
import net.sourcewalker.garanbot.api.GaranboClient;
import net.sourcewalker.garanbot.api.User;
import net.sourcewalker.garanbot.data.GaranbotDBMetaData;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
public class LoginActivity extends AccountAuthenticatorActivity implements
        OnClickListener {

    public static final String ACTION_ERROR = LoginActivity.class.getName()
            + ".ERROR";

    public static final String TAG = "LoginActivity";

    private String accountType;
    private AccountManager accountManager;
    private Button loginButton;
    private Button cancelButton;
    private EditText usernameField;
    private EditText passwordField;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);

        accountType = getString(R.string.account_type);
        accountManager = AccountManager.get(this);

        loginButton = (Button) findViewById(R.id.login_ok);
        loginButton.setOnClickListener(this);
        cancelButton = (Button) findViewById(R.id.login_cancel);
        cancelButton.setOnClickListener(this);
        usernameField = (EditText) findViewById(R.id.login_username);
        passwordField = (EditText) findViewById(R.id.login_password);

        if (ACTION_ERROR.equals(getIntent().getAction())) {
            final Bundle extras = getIntent().getExtras();
            if (extras != null) {
                final String message = extras
                        .getString(AccountManager.KEY_ERROR_MESSAGE);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }

    /**
     * Enable or disable interactive GUI elements in activity.
     * 
     * @param enabled
     *            If true, elements will be enabled.
     */
    protected void enableGui(final boolean enabled) {
        loginButton.setEnabled(enabled);
        cancelButton.setEnabled(enabled);
        usernameField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
    }

    /**
     * Create the account in the account manager.
     * 
     * @param username
     *            Username of new account.
     * @param password
     *            Password of new account.
     */
    public void createAccount(final String username, final String password) {
        final Account account = new Account(username, accountType);
        final boolean created = accountManager.addAccountExplicitly(account,
                password, null);
        if (created) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, username);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
            setAccountAuthenticatorResult(result);

            ContentResolver.setSyncAutomatically(account,
                    GaranbotDBMetaData.AUTHORITY, true);
            finish();
        } else {
            Toast.makeText(this, R.string.toast_account_createfailed,
                    Toast.LENGTH_LONG).show();
        }
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    public void onClick(final View v) {
        switch (v.getId()) {
        case R.id.login_ok:
            final String username = usernameField.getText().toString();
            final String password = passwordField.getText().toString();

            final CredentialsTestTask task = new CredentialsTestTask();
            task.execute(username, password);
            break;
        case R.id.login_cancel:
            finish();
            break;
        default:
            throw new IllegalArgumentException("Unknown view clicked: " + v);
        }
    }

    private class LoginResult {

        public String username;
        public String password;
        public boolean successful;

        public LoginResult(final String username, final String password,
                final boolean result) {
            this.username = username;
            this.password = password;
            this.successful = result;
        }
    }

    private class CredentialsTestTask extends
            AsyncTask<String, Void, LoginResult> {

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
        protected LoginResult doInBackground(final String... params) {
            final String username = params[0];
            final String password = params[1];
            final GaranboClient client = new GaranboClient(username, password);
            Boolean result;
            try {
                final User serverUser = client.user().get();
                result = username.equalsIgnoreCase(serverUser.getUsername());
            } catch (final AuthenticationException e) {
                // Wrong password etc.
                result = false;
            } catch (final ClientException e) {
                // Other reason for failing (e.g. network).
                Log.e(TAG, "Error while logging in: " + e.getMessage());
                result = false;
            }
            return new LoginResult(username, password, result);
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(final LoginResult result) {
            setProgressBarIndeterminateVisibility(false);
            enableGui(true);

            if (result.successful) {
                createAccount(result.username, result.password);
            } else {
                Toast.makeText(LoginActivity.this, R.string.toast_loginfailed,
                        Toast.LENGTH_LONG).show();
            }
        }

    }

}
