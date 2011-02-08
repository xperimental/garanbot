package net.sourcewalker.garanbot.account;

import net.sourcewalker.garanbot.R;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * This authenticator can create and validate accounts on the Garanbo server.
 * 
 * @author Xperimental
 */
public class GaranboAuthenticator extends AbstractAccountAuthenticator {

    private final AccountManager accountManager;
    private final Context context;
    private final String type;

    public GaranboAuthenticator(Context context) {
        super(context);

        this.accountManager = AccountManager.get(context);
        this.context = context;
        this.type = context.getString(R.string.account_type);
    }

    /*
     * (non-Javadoc)
     * @see
     * android.accounts.AbstractAccountAuthenticator#addAccount(android.accounts
     * .AccountAuthenticatorResponse, java.lang.String, java.lang.String,
     * java.lang.String[], android.os.Bundle)
     */
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response,
            String accountType, String authTokenType,
            String[] requiredFeatures, Bundle options)
            throws NetworkErrorException {
        if (accountType.equals(type) == false) {
            throw new IllegalArgumentException("Invalid account type: "
                    + accountType);
        }
        final Bundle bundle = new Bundle();
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
                response);
        if (accountManager.getAccountsByType(accountType).length > 0) {
            intent.setAction(LoginActivity.ACTION_ERROR);
            intent.putExtra(AccountManager.KEY_ERROR_MESSAGE,
                    context.getString(R.string.toast_account_onlyone));
        }
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see
     * android.accounts.AbstractAccountAuthenticator#confirmCredentials(android
     * .accounts.AccountAuthenticatorResponse, android.accounts.Account,
     * android.os.Bundle)
     */
    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response,
            Account account, Bundle options) throws NetworkErrorException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * android.accounts.AbstractAccountAuthenticator#editProperties(android.
     * accounts.AccountAuthenticatorResponse, java.lang.String)
     */
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response,
            String accountType) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * android.accounts.AbstractAccountAuthenticator#getAuthToken(android.accounts
     * .AccountAuthenticatorResponse, android.accounts.Account,
     * java.lang.String, android.os.Bundle)
     */
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response,
            Account account, String authTokenType, Bundle options)
            throws NetworkErrorException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * android.accounts.AbstractAccountAuthenticator#getAuthTokenLabel(java.
     * lang.String)
     */
    @Override
    public String getAuthTokenLabel(String authTokenType) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * android.accounts.AbstractAccountAuthenticator#hasFeatures(android.accounts
     * .AccountAuthenticatorResponse, android.accounts.Account,
     * java.lang.String[])
     */
    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response,
            Account account, String[] features) throws NetworkErrorException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * android.accounts.AbstractAccountAuthenticator#updateCredentials(android
     * .accounts.AccountAuthenticatorResponse, android.accounts.Account,
     * java.lang.String, android.os.Bundle)
     */
    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response,
            Account account, String authTokenType, Bundle options)
            throws NetworkErrorException {
        // TODO Auto-generated method stub
        return null;
    }

}
