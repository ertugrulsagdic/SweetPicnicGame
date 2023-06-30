// Ertugrul Sagdic
// Goksel Tokur
// Arda Bayram

package com.example.sweetpicnic;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

public class HighScoreFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public HighScoreFragment() {
        // Placeholder
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preference from an xml resource
        addPreferencesFromResource(R.xml.high_score_fragment);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int highScore = sharedPreferences.getInt("high_score", 0);

        Preference preferenceHighScore = getPreferenceScreen().findPreference("high_score_text");

        preferenceHighScore.setSummary(String.valueOf(highScore));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener when a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        Preference preferenceResetHighScore = getPreferenceScreen().findPreference("reset_high_score");
        preferenceResetHighScore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Reset High Score")
                        .setMessage("Are you sure you want to reset high score?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                resetHighScore();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        });
    }

    private void resetHighScore() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("high_score", 0);
        editor.apply();

        Preference preference = getPreferenceScreen().findPreference("high_score_text");
        preference.setSummary(String.valueOf(0));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Placeholder
    }
}
