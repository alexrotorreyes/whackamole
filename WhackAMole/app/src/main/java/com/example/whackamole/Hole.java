package com.example.whackamole;

import android.graphics.Bitmap;

public class Hole {
    private Bitmap bmp;
    private int currentStage;
    private String direction;

    public Hole(Bitmap bmp, int currentStage, String direction){
        this.bmp = bmp;
        this.currentStage = currentStage;
        this.direction = direction;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(int currentStage) {
        this.currentStage = currentStage;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
