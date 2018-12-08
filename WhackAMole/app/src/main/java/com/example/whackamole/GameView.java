package com.example.whackamole;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView {

    //private Bitmap bmp1, bmp2, bmp3, bmp4, bmp5, bmp6, bmp7, bmp8, bmp9;
    private SurfaceHolder holder;
    private GameThread gameThread;
    //private ArrayList<Bitmap> bitmaps;
    private ArrayList<Hole> holes;


    public GameView(Context context) {
        super(context);
        gameThread = new GameThread(this);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            //@SuppressLint("WrongCall")
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
//                Canvas c = holder.lockCanvas();
//                onDraw(c);
//                holder.unlockCanvasAndPost(c);
                gameThread.setRunning(true);
                gameThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                gameThread.setRunning(false);
                while(retry){
                    try{
                        gameThread.join();              //join method waits for this thread to die
                        retry = false;
                    }catch (InterruptedException e){

                    }
                }

            }
        });

        Bitmap temp;

        temp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage1);

        for(int i = 0; i < 9; i++){
            Hole hole = new Hole(temp, 1, "up");
            //holes.add(hole);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) { //will be called when View is created and will draw on view using its canvas
        super.onDraw(canvas);
        canvas.drawColor(Color.GREEN);
//        Random r = new Random();
//        int rand = r.nextInt(9);
//        Bitmap temp;

//        if(holes.get(rand).getCurrentStage() == 1) {
//            changeImage(holes.get(rand).getBmp(), 2);
//            holes.get(rand).setDirection("up");
//        }

        //testing
//        holes.get(0).setCurrentStage(2);
//        temp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage2);
//        holes.get(0).setBmp(temp);


//        for(int i = 0; i < holes.size(); i++){
//            if(holes.get(i).getDirection().equals("up")) {
//                switch (holes.get(i).getCurrentStage()) {
//                    case 1:
//                        temp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage2);
//                        holes.get(i).setBmp(temp);
//                        break;
//                    case 2:
//                        temp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage3);
//                        holes.get(i).setBmp(temp);
//                        break;
//                    case 3:
//                        temp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage4);
//                        holes.get(i).setBmp(temp);
//                        break;
//                    case 4:
//                        temp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage3);
//                        holes.get(i).setBmp(temp);
//                        holes.get(i).setDirection("down");
//                        break;
//                }
//            }else if(holes.get(i).getDirection().equals("down")) {
//                    switch (holes.get(i).getCurrentStage()) {
//                        case 1:
//                            temp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage1);
//                            holes.get(i).setBmp(temp);
//                            holes.get(i).setDirection("up");
//                            break;
//                        case 2:
//                            temp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage1);
//                            holes.get(i).setBmp(temp);
//                            break;
//                        case 3:
//                            temp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage2);
//                            holes.get(i).setBmp(temp);
//                            break;
//                        case 4:
//                            temp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage3);
//                            holes.get(i).setBmp(temp);
//                            break;
//                    }
//               }
//        }
//
//        int left = 10;
//        int top = 100;
//
//        for(int x = 0; x < holes.size(); x++){
//            switch(x){
//                case 1: canvas.drawBitmap(holes.get(x).getBmp(), 10, 100, null);
//                        break;
//                case 2: canvas.drawBitmap(holes.get(x).getBmp(), 360, 100, null);
//                    break;
//                case 3: canvas.drawBitmap(holes.get(x).getBmp(), 710, 100, null);
//                    break;
//                case 4: canvas.drawBitmap(holes.get(x).getBmp(), 10, 600, null);
//                    break;
//                case 5: canvas.drawBitmap(holes.get(x).getBmp(), 360, 600, null);
//                    break;
//                case 6: canvas.drawBitmap(holes.get(x).getBmp(), 710, 600, null);
//                    break;
//                case 7: canvas.drawBitmap(holes.get(x).getBmp(), 10, 1100, null);
//                    break;
//                case 8: canvas.drawBitmap(holes.get(x).getBmp(), 360, 1100, null);
//                    break;
//                case 9: canvas.drawBitmap(holes.get(x).getBmp(), 710, 1100, null);
//                    break;
//
//            }
//        }

    }

//    private void changeImage(Bitmap bmp, int stage){
//        switch(stage){
//            case 1: bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage1);
//                    break;
//            case 2: bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage2);
//                break;
//            case 3: bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage3);
//                break;
//            case 4: bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage4);
//                break;
//        }
//    }
}
