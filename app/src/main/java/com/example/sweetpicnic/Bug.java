package com.example.sweetpicnic;

import android.graphics.Bitmap;

public class Bug {
    private int bugX, bugY;
    private boolean isBugDead;
    private Bitmap bug1Image, bug2Image, deadBugImage;
    private float bugRadius;


    private int magnitude;
    private int accelerationX;

    public Bug(int width, int bugY, float bugRadius, Bitmap bug1Image, Bitmap bug2Image, Bitmap deadBugImage, int magnitude) {
        this.bugX = randomWidth(width);
        this.bugY = bugY;
        this.bugRadius = bugRadius;
        this.bug1Image = bug1Image;
        this.bug2Image = bug2Image;
        this.deadBugImage = deadBugImage;
        this.isBugDead = false;
        this.magnitude = magnitude;
        this.accelerationX = 0;
    }

    private int randomWidth(int width) {
        return (int) (Math.random() * width);
    }

    public Bitmap getBug1Image() {
        return bug1Image;
    }

    public void setBug1Image(Bitmap bug1Image) {
        this.bug1Image = bug1Image;
    }

    public Bitmap getBug2Image() {
        return bug2Image;
    }

    public void setBug2Image(Bitmap bug2Image) {
        this.bug2Image = bug2Image;
    }

    public Bitmap getDeadBugImage() {
        return deadBugImage;
    }

    public void setDeadBugImage(Bitmap deadBugImage) {
        this.deadBugImage = deadBugImage;
    }

    public int getBugX() {
        return bugX;
    }

    public void setBugX(int bugX) {
        this.bugX = bugX;
    }

    public int getBugY() {
        return bugY;
    }

    public void setBugY(int bugY) {
        this.bugY = bugY;
    }

    public boolean isBugDead() {
        return isBugDead;
    }

    public void setBugDead(boolean isBugDead) {
        this.isBugDead = isBugDead;
    }

    public int getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(int magnitude) {
        this.magnitude = magnitude;
    }

    public int getAccelerationX() {
        return accelerationX;
    }

    public void setAccelerationX(int accelerationX) {
        this.accelerationX = accelerationX;
    }

}
