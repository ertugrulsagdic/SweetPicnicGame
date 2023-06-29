package com.example.sweetpicnic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

public class PreferencesFragmentSettings extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public PreferencesFragmentSettings() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preference from an xml resource
        addPreferencesFromResource(R.xml.preferences_fragment_settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener when a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        Preference preference;
        preference = getPreferenceScreen().findPreference("key_student_info");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Handle action on click
                try {
                    Uri site = Uri.parse("https://www.linkedin.com/in/ertugrulsagdic/");
                    Intent intent = new Intent(Intent.ACTION_VIEW, site);
                    startActivity(intent);
                }
                catch (Exception e) {
                    Log.e("PreferencesFragment", "Browser failed" + e);
                }
                return true;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("key_music_enabled")) {

        }
    }
}
