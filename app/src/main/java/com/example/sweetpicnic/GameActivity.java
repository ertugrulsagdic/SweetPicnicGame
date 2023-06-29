package com.example.sweetpicnic;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GameActivity extends AppCompatActivity {
    GameView v = null;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Start the view
        v = new GameView(this, handler);
        setContentView(v);
    }

    @Override
    public void onBackPressed() {
        // On back button pressed go to title activity
        handler.removeCallbacksAndMessages(null);
        Assets.soundPool.autoPause();
        Intent intent = new Intent(this, TitleActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (v != null)
            v.pause();
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (v != null)
            v.resume();
    }

}