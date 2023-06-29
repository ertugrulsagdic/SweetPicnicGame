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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainThread extends Thread {
    private static final Object lock = new Object();
    private final int MAX_BUGS_NUM = 7;
    private final int MIN_BUGS_NUM = 2;
    int touchX, touchY;
    boolean initialized, touched, gameOver;
    Bitmap bug1Image, bug2Image, deadBugImage, floor, life, foodBar;
    Context context;

    Handler handler;
    int currentAngle;
    float bugRadius;
    Random generator = new Random();
    int livesLeft;
    int score, highScore;
    boolean startPlaying, hasHighScorePassed;
    private SurfaceHolder holder;
    private boolean isRunning = false;
    private List<Bug> bugs = new ArrayList<>();
    long startTime;
    private int aliveBugNum = 0;

    SharedPreferences preferences;

    int buttonWidth, buttonHeight, buttonX, buttonY;

    GameView gameView;

    public MainThread(SurfaceHolder surfaceHolder, Context context, Handler handler, SharedPreferences preferences, GameView gameView) {
        holder = surfaceHolder;
        this.context = context;
        this.handler = handler;
        this.preferences = preferences;
        this.gameView = gameView;
        initialized = false;
        currentAngle = 0;
        touched = false;
        livesLeft = 3;
        startPlaying = false;
        score = 0;
        startTime = System.currentTimeMillis() / 1000;
        hasHighScorePassed = false;

        highScore = preferences.getInt("high_score", 0);
    }

    public void setRunning(boolean b) {
        isRunning = b;
    }

    public void setXY(int x, int y) {
        synchronized (lock) {
            this.touchX = x;
            this.touchY = y;
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
                        Assets.playGetReadySound();
                        playGetReadyAndStart();
                        initialized = true;
                    }
                    // Perform UI processing
                    update();
                    // Perform drawing operations on the canvas
                    renderBackground(canvas);

                    if (aliveBugNum <= 2) {
                        addBugs(canvas);
                    }

                    if (startPlaying) {
                        renderBugs(canvas);
                    } else {
                        renderCountdown(canvas);
                    }

                    if (livesLeft == 0 && !gameOver) {
                        renderLives(canvas);
                        Assets.mediaPlayer.pause();
                        Assets.playGameOverSound();
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

    private void checkHighScore() {
        if (score > highScore) {
            if (highScore > 0  && !hasHighScorePassed) {
                Assets.playHighScoreSound();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Awesome! New high score. Keep going", Toast.LENGTH_SHORT).show();
                    }
                });

                hasHighScorePassed = true;
            }

            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("high_score", score);
            editor.apply();

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

                    checkHighScore();

                    isTouchToBug = true;
                    aliveBugNum--;

                    // revome bug from list
                    handler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    bugs.remove(bug);
                                }
                            }, 2000);
                }
            }

            if (!isTouchToBug) {
                Assets.playThumpSound();
            }
        }

        if (touched && gameOver) {
            // check if the touch is on the try again button
            if (touchX >= buttonX && touchX <= buttonX + buttonWidth && touchY >= buttonY && touchY <= buttonY + buttonHeight) {
                // reset the game
                gameView.resetGame();
            }
        }
        touched = false;
    }

    private boolean touchInCircle(Bug bug) {
        int centerX = bug.getBugX() + bug.getBug1Image().getWidth() / 2;
        int centerY = bug.getBugY() + bug.getBug1Image().getHeight() / 2;
        double dis = Math.sqrt((this.touchX - centerX) * (this.touchX - centerX) + (this.touchY - centerY) * (this.touchY - centerY));

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

        this.bug1Image = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);
        this.bugRadius = newWidth / 2;

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.spider2);
        this.bug2Image = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.spider_dead);
        this.deadBugImage = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);

        addBugs(canvas);

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

    private void addBugs(Canvas canvas) {
        int numberOfBugs = generator.nextInt(MAX_BUGS_NUM - MIN_BUGS_NUM + 1) + MIN_BUGS_NUM;
        for (int i = 0; i < numberOfBugs; i++) {
            int randomMagnitude = generator.nextInt(5) + 2;
            Bug bug = new Bug(canvas.getWidth(), 0, this.bugRadius, this.bug1Image, this.bug2Image, this.deadBugImage, randomMagnitude);
            bugs.add(bug);
            aliveBugNum++;
        }
    }

    private void renderBackground(Canvas canvas) {
        loadGraphics(canvas);

        canvas.drawBitmap(floor, 0, 0, null);
        canvas.drawBitmap(foodBar, 0, (int) (canvas.getHeight() - foodBar.getHeight()), null);

        Typeface customTypeface = ResourcesCompat.getFont(context, R.font.press_start_2p);

        renderLives(canvas);
        // render score with the font
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);
        paint.setTypeface(customTypeface);
        canvas.drawText(String.valueOf(score), 25, 120, paint);
    }

    private void renderLives(Canvas canvas) {
        for (int i = 0; i < livesLeft; i++) {
            // put x to left top corner
            int left = canvas.getWidth() - (i + 1) * life.getWidth() - (i + 1) * 15;
            canvas.drawBitmap(life, left, 10, null);
        }
    }

    private void renderBugs(Canvas canvas) {
        synchronized (lock) {
            for (int i = 0; i < bugs.size(); i++) {
                Bug bug = bugs.get(i);
                if (bug == null) {
                    continue;
                }
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

                        Assets.playEatFoodSound();

                        bug.setHasPassedFoodBar(true);

                        aliveBugNum--;


                        handler.postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        bugs.remove(bug);
                                    }
                                }, 0);
                    }
                }
            }
        }
    }

    private void renderCountdown(Canvas canvas) {
        // count from 3
        long curTime = System.currentTimeMillis() / 1000;

        long time = curTime - startTime;
        if (time > 1 && time <= 4) {
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(200);
            paint.setTypeface(ResourcesCompat.getFont(context, R.font.press_start_2p));
            String text = String.valueOf(5 - (curTime - startTime));
            int textWidth = (int) paint.measureText(text);
            canvas.drawText(text, canvas.getWidth() / 2 - textWidth / 2, canvas.getHeight() / 2, paint);
        }
    }

    private void renderGameOver(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        int width = canvas.getWidth();
        int textSize = width / 16;
        paint.setTextSize(textSize);
        paint.setTypeface(ResourcesCompat.getFont(context, R.font.press_start_2p));
        int textWidth = (int) paint.measureText("Game Over!");
        canvas.drawText("Game Over!", canvas.getWidth() / 2 - textWidth / 2, canvas.getHeight() / 2, paint);

        // try again button
        buttonWidth = width / 2;
        buttonHeight = width / 8;
        buttonX = width / 2 - buttonWidth / 2;
        buttonY = canvas.getHeight() / 2 + buttonHeight * 2;
        paint.setColor(Color.rgb(242, 78, 30));
        canvas.drawRect(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(buttonHeight / 3);
        textWidth = (int) paint.measureText("Try Again");
        canvas.drawText("Try Again", buttonX + buttonWidth / 2 - textWidth / 2, buttonY + buttonHeight / 2, paint);
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

        if (Assets.mediaPlayer != null) {
            Assets.mediaPlayer.release();
            Assets.mediaPlayer = null;
        }
        Assets.mediaPlayer = MediaPlayer.create(context, R.raw.music);
        Assets.mediaPlayer.setLooping(true);
        boolean b = preferences.getBoolean("key_music_enabled", true);
        System.out.println("music enabled: " + b);
        if (b == true) {
        Assets.mediaPlayer.start();
        }
    }

}
