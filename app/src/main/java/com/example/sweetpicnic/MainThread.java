package com.example.sweetpicnic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
    private static final Object lock = new Object();
    int x, y;
    int tx, ty;
    boolean initialized, touched, isDead;
    Bitmap bug1, bug2, dead_bug, floor, lives[], foodBar;
    Context context;
    int accelerationX, accelerationY, magnitude;
    int currentAngle;
    float bugRadius;
    private SurfaceHolder holder;
    private boolean isRunning = false;


    public MainThread(SurfaceHolder surfaceHolder, Context context) {
        holder = surfaceHolder;
        this.context = context;
        x = y = 0;
        initialized = false;
        accelerationX = 0;
        accelerationY = 0;
        magnitude = 10;
        currentAngle = 0;
        touched = false;
        isDead = false;
        lives = new Bitmap[3];
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
        //while (!isRunning);
        while (isRunning) {
            // Lock the canvas before drawing
            if (holder != null) {
                Canvas canvas = holder.lockCanvas();
                if (canvas != null) {
                    // Perform UI processing
                    update(canvas);
                    // Perform drawing operations on the canvas
                    render(canvas);
                    // After drawing, unlock the canvas and display it
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void update(Canvas canvas) {
        if (touched) {
            if (TouchInCircle(x, y)) {
                Assets.playSquishSound();
                isDead = true;
            } else {
                Assets.soundPool.play(Assets.thump, 1, 1, 1, 0, 1);
            }

            touched = false;
        }
    }

    private boolean TouchInCircle(int x, int y) {
        int centerX = x + bug1.getWidth() / 2;
        int centerY = y + bug1.getHeight() / 2;
        // find distance between touch and center of bug
        double dist = Math.sqrt((tx - centerX) * (tx - centerX) + (ty - centerY) * (ty - centerY));

        if (dist < bugRadius) {
            return true;
        } else {
            return false;
        }
    }


    private void loadGraphics(Canvas canvas) {
        if (!initialized) {
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.spider1);

            int newWidth = (int) (canvas.getWidth() * 0.2f);

            float scaleFact = (float) newWidth / bmp.getWidth();

            int newHeight = (int) (bmp.getHeight() * scaleFact);

            bugRadius = newWidth / 2;

            System.out.println("newWidth: " + newWidth + " newHeight: " + newHeight);

            bug1 = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);

            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.spider2);

            bug2 = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);

            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.spider_dead);

            dead_bug = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);

            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);

            floor = Bitmap.createScaledBitmap(bmp, canvas.getWidth(), canvas.getHeight(), false);


            bmp = BitmapFactory.decodeResource (context.getResources(), R.drawable.life);

            float hearthScaleRatio = 0.1f;
            lives[0] = Bitmap.createScaledBitmap(bmp, (int)(canvas.getWidth()*hearthScaleRatio), (int)(canvas.getWidth()*hearthScaleRatio), false);
            lives[1] = Bitmap.createScaledBitmap(bmp, (int)(canvas.getWidth()*hearthScaleRatio), (int)(canvas.getWidth()*hearthScaleRatio), false);
            lives[2] = Bitmap.createScaledBitmap(bmp, (int)(canvas.getWidth()*hearthScaleRatio), (int)(canvas.getWidth()*hearthScaleRatio), false);

            bmp = BitmapFactory.decodeResource (context.getResources(), R.drawable.food_bar);
            newWidth = (int) (canvas.getWidth() * 0.2f);
            scaleFact = (float) newWidth / bmp.getWidth();
            newHeight = (int) (bmp.getHeight() * scaleFact);
            foodBar = Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), false);


            bmp = null;

            initialized = true;
        }
    }


    private void render(Canvas canvas) {
        int xx, yy;
        loadGraphics(canvas);

        canvas.drawBitmap(floor, 0, 0, null);
        canvas.drawBitmap(foodBar, 0, (int)(canvas.getHeight()-foodBar.getHeight()), null);
        canvas.drawBitmap(lives[0], 0, 0, null);
        canvas.drawBitmap(lives[1], lives[0].getWidth(), 0, null);
        canvas.drawBitmap(lives[2], lives[0].getWidth()*2, 0, null);


        long time = System.currentTimeMillis() / 100 % 10;

        if (!isDead) {
            // Draw a white circle at position (100, 100) with a radius of 100
            synchronized (lock) {

                if (x >= canvas.getWidth() - bug1.getWidth()) {
                    accelerationX = -magnitude;
                } else if (x <= 0) {
                    accelerationX = magnitude;
                } else {
                    if (time % 10 == 0) {
                        double rand = Math.random();
                        if (rand < 0.4)
                            accelerationX = magnitude;
                        else if (rand > 0.4 && rand < 0.8)
                            accelerationX = -magnitude;
                        else
                            accelerationX = 0;
                    }
                }

                if (y >= canvas.getHeight() - bug1.getHeight()) {
                    accelerationY = -magnitude;
                } else if (y <= 0) {
                    accelerationY = magnitude;
                } else {
                    if (time % 10 == 0) {
                        double rand = Math.random();
                        if (rand < 0.4)
                            accelerationY = magnitude;
                        else if (rand > 0.4 && rand < 0.8)
                            accelerationY = -magnitude;
                        else
                            accelerationY = 0;
                    }
                }

                if (accelerationX == 0 && accelerationY == 0) {
                    accelerationX = magnitude;
                    accelerationY = magnitude;
                }
                x += accelerationX;
                y += accelerationY;

                xx = x;
                yy = y;
            }

            rotateAccordingToAcceleration(canvas, xx, yy);

            if (time % 2 == 0) {
                canvas.drawBitmap(bug1, xx, yy, null);
            } else {
                canvas.drawBitmap(bug2, xx, yy, null);
            }

        } else {
            rotateAccordingToAcceleration(canvas, x, y);
            canvas.drawBitmap(dead_bug, x, y, null);
        }
    }

    private void rotateAccordingToAcceleration(Canvas canvas, int xx, int yy) {
        int x = xx + bug1.getWidth() / 2;
        int y = yy + bug1.getHeight() / 2;
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
