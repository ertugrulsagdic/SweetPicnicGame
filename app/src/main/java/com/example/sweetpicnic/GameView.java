package com.example.sweetpicnic;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    int x, y;
    Context context;
    int numberOfSoundsLoaded = 0;
    Handler handler;
    SharedPreferences preferences;
    private SurfaceHolder holder = null;
    private MainThread t = null;



    public GameView(Context context, Handler handler, SharedPreferences preferences) {
        super(context);
        this.context = context;
        this.handler = handler;
        this.preferences = preferences;
        // Init variables
        x = y = 0;
        // Retrieve the SurfaceHolder instance associated with this SurfaceView.
        holder = getHolder();
        // Specify this class (MainView) as the class that implements the three callback methods required by SurfaceHolder.Callback.
        holder.addCallback(this);

        // initialize the sound pool
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Assets.soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
        } else {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            Assets.soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .setMaxStreams(8)
                    .build();
        }

        Assets.soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            numberOfSoundsLoaded++;
            if (numberOfSoundsLoaded == 8) {

                System.out.println("Is Loaded Inside: " + true);
            }
        });
        // load the sounds
        Assets.thump = Assets.soundPool.load(context, R.raw.thump, 1);
        Assets.squish1 = Assets.soundPool.load(context, R.raw.squish1, 1);
        Assets.squish2 = Assets.soundPool.load(context, R.raw.squish3, 1);
        Assets.squish3 = Assets.soundPool.load(context, R.raw.squish3, 1);
        Assets.eatFood = Assets.soundPool.load(context, R.raw.eat_food, 1);
        Assets.getReady = Assets.soundPool.load(context, R.raw.get_ready, 1);
        Assets.gameOver = Assets.soundPool.load(context, R.raw.game_over, 1);
        Assets.highScore = Assets.soundPool.load(context, R.raw.high_score, 1);


    }

    public void resetGame() {
        System.out.println("Reset Game");

        t.setRunning(false);
        t.interrupt();
        t = null;

        // Now create a new MainThread
        t = new MainThread(holder, context, handler, preferences, this);
        t.setRunning(true);
        t.start();

    }

    public void pause() {
        t.setRunning(false);
        while (true) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            break;
        }
    }

    public void resume() {
        // Placeholder
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x, y;
        int action = event.getAction();
        x = event.getX();
        y = event.getY();

        if (action == MotionEvent.ACTION_DOWN) {
            if (t != null) {
                t.setXY((int) x, (int) y);
            }



        }
        return true; // to indicate we have handled this event
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Create and start a drawing thread whose Runnable object is defined by this class (MainView)
        if (t == null) {
            t = new MainThread(holder, context, handler, preferences, this);
            t.setRunning(true);
            t.start();
            setFocusable(true); // make sure we get events
        }
    }

    // Neither of these two methods are used in this example, however, their definitions are required because SurfaceHolder.Callback was implemented
    @Override
    public void surfaceChanged(SurfaceHolder sh, int f, int w, int h) {
        // Placeholder
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder sh) {
        // Placeholder
    }
}
