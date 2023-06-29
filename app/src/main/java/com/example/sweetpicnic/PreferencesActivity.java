package com.example.sweetpicnic;

import android.os.Build;
import android.preference.PreferenceActivity;

import java.util.List;

public class PreferencesActivity extends PreferenceActivity {

    @Override
    protected boolean isValidFragment(String fragmentName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return true;
        }
        else if (PreferencesFragmentSettings.class.getName().equals(fragmentName)) {
            return true;
        }
        return false;
    }

    @Override
    public void onBuildHeaders (List<Header> target) {
        // Use this to load an XML file containing references to multiple fragments (a multi-screen preferences screen)
        // loadHeadersFromResource(R.xml.preference_headers, target);

        // User this to load an XML file containing a single preferences screen
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragmentSettings()).commit();
    }
}