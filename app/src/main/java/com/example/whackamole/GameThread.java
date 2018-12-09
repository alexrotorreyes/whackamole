package com.example.whackamole;

import android.annotation.SuppressLint;
import android.graphics.Canvas;

public class GameThread extends Thread {
    private GameView view;
    private boolean running = false;

    public GameThread(GameView view){
        this.view = view;

    }

    public void setRunning(boolean run){
        running = run;
    }

    //@SuppressLint("WrongCall")
    @Override
    public void run(){
        while(running){ //while canvas is running
            Canvas c = null;
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try{
                c = view.getHolder().lockCanvas();
                synchronized(view.getHolder()){
                    view.onDraw(c);
                }
            }finally{
                if(c != null){
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }
        }
    }
}
