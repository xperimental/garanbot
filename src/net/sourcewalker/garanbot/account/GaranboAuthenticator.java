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

import net.sourcewalker.garanbot.R;
import net.sourcewalker.garanbot.api.AuthenticationException;
import net.sourcewalker.garanbot.api.ClientException;
import net.sourcewalker.garanbot.api.GaranboClient;
import net.sourcewalker.garanbot.data.GaranboItemsProvider;
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
        String password = accountManager.getPassword(account);
        GaranboClient client = new GaranboClient(account.name, password);
        boolean success;
        try {
            client.user().get();
            success = true;
        } catch (AuthenticationException e) {
            success = false;
        } catch (ClientException e) {
            throw new NetworkErrorException("Service not available!", e);
        }
        Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, success);
        return result;
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

    /*
     * (non-Javadoc)
     * @see
     * android.accounts.AbstractAccountAuthenticator#getAccountRemovalAllowed
     * (android.accounts.AccountAuthenticatorResponse, android.accounts.Account)
     */
    @Override
    public Bundle getAccountRemovalAllowed(
            AccountAuthenticatorResponse response, Account account)
            throws NetworkErrorException {
        context.getContentResolver().delete(
                GaranboItemsProvider.CONTENT_URI_ITEMS, null, null);
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true);
        return result;
    }

}
