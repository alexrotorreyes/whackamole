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
    private boolean regularMode = false;
    private boolean whackoMode = false; //if true, whackoMode will be shown
    private boolean mainMenu = true;   //if true, mainMenu will be shown
    private boolean gameStart = false; //if true, game has started
    private int moleType = 1;
    private SensorManager sensorManager;
    private float acelVal;      //CURRENT ACCELERATION VALUE AND GRAVITY
    private float acelLast;     //LAST ACCELERATION VALUE AND GRAVITY
    private float shake;        //ACCELERATION VALUE DIFFER FROM GRAVITY
    private int instructionsBtnX = 0;
    private int instructionsBtnY = 0;
    private int startBtnX = 0;
    private int startBtnY = 0;
    private int highScore = 0;
    private Bitmap startBtn;
    private Bitmap instructionsBtn;
//    private Runnable runnable;

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

    }

    private final SensorEventListener sensorListener = new SensorEventListener() { //handles shaking
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

        if(mainMenu == true && whackoMode == false && regularMode == false) {
            gameStart = false;
            mainMenu();

        }else if(regularMode == true && mainMenu == false && whackoMode == false){
            gameStart = true;
            regularMode(c);
        }
        else if(whackoMode == true && mainMenu == false && regularMode == false){
            whackoMode();
        }

    }

    protected void reDraw(String mode) {        //sets boolean value of mode to "true" before onDraw is called
//        this.invalidate();
        switch(mode){
            case "MAIN MENU":   mainMenu = true;
                                whackoMode = false;
                                regularMode = false;
                                Toast.makeText(getContext().getApplicationContext(), mode, Toast.LENGTH_SHORT).show();
                                break;
            case "WHACKO MODE": whackoMode = true;
                                mainMenu = false;
                                regularMode = false;
                                Toast.makeText(getContext().getApplicationContext(), mode, Toast.LENGTH_SHORT).show();
                                break;
            case "REGULAR MODE": regularMode = true;
                                    whackoMode = false;
                                  mainMenu = false;
                Toast.makeText(getContext().getApplicationContext(), mode, Toast.LENGTH_SHORT).show();
                                break;
        }
        this.invalidate();

    }

    public void reset(){
        //handler.removeCallbacksAndMessages(null);
        handler.removeMessages(0);

        for(int i = 0; i < holes.size(); i++){
            holes.get(i).setCurrentStage(1);
            holes.get(i).setDirection("up");
        //    Toast.makeText(getContext().getApplicationContext(), "entered LOOOOOOOOOOOOOOP RESET: " + i, Toast.LENGTH_SHORT).show();
        //   Toast.makeText(getContext().getApplicationContext(), "LOOOOP CURR STAGE: " + holes.get(i).getCurrentStage(), Toast.LENGTH_SHORT).show();
        }

        whacks = 0;
    }

    public void mainMenu(){
        canvas.drawColor(Color.DKGRAY);
        Bitmap title = BitmapFactory.decodeResource(getResources(), R.mipmap.title);
        startBtn = BitmapFactory.decodeResource(getResources(), R.mipmap.startbtn);
        instructionsBtn = BitmapFactory.decodeResource(getResources(), R.mipmap.instructionsbtn);

        canvas.drawBitmap(title, 25, 130, null);
        canvas.drawBitmap(startBtn, 330, 1100, null);
        startBtnX = 330;
        startBtnY = 1100;
        canvas.drawBitmap(instructionsBtn, 250, 1350, null);
        instructionsBtnX = 250;
        instructionsBtnY = 1350;

        Paint paint = new Paint();
        Bitmap b = Bitmap.createBitmap(200, 200, Bitmap.Config.ALPHA_8);
        Canvas c = new Canvas(b);
        c.drawRect(0, 0, 200, 200, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        paint.setTextSize(30);
        paint.setTextScaleX(1.f);
        paint.setAlpha(0);
        paint.setAntiAlias(true);
        c.drawText("HIGH SCORE:", 15, 40, paint);
        c.drawText(String.valueOf(highScore), 20, 120, paint);
        paint.setColor(Color.WHITE);
        canvas.drawBitmap(b, 435,700, paint);
    }

    public void whackoMode(){
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                if(whacks > highScore){
                    highScore = whacks;
                }

                reDraw("MAIN MENU");
            }
        }, 5000);

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

        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                sensorManager = (SensorManager) getContext().getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
                sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

                acelVal = SensorManager.GRAVITY_EARTH;
                acelLast = SensorManager.GRAVITY_EARTH;
                shake = 0.00f;
                reDraw("WHACKO MODE");
            }
        }, 5000);

        Random rand = new Random();
        int random = rand.nextInt(9);

        if(holes.get(random).getCurrentStage() == 1 && finish == true) { //if no mole in hole
            finish = false;
            currHole = random;
            update(2, random);
            holes.get(random).setDirection("up");
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

    private void update(int stage, int offset){ //changes the photo stage of the mole
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


    private boolean clickOnBitmap(Hole hole, MotionEvent event) { //if a certain mole was clicked
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

    private String clickOnBtn(Bitmap bmp, MotionEvent event){ //if a certain button was clicked
        float startBtnXEnd = startBtnX + bmp.getWidth();
        float startBtnYEnd = startBtnY + bmp.getHeight();
        float instructionsBtnXEnd = instructionsBtnX + bmp.getWidth();
        float instructionsBtnYEnd = instructionsBtnY + bmp.getHeight();

        if ((event.getX() >= startBtnX && event.getX() <= startBtnXEnd) //removed the "+20"
                && (event.getY() >= startBtnY && event.getY() <= startBtnYEnd)) {
            int pixX = (int) (event.getX() - startBtnX);
            int pixY = (int) (event.getY() - startBtnY);
            if (!(bmp.getPixel(pixX, pixY) == 0)) {
                return "START";
            }
        }

        if ((event.getX() >= instructionsBtnX && event.getX() <= instructionsBtnXEnd+20)
                && (event.getY() >= instructionsBtnY && event.getY() <= instructionsBtnYEnd+20) ) {
            int pixX = (int) (event.getX() - instructionsBtnX);
            int pixY = (int) (event.getY() - instructionsBtnY);
            if (!(bmp.getPixel(pixX, pixY) == 0)) {
                return "INSTRUCTIONS";
            }
        }

        return "NOTHING";
    }


    public boolean onTouchEvent(MotionEvent event) { //if the user clicked any area of the screen
        if ((holes.get(currHole).getCurrentStage() == 4 || holes.get(currHole).getCurrentStage() == 3) && finish == false && mainMenu == false) {
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
        if ((mainMenu == true && whackoMode == false && regularMode == false && gameStart == false)){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (clickOnBtn(startBtn, event).equals("START")) {
                        currHole = 0;
                        reset();
                        reDraw("REGULAR MODE");
                    }else if(clickOnBtn(instructionsBtn, event).equals("INSTRUCTIONS")){
                        //do instructions actions here
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

    public void addWhacks(){ //adds whacks to whack counter
        whacks++;
    }

}

