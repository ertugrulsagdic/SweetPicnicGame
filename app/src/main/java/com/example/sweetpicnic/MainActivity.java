// Ertugrul Sagdic
// Goksel Tokur
// Arda Bayram

package com.example.sweetpicnic;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    boolean quit;
    private Handler handler = new Handler();

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