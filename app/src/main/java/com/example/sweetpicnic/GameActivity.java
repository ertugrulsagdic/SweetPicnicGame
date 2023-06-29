package com.example.sweetpicnic;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends AppCompatActivity {
    GameView v = null;

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

    @Override
    protected void onResume () {
        super.onResume();
        if (v != null)
            v.resume();
    }


}