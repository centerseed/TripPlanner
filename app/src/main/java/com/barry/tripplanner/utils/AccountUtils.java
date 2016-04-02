package com.barry.tripplanner.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.preference.PreferenceManager;

import com.barry.tripplanner.R;

public class AccountUtils {
    public static Account getCurrentAccount(Context context) {
        Account accounts[] = AccountManager.get(context).getAccounts();

        for (Account account : accounts) {
            if (account.type.equals(context.getString(R.string.tripAccountType))) return account;
        }
        return null;
    }

    public static String getCurrentAccountID(Context context) {
        Account accounts[] = AccountManager.get(context).getAccounts();

        for (Account account : accounts) {
            if (account.type.equals(context.getString(R.string.tripAccountType))) {
                return AccountManager.get(context).getPassword(account);
            }
        }
        return null;
    }

    public static String getAccountPictureURL(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("account_picture", "");
    }

    public static void setAccountPicture(Context context, String url) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("account_picture", url).commit();
    }
}
