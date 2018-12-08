package com.example.whackamole;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView {
    private SurfaceHolder holder;
    private GameThread gameThread;
    private ArrayList<Hole> holes = new ArrayList<>();
    private int whacks = 0;
    private int currHole = 0;
    private boolean finish = false;


    public GameView(Context context) {
        super(context);
        gameThread = new GameThread(this);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                gameThread.setRunning(true);
                gameThread.start();

                Bitmap temp;

                temp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage1);

                for(int i = 0; i < 9; i++){
                    Hole hole = new Hole(temp, 1, "up");
                    holes.add(hole);
                }
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


    }

    @Override
    protected void onDraw(Canvas canvas) { //will be called when View is created and will draw on view using its canvas
        super.onDraw(canvas);
        canvas.drawColor(Color.GREEN);
        Random r = new Random();
        int rand = r.nextInt(9);
        Bitmap temp;

//        ArrayList<Hole> withMole = new ArrayList<>();
//
//        for(int i = 0; i < holes.size(); i++){          //making an arrayList of holes with moles <3
//            if(holes.get(i).getCurrentStage() > 1){
//                withMole.add(holes.get(i));
//            }
//        }

        int ctr = 0;
        //before adding another random mole on screen, check if number of existing moles is less than 3
        if(holes.get(rand).getCurrentStage() == 1 && finish == true) { //if no mole in hole
            finish = false;
            currHole = rand;
            update(2, rand);
            holes.get(rand).setDirection("up");
        }


        //up and down movements
        //for(int i = 0; i < holes.size(); i++){
        int i = currHole;
            if(holes.get(i).getDirection().equals("up")) {
                switch (holes.get(i).getCurrentStage()) {
                    case 1:
                        update(2, i);
                        break;
                    case 2:
                        update(3, i);
                        break;
                    case 3:
                        update(4, i);
                        break;
                    case 4:
                        update(4, i);
                        holes.get(i).setDirection("down");
                        break;
                }
            }else if(holes.get(i).getDirection().equals("down")) {
                    switch (holes.get(i).getCurrentStage()) {
                        case 1:
                            update(1, i);
                            holes.get(i).setDirection("up");
                            finish = true;
                            break;
                        case 2:
                            update(1, i);
                            break;
                        case 3:
                            update(2, i);
                            break;
                        case 4:
                            update(3, i);
                            break;
                    }
               }
       // }

        int left = 10;
        int top = 100;

        for(int x = 0; x < holes.size(); x++){
            switch(x){
                case 0: canvas.drawBitmap(holes.get(x).getBmp(), 10, 100, null);
                        holes.get(x).setX(10);
                        holes.get(x).setY(100);
                        break;
                case 1: canvas.drawBitmap(holes.get(x).getBmp(), 360, 100, null);
                    holes.get(x).setX(360);
                    holes.get(x).setY(100);
                    break;
                case 2: canvas.drawBitmap(holes.get(x).getBmp(), 710, 100, null);
                    holes.get(x).setX(710);
                    holes.get(x).setY(100);
                    break;
                case 3: canvas.drawBitmap(holes.get(x).getBmp(), 10, 600, null);
                    holes.get(x).setX(10);
                    holes.get(x).setY(600);
                    break;
                case 4: canvas.drawBitmap(holes.get(x).getBmp(), 360, 600, null);
                    holes.get(x).setX(360);
                    holes.get(x).setY(600);
                    break;
                case 5: canvas.drawBitmap(holes.get(x).getBmp(), 710, 600, null);
                    holes.get(x).setX(710);
                    holes.get(x).setY(600);
                    break;
                case 6: canvas.drawBitmap(holes.get(x).getBmp(), 10, 1100, null);
                    holes.get(x).setX(10);
                    holes.get(x).setY(1100);
                    break;
                case 7: canvas.drawBitmap(holes.get(x).getBmp(), 360, 1100, null);
                    holes.get(x).setX(360);
                    holes.get(x).setY(1100);
                    break;
                case 8: canvas.drawBitmap(holes.get(x).getBmp(), 710, 1100, null);
                    holes.get(x).setX(710);
                    holes.get(x).setY(1100);
                    break;

            }
        }

        Paint paint = new Paint();
        Bitmap b = Bitmap.createBitmap(200, 200, Bitmap.Config.ALPHA_8);
        Canvas c = new Canvas(b);
        c.drawRect(0, 0, 200, 200, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        paint.setTextSize(40);
        paint.setTextScaleX(1.f);
        paint.setAlpha(0);
        paint.setAntiAlias(true);
        c.drawText("WHACKS:", 15, 40, paint);
        c.drawText(String.valueOf(whacks), 20, 120, paint);
        paint.setColor(Color.DKGRAY);
        canvas.drawBitmap(b, 450,1500, paint);


    }

    private void update(int stage, int offset){
        Bitmap bmp;
        switch(stage){
            case 1: bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage1);
                    holes.get(offset).setBmp(bmp);
                    holes.get(offset).setCurrentStage(1);
                    break;
            case 2: bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage2);
                    holes.get(offset).setBmp(bmp);
                    holes.get(offset).setCurrentStage(2);
                    break;
            case 3: bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage3);
                    holes.get(offset).setBmp(bmp);
                    holes.get(offset).setCurrentStage(3);
                    break;
            case 4: bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage4);
                    holes.get(offset).setBmp(bmp);
                    holes.get(offset).setCurrentStage(4);
                    break;
        }
    }

    private boolean clickOnBitmap(Hole hole, MotionEvent event) {
        Bitmap bmp = hole.getBmp();
        float xEnd = hole.getX() + bmp.getWidth();
        float yEnd = hole.getY() + bmp.getHeight();


        if ((event.getX() >= hole.getX() && event.getX() <= xEnd)
                && (event.getY() >= hole.getY() && event.getY() <= yEnd) ) {
            int pixX = (int) (event.getX() - hole.getX());
            int pixY = (int) (event.getY() - hole.getY());
            if (!(bmp.getPixel(pixX, pixY) == 0)) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if((holes.get(currHole).getCurrentStage() == 4 || holes.get(currHole).getCurrentStage() == 3) && finish == false) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (clickOnBitmap(holes.get(currHole), event)) {
                        whacks++;
                    }
                    return true;
                case MotionEvent.ACTION_OUTSIDE:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    return true;
            }
        }
        return false;
    }
}