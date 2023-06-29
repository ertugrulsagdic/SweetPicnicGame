package com.example.sweetpicnic;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends AppCompatActivity {
    GameView v = null;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disable the title
        //requestWindowFeature (Window.FEATURE_NO_TITLE);
        // Make full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Start the view
        v = new GameView(this);
        setContentView(v);
    }

    @Override
    public void onBackPressed() {
        // On back button pressed go to title activity
        Intent intent = new Intent(this, TitleActivity.class);
        startActivity(intent);
        super.onBackPressed();

    }

    @Override
    protected void onPause () {
        super.onPause();
        if (v != null)
            v.pause();
    }


    private void playBackgroundMusic() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean b = preferences.getBoolean("key_music_enabled", true);
        if (b == true) {
            if (Assets.mediaPlayer != null) {
                Assets.mediaPlayer.release();
                Assets.mediaPlayer = null;
            }
        }
        Assets.mediaPlayer = MediaPlayer.create(this, R.raw.music);
        Assets.mediaPlayer.setLooping(true);
        Assets.mediaPlayer.start();

    }

    private void playGetReady() {


        if (Assets.mediaPlayer != null) {
            Assets.mediaPlayer.release();
            Assets.mediaPlayer = null;
        }
        Assets.mediaPlayer = MediaPlayer.create(this, R.raw.get_ready);
        Assets.mediaPlayer.setLooping(true);
        Assets.mediaPlayer.start();

    }
    @Override
    protected void onResume () {

        playGetReady();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                playBackgroundMusic();

            }
        }, 5000);

        super.onResume();
        if (v != null)
            v.resume();
    }


}