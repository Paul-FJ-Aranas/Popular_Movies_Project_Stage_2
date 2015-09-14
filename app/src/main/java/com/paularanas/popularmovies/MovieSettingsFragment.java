package com.paularanas.popularmovies;

import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by Paul Aranas on 8/6/2015.
 */
public class MovieSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    Activity context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_key)));

    }



    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));


    }

    // Update preference summary upon change
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);

            } else {
                preference.setSummary(stringValue);
            }

        }
        return true;
    }
}

