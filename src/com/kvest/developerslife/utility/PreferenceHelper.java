package com.kvest.developerslife.utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 1/27/14
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class PreferenceHelper {
    private static final String SHARED_PREFERENCES_NAME = "com.kvest.developerslife.utility.SETTINGS";
    private static final String HINT_SHOWN_KEY = "hint_shown";

    public static void setHintShown(Context context, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        try {
            editor.putBoolean(HINT_SHOWN_KEY, value);
        } finally {
            editor.commit();
        }
    }

    public static boolean getHintShown(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(HINT_SHOWN_KEY, false);
    }
}
