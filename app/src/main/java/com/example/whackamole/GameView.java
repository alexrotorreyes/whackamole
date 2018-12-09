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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
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
    private Canvas canvas;
    private Handler handler;
    private boolean whackoMode = false;
    private int moleType = 1;
    private SensorManager sensorManager;
    private float acelVal;      //CURRENT ACCELERATION VALUE AND GRAVITY
    private float acelLast;     //LAST ACCELERATION VALUE AND GRAVITY
    private float shake;        //ACCELERATION VALUE DIFFER FROM GRAVITY

    public GameView(final Context context) {
        super(context);
        gameThread = new GameThread(this);

        holder = getHolder();
        holder.setFixedSize(1060, 1960);
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

        handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
                sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

                acelVal = SensorManager.GRAVITY_EARTH;
                acelLast = SensorManager.GRAVITY_EARTH;
                shake = 0.00f;
                handler.postDelayed(this, 30000);
                reDraw();
            }
        };

        handler.postDelayed(r, 30000);

    }

    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            acelLast = acelVal;
            acelVal = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = acelVal - acelLast;

            shake = shake * 0.9f + delta;

            if (shake > 8)
            {
                whacks++;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onDraw(Canvas c) { //will be called when View is created and will draw on view using its canvas
        super.onDraw(c);
        this.canvas = c;

        if(whackoMode == false){
            regularMode(c);
        }
        else{
            whackoMode();
        }

    }

    protected void reDraw() {
        this.invalidate();
        whackoMode = true;

    }

    public void whackoMode(){
        finish = true;
        canvas.drawColor(Color.MAGENTA);
        Bitmap label = BitmapFactory.decodeResource(getResources(), R.mipmap.whackomodelabel);
        Bitmap shakeLbl = BitmapFactory.decodeResource(getResources(), R.mipmap.shake);
        Bitmap mole1 = BitmapFactory.decodeResource(getResources(), R.mipmap.whackomode1);
        Bitmap mole2 = BitmapFactory.decodeResource(getResources(), R.mipmap.whackomode2);


        canvas.drawBitmap(label, 25, 100, null);
        canvas.drawBitmap(shakeLbl, 110, 350, null);

        if(moleType == 1) {
            canvas.drawBitmap(mole2, 200, 1460, null);
            moleType = 2;
        }
        else {
            canvas.drawBitmap(mole1, 200, 1460, null);
            moleType = 1;
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
        paint.setColor(Color.WHITE);
        canvas.drawBitmap(b, 435,775, paint);
    }

    private void regularMode(Canvas canvas){
        canvas.drawColor(Color.GREEN);
        Random r = new Random();
        int rand = r.nextInt(9);
        Bitmap temp;

        int ctr = 0;
        //before adding another random mole on screen, check if number of existing moles is less than 3
        if(holes.get(rand).getCurrentStage() == 1 && finish == true) { //if no mole in hole
            finish = false;
            currHole = rand;
            update(2, rand);
            holes.get(rand).setDirection("up");
        }


        //up and down movements
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

//    private void updateWhacko(int num){
//        Bitmap bmp;
//        if(num%2 == 0)
//            bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage2);
//        else
//            bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.stage1);
//    }

    private boolean clickOnBitmap(Hole hole, MotionEvent event) {
        Bitmap bmp = hole.getBmp();
        float xEnd = hole.getX() + bmp.getWidth();
        float yEnd = hole.getY() + bmp.getHeight();


        if ((event.getX() >= hole.getX() && event.getX() <= xEnd+20)
                && (event.getY() >= hole.getY() && event.getY() <= yEnd+20) ) {
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
        if ((holes.get(currHole).getCurrentStage() == 4 || holes.get(currHole).getCurrentStage() == 3) && finish == false) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (clickOnBitmap(holes.get(currHole), event)) {
                        whacks = whacks + 1;
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

    public void addWhacks(){
        whacks++;
    }

}

