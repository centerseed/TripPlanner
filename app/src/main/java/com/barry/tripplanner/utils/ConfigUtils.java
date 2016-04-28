package com.barry.tripplanner.utils;

import android.content.Context;
import android.preference.PreferenceManager;

public class ConfigUtils {
    public static void setLocalUsageOnly(Context context, boolean b) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("local_only", b).commit();
    }

    public static boolean getLocalUsageOnly(Context context) {
        boolean result = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("local_only", false);
        return result;
    }
}
