package net.sourcewalker.garanbot.account;

import net.sourcewalker.garanbot.R;
import android.app.Activity;
import android.os.Bundle;

/**
 * This activity enables the user to create a new Garanbo account in case he
 * does not have one yet.
 * 
 * @author Xperimental
 */
public class RegisterActivity extends Activity {

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

}
