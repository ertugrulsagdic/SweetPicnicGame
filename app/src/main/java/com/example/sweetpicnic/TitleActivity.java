package com.example.sweetpicnic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TitleActivity extends AppCompatActivity  implements View.OnClickListener {

    Button startButton;
    LinearLayout highScoreLayout;
    TextView highScoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        startButton = findViewById(R.id.start_button);
        highScoreLayout = findViewById(R.id.high_score_layout);
        highScoreText = findViewById(R.id.high_score_text);

        startButton.setOnClickListener(this);
        highScoreLayout.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int highScore = sharedPreferences.getInt("high_score", 0);
        highScoreText.setText(String.valueOf(highScore));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.start_button:

                Intent intent = new Intent(this, GameActivity.class);
                startActivity(intent);
                break;
            case R.id.high_score_layout:
                Intent intent2 = new Intent(this, HighScoreActivity.class);
                startActivity(intent2);
                break;
        }

    }
}