package com.dijiaapp.eatserviceapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by wjy on 16/10/9.
 */

public class SettingsUtils {

    private static final String PREF_REMEMBER_PASSWORD = "remember_password";

    public static boolean isRememberPassword(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_REMEMBER_PASSWORD, false);
    }

    public static void setPrefRememberPassword(Context context, boolean newValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_REMEMBER_PASSWORD, newValue).apply();
    }

    private static final String PREF_AUTO_LOGIN = "auto_login";

    public static boolean isAutoLogin(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_AUTO_LOGIN, false);
    }

    public static void setPrefAutoLogin(Context context, boolean newValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_AUTO_LOGIN, newValue).apply();
    }


}
