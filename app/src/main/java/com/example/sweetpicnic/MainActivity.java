package com.example.sweetpicnic;


import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.example.sweetpicnic.R;

public class MainActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    boolean quit;

    @Override
    protected void onCreate(Bundle inBundle) {
        super.onCreate(inBundle);
        setContentView(R.layout.activity_main);

        quit = false;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!quit) {
                    runAfter();
                }
            }
        }, 2000);


    }

    private void runAfter() {
        Intent intent = new Intent(this, TitleActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        quit = true;
        super.onBackPressed();
    }
}