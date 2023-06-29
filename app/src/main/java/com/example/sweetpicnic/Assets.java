package com.example.sweetpicnic;

import android.media.MediaPlayer;
import android.media.SoundPool;

public class Assets {
    public static MediaPlayer mediaPlayer;
    public static SoundPool soundPool;
    public static int thump, squish1, squish2, squish3, eatFood, getReady, gameOver, highScore;

    public static boolean isSoundsEnabled;


    public static void playSquishSound() {
        if (isSoundsEnabled) {
            int r = (int) (Math.random() * 3);

            switch (r) {
                case 0:
                    soundPool.play(squish1, 1, 1, 1, 0, 1);
                    break;
                case 1:
                    soundPool.play(squish2, 1, 1, 1, 0, 1);
                    break;
                case 2:
                    soundPool.play(squish3, 1, 1, 1, 0, 1);
                    break;
            }
        }
    }

    public static void playThumpSound() {
        if (isSoundsEnabled) {
            soundPool.play(thump, 1, 1, 1, 0, 1);
        }
    }

    public static void playEatFoodSound() {
        if (isSoundsEnabled) {
            soundPool.play(eatFood, 1, 1, 1, 0, 1);
        }
    }

    public static void playGetReadySound() {
        if (isSoundsEnabled) {
            soundPool.play(getReady, 1, 1, 1, 0, 1);
        }
    }

    public static void playGameOverSound() {
        if (isSoundsEnabled) {
            soundPool.play(gameOver, 1, 1, 1, 0, 1);
        }
    }

    public static void playHighScoreSound() {
        if (isSoundsEnabled) {
            soundPool.play(highScore, 1, 1, 1, 0, 1);
        }
    }



}
