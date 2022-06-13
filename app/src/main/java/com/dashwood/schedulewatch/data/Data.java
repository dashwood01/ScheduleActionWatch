package com.dashwood.schedulewatch.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * DASHWOOD create this class
 * Class for save preference data and this class have all u need
 * You can save Integer,Boolean and String to preference data
 */
public class Data {
    public static void saveBoolPreference(Context context, String preferenceHome, boolean preferenceValuee, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(preferenceHome, preferenceValuee);
        editor.apply();
    }

    public static boolean readBoolPreference(Context context, String preferenceHome, boolean defultValue, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(preferenceHome, defultValue);

    }

    public static void saveToPrefermenceString(Context context, String preferenceHome, String preferenceValuee, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceHome, preferenceValuee);
        editor.apply();
    }

    public static String readPreferencesString(Context context, String preferenceHome, String defaultValue, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceHome, defaultValue);
    }

    public static void saveToPrefermenceInteger(Context context, String preferenceHome, int preferenceValuee, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(preferenceHome, preferenceValuee);
        editor.apply();
    }

    public static int readPreferencesInteger(Context context, String preferenceHome, int defaultValue, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(preferenceHome, defaultValue);
    }
}
