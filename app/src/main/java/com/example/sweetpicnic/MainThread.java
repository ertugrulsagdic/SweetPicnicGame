package com.example.sweetpicnic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainThread extends Thread {
    private static final Object lock = new Object();
    int x, y;
    int tx, ty;
    boolean initialized, touched, isDead;
    Bitmap floor, life, foodBar;
    Context context;
    int accelerationX, accelerationY, magnitude;
    int currentAngle;
    float bugRadius;
    Random generator = new Random();
    int livesLeft;
    private SurfaceHolder holder;
    private boolean isRunning = false;
    private List<Bug> bugs = new ArrayList<>();

    public MainThread(SurfaceHolder surfaceHolder, Context context) {
        holder = surfaceHolder;
        this.context = context;
        x = y = 0;
        initialized = false;
        accelerationX = 0;
        accelerationY = 0;
        magnitude = 5;
        currentAngle = 0;
        touched = false;
        isDead = false;
        livesLeft = 3;
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

    @Override
    public void run() {
        while (isRunning) {
            // Lock the canvas before drawing
            if (holder != null) {
                Canvas canvas = holder.lockCanvas();
                if (canvas != null) {
                    // Perform UI processing
                    update();
                    // Perform drawing operations on the canvas
                    render(canvas);
                    // After drawing, unlock the canvas and display it
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void update() {
        for (Bug bug : bugs) {
            if (touched && touchInCircle(bug)) {
                Assets.playSquishSound();
                bug.setBugDead(true);
            } else {
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

        int newWidth = (int) (canvas.getWidth() * 0.2f);
        float scaleFactor = (float) newWidth / bmp.getWidth();
        int newHeight = (int) (bmp.getHeight() * scaleFactor);

        Bitmap bug1Image = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);
        this.bugRadius = newWidth * 0.66f;

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.spider2);
        Bitmap bug2Image = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.spider_dead);
        Bitmap deadBugImage = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);

        for (int i = 0; i < 5; i++) {
            int bugY = i * 30;
            Bug bug = new Bug(canvas.getWidth(), bugY, this.bugRadius, bug1Image, bug2Image, deadBugImage);
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

        initialized = true;
    }

    private void render(Canvas canvas) {
        loadGraphics(canvas);

        canvas.drawBitmap(floor, 0, 0, null);
        canvas.drawBitmap(foodBar, 0, (int) (canvas.getHeight() - foodBar.getHeight()), null);

        for (int i = 0; i < livesLeft; i++) {
            canvas.drawBitmap(life, i * life.getWidth() + (i + 1) * 10, 0, null);
        }

        // Draw a white circle at position (100, 100) with a radius of 100
        synchronized (lock) {
            for (Bug bug : bugs) {
                if (bug.isBugDead()) {
                    canvas.drawBitmap(bug.getDeadBugImage(), bug.getBugX(), bug.getBugY(), null);
                    bugs.remove(bug);
                } else {
                    long curTime = System.currentTimeMillis() / 100 % 10;

                    setBugXY(canvas, bug, curTime);
//                    rotateAccordingToAcceleration(canvas, bug);

                    if (curTime % 2 == 0) {
                        canvas.drawBitmap(bug.getBug1Image(), bug.getBugX(), bug.getBugY(), null);
                    } else {
                        canvas.drawBitmap(bug.getBug2Image(), bug.getBugX(), bug.getBugY(), null);
                    }
                }
            }
        }
    }

    private void setBugXY(Canvas canvas, Bug bug, long curTime) {
        int bugX = bug.getBugX();
        int bugY = bug.getBugY();

        if (bugX >= canvas.getWidth() - bug.getBug1Image().getWidth()) {
            accelerationX = -magnitude;
        } else if (bugX <= 0) {
            accelerationX = magnitude;
        } else {
            if (curTime % 10 == 0) {
                double rand = generator.nextDouble();
                if (rand < 0.4) {
                    accelerationX = magnitude;
                } else if (rand > 0.4 && rand < 0.8) {

                    accelerationX = -magnitude;
                } else {
                    accelerationX = 0;
                }
            }
        }

        bugX += accelerationX;
        bugY += magnitude;

        bug.setBugX(bugX);
        bug.setBugY(bugY);
    }


    private void rotateAccordingToAcceleration(Canvas canvas, Bug bug) {
        int x = bug.getBugX() + bug.getBug1Image().getWidth() / 2;
        int y = bug.getBugY() + bug.getBug1Image().getHeight() / 2;
        int angle = 0;

        if (accelerationY < 0 && accelerationX == 0) {
            angle = 180;
        }

        if (accelerationY > 0 && accelerationX < 0) {
            angle = 45;
        }

        if (accelerationY > 0 && accelerationX > 0) {
            angle = 315;
        }
        if (accelerationY < 0 && accelerationX < 0) {
            angle = 135;
        }

        if (accelerationY < 0 && accelerationX > 0) {
            angle = 225;
        }

        if (accelerationY > 0 && accelerationX == 0) {
            angle = 0;
        }

        if (accelerationY == 0 && accelerationX < 0) {
            angle = 90;
        }

        if (accelerationY == 0 && accelerationX > 0) {
            angle = 270;
        }

        canvas.rotate(angle, x, y);

    }

}
