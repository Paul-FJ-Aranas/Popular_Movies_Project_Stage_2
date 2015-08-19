package com.paularanas.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by Paul Aranas on 8/16/2015.
 */
public class Utility {

    // return sort order preference
    public static String getSortingOrderPreference(Context context) {

        SharedPreferences prefs = getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key), context.getString(R.string.sort_by_default_value));
    }

    // initialize sortPreferences
    public static void initializeSortPreference(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("sort_by", context.getString(R.string.sort_by_popularity_value));
        editor.apply();

    }

}
