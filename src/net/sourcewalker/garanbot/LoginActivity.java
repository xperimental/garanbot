package net.sourcewalker.garanbot;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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

    private Button loginButton;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.loginbutton);
        loginButton.setOnClickListener(new LoginListener());
    }

    public class LoginListener implements OnClickListener {

        /*
         * (non-Javadoc)
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(View v) {
            // TODO Implement credential check
            startActivity(new Intent(LoginActivity.this, ItemListActivity.class));
        }

    }

}
