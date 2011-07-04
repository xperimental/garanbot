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
package net.sourcewalker.garanbot.account;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourcewalker.garanbot.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * This activity enables the user to create a new Garanbo account in case he
 * does not have one yet.
 * 
 * @author Xperimental
 */
public class RegisterActivity extends Activity implements OnClickListener {

    private static final Pattern EMAIL_PATTERN = Pattern
            .compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$");

    private static final int PASS = 0;
    private static final int FAIL_USERNAME = 1;
    private static final int FAIL_PASSWORD = 2;
    private static final int FAIL_PASSWORD_CHECK = 4;
    private static final int FAIL_NAME = 8;
    private static final int FAIL_EMAIL = 16;

    private static final Map<Integer, ErrorMessage> ERROR_MESSAGE_MAP;

    static {
        ERROR_MESSAGE_MAP = new HashMap<Integer, RegisterActivity.ErrorMessage>();
        ERROR_MESSAGE_MAP.put(FAIL_USERNAME, new ErrorMessage(
                R.id.register_username, R.string.register_username_error));
        ERROR_MESSAGE_MAP.put(FAIL_PASSWORD, new ErrorMessage(
                R.id.register_password, R.string.register_password_error));
        ERROR_MESSAGE_MAP.put(FAIL_PASSWORD_CHECK, new ErrorMessage(
                R.id.register_password_check,
                R.string.register_passwordcheck_error));
        ERROR_MESSAGE_MAP.put(FAIL_NAME, new ErrorMessage(R.id.register_name,
                R.string.register_name_error));
        ERROR_MESSAGE_MAP.put(FAIL_EMAIL, new ErrorMessage(R.id.register_email,
                R.string.register_email_error));
    }

    private EditText usernameField;
    private EditText passwordField;
    private EditText passwordCheckField;
    private EditText nameField;
    private EditText emailField;
    private Button cancelButton;
    private Button registerButton;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameField = (EditText) findViewById(R.id.register_username);
        passwordField = (EditText) findViewById(R.id.register_password);
        passwordCheckField = (EditText) findViewById(R.id.register_password_check);
        nameField = (EditText) findViewById(R.id.register_name);
        emailField = (EditText) findViewById(R.id.register_email);

        cancelButton = (Button) findViewById(R.id.register_cancel);
        cancelButton.setOnClickListener(this);
        registerButton = (Button) findViewById(R.id.register_ok);
        registerButton.setOnClickListener(this);

        updateUI(true, PASS);
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
        case R.id.register_cancel:
            setResult(RESULT_CANCELED);
            finish();
            break;
        case R.id.register_ok:
            final int checkResult = checkInput();
            if (checkResult == PASS) {
                startRegisterUser();
            } else {
                updateUI(true, checkResult);
            }
            break;
        default:
            throw new IllegalArgumentException("Unknown view clicked: " + v);
        }
    }

    /**
     * Updates the controls enabled state and color.
     * 
     * @param enabled
     *            True, if user can make inputs.
     * @param checkResult
     *            Bitmask resturned by {@link #checkInput()} for coloring
     *            erroneous fields.
     */
    private void updateUI(final boolean enabled, final int checkResult) {
        usernameField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        passwordCheckField.setEnabled(enabled);
        nameField.setEnabled(enabled);
        emailField.setEnabled(enabled);
        cancelButton.setEnabled(enabled);
        registerButton.setEnabled(enabled);

        for (Integer key : ERROR_MESSAGE_MAP.keySet()) {
            final ErrorMessage msg = ERROR_MESSAGE_MAP.get(key);
            final EditText view = (EditText) findViewById(msg.viewId);
            if ((checkResult & key) > 0) {
                view.setError(getString(msg.msgResId));
            } else {
                view.setError(null);
            }
        }
    }

    /**
     * Checks the input for correctness.
     * 
     * @return True, if the input can be submitted.
     */
    private int checkInput() {
        int result = PASS;
        result += usernameField.getText().length() > 0 ? PASS : FAIL_USERNAME;
        result += passwordField.getText().length() >= 8 ? PASS : FAIL_PASSWORD;
        result += passwordField.getText().toString()
                .equals(passwordCheckField.getText().toString()) ? PASS
                : FAIL_PASSWORD_CHECK;
        result += nameField.getText().length() > 0 ? PASS : FAIL_NAME;
        result += RegisterActivity.checkEmail(emailField.getText().toString()) ? PASS
                : FAIL_EMAIL;
        return result;
    }

    /**
     * Check if the text is a valid email address.
     * 
     * @param text
     *            Text to check.
     * @return True, if the text is an email address.
     */
    private static boolean checkEmail(final String text) {
        final Matcher matcher = EMAIL_PATTERN.matcher(text.toLowerCase(Locale
                .getDefault()));
        return matcher.matches();
    }

    /**
     * Starts the process to register a user.
     */
    private void startRegisterUser() {
        // TODO Auto-generated method stub
    }

    private static class ErrorMessage {

        public int viewId;
        public int msgResId;

        public ErrorMessage(final int viewId, final int msgResId) {
            this.viewId = viewId;
            this.msgResId = msgResId;
        }
    }

}
