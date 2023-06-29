package com.example.sweetpicnic;

import android.media.MediaPlayer;
import android.media.SoundPool;

public class Assets {
    public static MediaPlayer mediaPlayer;
    public static SoundPool soundPool;
    public static int thump, squish1, squish2, squish3;

    public static void playSquishSound() {
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
