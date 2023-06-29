package com.example.sweetpicnic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TitleActivity extends AppCompatActivity implements View.OnClickListener {

    Button startButton, preferencesButton;
    LinearLayout highScoreLayout;
    TextView highScoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        startButton = findViewById(R.id.start_button);
        preferencesButton = findViewById(R.id.preferences_button);
        highScoreLayout = findViewById(R.id.high_score_layout);
        highScoreText = findViewById(R.id.high_score_text);

        startButton.setOnClickListener(this);
        preferencesButton.setOnClickListener(this);
        highScoreLayout.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        // initialize the shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int highScore = preferences.getInt("high_score", 0);
        highScoreText.setText(String.valueOf(highScore));

        playMusic();
    }

    private void playMusic() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        boolean musicEnabled = preferences.getBoolean("key_music_enabled", true);
        if (musicEnabled == true) {
            if (Assets.mediaPlayer != null) {
                Assets.mediaPlayer.release();
                Assets.mediaPlayer = null;
            }
            Assets.mediaPlayer = MediaPlayer.create(this, R.raw.music_menu);
            Assets.mediaPlayer.setLooping(true);
            Assets.mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            if (Assets.mediaPlayer != null) {
                Assets.mediaPlayer.pause();
                Assets.mediaPlayer.release();
                Assets.mediaPlayer = null;
            }
        }
        super.onPause();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.start_button:
                Intent intent = new Intent(this, GameActivity.class);
                startActivity(intent);

                if (Assets.mediaPlayer != null)
                    Assets.mediaPlayer.pause();

                break;
            case R.id.high_score_layout:
                Intent intent2 = new Intent(this, HighScoreActivity.class);
                startActivity(intent2);
                break;
            case R.id.preferences_button:
                Intent intent3 = new Intent(this, PreferencesActivity.class);
                startActivity(intent3);
                break;
        }

    }
}