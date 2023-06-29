package com.example.sweetpicnic;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.res.ResourcesCompat;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainThread extends Thread {
    private static final Object lock = new Object();
    private final int MAX_BUGS_NUM = 10;
    private final int MIN_BUGS_NUM = 2;
    int x, y;
    int tx, ty;
    boolean initialized, touched, gameOver;
    Bitmap floor, life, foodBar;
    Context context;

    Handler handler;
    int currentAngle;
    float bugRadius;
    Random generator = new Random();
    int livesLeft;
    int score;
    boolean startPlaying;
    private SurfaceHolder holder;
    private boolean isRunning = false;
    private List<Bug> bugs = new ArrayList<>();
    long startTime;

    public MainThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
        holder = surfaceHolder;
        this.context = context;
        this.handler = handler;
        x = y = 0;
        initialized = false;
        currentAngle = 0;
        touched = false;
        livesLeft = 3;
        startPlaying = false;
        score = 0;
        startTime = System.currentTimeMillis() / 1000;
    }

    public void setRunning(boolean b) {
        isRunning = b;
    }

    public void setXY(int x, int y) {
        synchronized (lock) {
            this.tx = x;
            this.ty = y;
            touched = true;
        }
    }

    private void playGetReadyAndStart() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                playBackgroundMusic();
                startPlaying = true;
            }
        }, 4000);
    }

    @Override
    public void run() {
        while (isRunning) {
            // Lock the canvas before drawing
            if (holder != null) {
                Canvas canvas = holder.lockCanvas();
                if (canvas != null) {
                    if (!initialized) {
                        loadGraphics(canvas);
                        Assets.soundPool.play(Assets.getReady, 1, 1, 1, 0, 1);
                        playGetReadyAndStart();
                        initialized = true;
                    }
                    // Perform UI processing
                    update();
                    // Perform drawing operations on the canvas
                    renderBackground(canvas);

                    if (startPlaying) {
                        renderBugs(canvas);
                    } else {
                        renderCountdown(canvas);
                    }

                    if (livesLeft == 0 && !gameOver) {
                        setLives(canvas);
                        Assets.mediaPlayer.pause();
                        Assets.soundPool.play(Assets.gameOver, 1, 1, 1,0, 1);
                        gameOver = true;
                    }

                    if (gameOver) {
                        renderGameOver(canvas);
                    }

                    // After drawing, unlock the canvas and display it
                    holder.unlockCanvasAndPost(canvas);

                }
            }

        }
    }

    private void update() {
        if (touched && !gameOver) {
            boolean isTouchToBug = false;

            for (Bug bug : bugs) {
                if (bug.isBugDead()) {
                    continue;
                }

                if (touchInCircle(bug)) {
                    Assets.playSquishSound();
                    bug.setBugDead(true);
                    score++;
                    isTouchToBug = true;
                }
            }

            if (!isTouchToBug) {
                Assets.soundPool.play(Assets.thump, 1, 1, 1, 0, 1);
            }
        }

        touched = false;
    }

    private boolean touchInCircle(Bug bug) {
        int centerX = bug.getBugX() + bug.getBug1Image().getWidth() / 2;
        int centerY = bug.getBugY() + bug.getBug1Image().getHeight() / 2;
        double dis = Math.sqrt((this.tx - centerX) * (this.tx - centerX) + (this.ty - centerY) * (this.ty - centerY));

        return dis <= this.bugRadius;
    }


    private void loadGraphics(Canvas canvas) {
        if (initialized) {
            return;
        }

        Bitmap bmp;
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.spider1);

        int newWidth = (int) (canvas.getWidth() * 0.1f);
        float scaleFactor = (float) newWidth / bmp.getWidth();
        int newHeight = (int) (bmp.getHeight() * scaleFactor);

        Bitmap bug1Image = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);
        this.bugRadius = newWidth / 2;

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.spider2);
        Bitmap bug2Image = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.spider_dead);
        Bitmap deadBugImage = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);

        int numberOfBugs = generator.nextInt(MAX_BUGS_NUM - MIN_BUGS_NUM + 1) + MIN_BUGS_NUM;
        for (int i = 0; i < numberOfBugs; i++) {
            int randomMagnitude = generator.nextInt(5) + 2;
            Bug bug = new Bug(canvas.getWidth(), 0, this.bugRadius, bug1Image, bug2Image, deadBugImage, randomMagnitude);
            bugs.add(bug);
        }

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        floor = Bitmap.createScaledBitmap(bmp, canvas.getWidth(), canvas.getHeight(), false);

        float hearthScaleRatio = 0.1f;
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.life);
        life = Bitmap.createScaledBitmap(bmp, (int) (canvas.getWidth() * hearthScaleRatio), (int) (canvas.getWidth() * hearthScaleRatio), false);

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.food_bar);
        newWidth = canvas.getWidth();
        newHeight = (int) (canvas.getHeight() * 0.1f);
        foodBar = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);

        bmp = null;
    }

    private void renderBackground(Canvas canvas) {
        loadGraphics(canvas);

        canvas.drawBitmap(floor, 0, 0, null);
        canvas.drawBitmap(foodBar, 0, (int) (canvas.getHeight() - foodBar.getHeight()), null);

        Typeface customTypeface = ResourcesCompat.getFont(context, R.font.press_start_2p);

        setLives(canvas);
        // render score with the font
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        paint.setTypeface(customTypeface);
        canvas.drawText("Score: " + score, 10, 80, paint);
    }

    private void setLives(Canvas canvas) {
        for (int i = 0; i < livesLeft; i++) {
            // put x to left top corner
            int left = canvas.getWidth() - (i + 1) * life.getWidth() - (i + 1) * 10;
            canvas.drawBitmap(life, left, 0, null);
        }
    }

    private void renderBugs(Canvas canvas) {
        synchronized (lock) {
            for (Bug bug : bugs) {
                if (bug.isBugDead()) {
                    canvas.drawBitmap(bug.getDeadBugImage(), bug.getBugX(), bug.getBugY(), null);
                } else {
                    long curTime = System.currentTimeMillis() / 100 % 10;

                    if (!gameOver) {
                        setBugXY(canvas, bug, curTime);
                    }

                    if (curTime % 2 == 0) {
                        canvas.drawBitmap(bug.getBug1Image(), bug.getBugX(), bug.getBugY(), null);
                    } else {
                        canvas.drawBitmap(bug.getBug2Image(), bug.getBugX(), bug.getBugY(), null);
                    }
                }


                if (bug.getBugY() >= canvas.getHeight() - foodBar.getHeight()) {
                    if (livesLeft > 0 && !bug.getHasPassedFoodBar()) {
                        livesLeft--;

                        Assets.soundPool.play(Assets.eatFood, 1, 1, 1, 0, 1);

                        bug.setHasPassedFoodBar(true);
                    }
                }
            }
        }
    }

    private void renderCountdown(Canvas canvas) {
        // count from 3
        int countDown = 3;
        long curTime = System.currentTimeMillis() / 1000;

        long time = curTime - startTime;
        if (time > 1 && time <= 4) {
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(200);
            paint.setTypeface(ResourcesCompat.getFont(context, R.font.press_start_2p));
            canvas.drawText(String.valueOf(5 - (curTime - startTime)), canvas.getWidth() / 2 - 100, canvas.getHeight() / 2, paint);
        }
    }

    private void renderGameOver(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        int width = canvas.getWidth();
        int textSize = width / 16;
        paint.setTextSize(textSize);
        paint.setTypeface(ResourcesCompat.getFont(context, R.font.press_start_2p));
        canvas.drawText("Game Over!", canvas.getWidth() / 2 - 300, canvas.getHeight() / 2, paint);
    }

    private void setBugXY(Canvas canvas, Bug bug, long curTime) {
        int bugX = bug.getBugX();
        int bugY = bug.getBugY();
        int magnitude = bug.getMagnitude();

        if (bugX >= canvas.getWidth() - bug.getBug1Image().getWidth()) {
            bug.setAccelerationX(-magnitude);
        } else if (bugX <= 0) {
            bug.setAccelerationX(magnitude);
        } else {
            if (curTime % 10 == 0) {
                double rand = generator.nextDouble();
                if (rand < 0.5) {
                    bug.setAccelerationX(magnitude);
                } else {
                    bug.setAccelerationX(-magnitude);
                }
            }
        }

        bugX += bug.getAccelerationX();
        bugY += bug.getMagnitude();

        bug.setBugX(bugX);
        bug.setBugY(bugY);
    }


    private void playBackgroundMusic() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean b = preferences.getBoolean("key_music_enabled", true);
        if (b == true) {
            if (Assets.mediaPlayer != null) {
                Assets.mediaPlayer.release();
                Assets.mediaPlayer = null;
            }
        }
        Assets.mediaPlayer = MediaPlayer.create(context, R.raw.music);
        Assets.mediaPlayer.setLooping(true);
        Assets.mediaPlayer.start();

    }

}
