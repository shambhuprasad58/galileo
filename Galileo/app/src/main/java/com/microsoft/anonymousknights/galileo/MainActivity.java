package com.microsoft.anonymousknights.galileo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import KeyboardPlus.Keyboard;
import Shaker.ShakeListener;
import T9.T9;
import  Contacts.*;

public class MainActivity extends AppCompatActivity{


    BlockingDeque<Touch> SenseDataList;
    BlockingQueue<ActionData> ActionDataList;
    Vibrator vibrator;
    //TextToSpeech mTts;
    T9 T9ContactDictionary;
    T9 T9WordDictionary;
    ShakeListener mShaker;
    Keyboard kpp;
    Context mContext;
    private static final int MY_DATA_CHECK_CODE = 1234;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SenseDataList = new LinkedBlockingDeque<>();
        ActionDataList = new LinkedBlockingDeque<>();
        vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        mContext = this;
        T9ContactDictionary = new T9();

        //T9WordDictionary = new T9();
        try {
            File fl = new File(getApplicationContext().getCacheDir(), "crap");
            FileInputStream fi = new FileInputStream(fl);
            ObjectInputStream in = new ObjectInputStream(fi);
            T9WordDictionary = (T9)in.readObject();
            T9WordDictionary.clear();
            in.close();
            fi.close();
        }
        catch (Exception e)
        {
            //TODO: Create Dictionary
            T9WordDictionary = new T9();
            try {
                InputStream in = this.getAssets().open("wordDict.txt");
                String tmpstring;
                int tmpx = 0;
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                do {
                    tmpstring = r.readLine();
                    if(tmpstring != null)
                    {
                        new ContactData(tmpstring, null, T9WordDictionary);
                        
                        if((tmpx/100) != ((tmpx + 1)/100))
                        {
                            Log.d("Dictionary Load:", " " + tmpx);
                        }
                        tmpx++;
                    }
                }while (tmpstring != null);
                //System.out.println(tmpstring);
            }
            catch (IOException e2)
            {
                Log.d("error:", "Unable to read resource file.");
            }

            //Read keyboard file
            try {
                File fl = new File(getApplicationContext().getCacheDir(), "keyboard++");
                FileInputStream fi = new FileInputStream(fl);
                ObjectInputStream in = new ObjectInputStream(fi);
                AppConstants.keyboard = (Keyboard)in.readObject();
                in.close();
                fi.close();
            }
            catch (Exception noKeyPPCache)
            {
                kpp = new Keyboard(12);
            }
        }
        Contacts allPhoneContacts = new Contacts(this);
        Log.d("galileo_mytag", "Contacts class created");
        allPhoneContacts.fetchList(T9ContactDictionary);

        LinearLayout baselayout = (LinearLayout) findViewById(R.id.base_layout);
        //baselayout.getX();

        baselayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.e("galileo_mytag" + motionEvent.getAction(),
                            String.valueOf(motionEvent.getX()) + "x" + String.valueOf(motionEvent.getY()) + "xxxx" + motionEvent.getRawX() + "x" + motionEvent.getRawY());
                    SenseDataList.addFirst(new Touch(motionEvent.getRawX(), motionEvent.getRawY(), motionEvent.getAction(), System.currentTimeMillis()));
                }
                return true;
            }
        });

        mShaker = new ShakeListener(this);
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {
                //vibrator.vibrate(100);
                if (AppConstants.speech != null) {
                    AppConstants.speech.speak("Going to Home Page. Thank You.", TextToSpeech.QUEUE_FLUSH, null);
                    //TODO: Dump word list
                    try {
                        FileOutputStream fo = new FileOutputStream(new File(getApplicationContext().getCacheDir(), "cachefile"));
                        ObjectOutputStream oout = new ObjectOutputStream(fo);
                        oout.writeObject(T9WordDictionary);
                        oout.close();
                        fo.close();
                        fo = new FileOutputStream(new File(getApplicationContext().getCacheDir(), "keyboard++"));
                        oout = new ObjectOutputStream(fo);
                        oout.writeObject(AppConstants.keyboard);
                        oout.close();
                        fo.close();
                    } catch (Exception e) {
                        Log.d("Galileo", "Warning: Could not save word dictionary");
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                finish();
                //System.exit(0);
            }
        });
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

//        retrievePositions();
//        createThreads();
    }

    @Override
    public void onResume()
    {
        mShaker.resume();
        super.onResume();
    }
    @Override
    public void onPause()
    {
        mShaker.pause();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected  void onDestroy()
    {
        super.onDestroy();
        try {
            FileOutputStream fo = new FileOutputStream(new File(getApplicationContext().getCacheDir(), "cachefile"));
            ObjectOutputStream oout = new ObjectOutputStream(fo);
            oout.writeObject(T9WordDictionary);
            oout.close();
            fo.close();
            fo = new FileOutputStream(new File(getApplicationContext().getCacheDir(), "keyboard++"));
            oout = new ObjectOutputStream(fo);
            oout.writeObject(AppConstants.keyboard);
            oout.close();
            fo.close();
        } catch (Exception e) {
            Log.d("Galileo", "Warning: Could not save word dictionary");
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        AppConstants.speech.speak("LETS " + AppConstants.currentActionSpeech,
                TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
                null);
        AppConstants.speech.speak("SEARCH 5", TextToSpeech.QUEUE_FLUSH, null);
        createThreads();
        retrievePositions();
    }

    public void createThreads()
    {
        Thread ActionThread = new Thread() {
            @Override
            public void run() {
                try {
                    ActionIdentifier.IdentifyAction(SenseDataList, ActionDataList, vibrator, AppConstants.speech);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        ActionThread.start();
        ActionThread.getState().toString();

        Thread FSMThread = new Thread() {
            @Override
            public void run() {
                try {
                    KeyboardFSM.FSM(ActionDataList, vibrator, AppConstants.speech, T9ContactDictionary, T9WordDictionary, mContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        FSMThread.start();
    }

    public void retrievePositions()
    {
        TextView textView = (TextView) findViewById(R.id.TextView5);
        AppConstants.TEXTVIEW_WIDTH = textView.getWidth();
        AppConstants.TEXTVIEW_HEIGHT = textView.getHeight();
        AppConstants.MIN_KEY_RADIUS = (AppConstants.TEXTVIEW_HEIGHT * AppConstants.TEXTVIEW_HEIGHT +
                AppConstants.TEXTVIEW_WIDTH * AppConstants.TEXTVIEW_WIDTH) / 4;
        int[] location = new int[2];

        textView = (TextView) findViewById(R.id.TextView1);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW_POSITION_X[1] = location[0];
        AppConstants.TEXTVIEW_POSITION_Y[1] = location[1];

        textView = (TextView) findViewById(R.id.TextView2);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW_POSITION_X[2] = location[0];
        AppConstants.TEXTVIEW_POSITION_Y[2] = location[1];

        textView = (TextView) findViewById(R.id.TextView3);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW_POSITION_X[3] = location[0];
        AppConstants.TEXTVIEW_POSITION_Y[3] = location[1];

        textView = (TextView) findViewById(R.id.TextView4);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW_POSITION_X[4] = location[0];
        AppConstants.TEXTVIEW_POSITION_Y[4] = location[1];

        textView = (TextView) findViewById(R.id.TextView5);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW_POSITION_X[5] = location[0];
        AppConstants.TEXTVIEW_POSITION_Y[5] = location[1];

        textView = (TextView) findViewById(R.id.TextView6);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW_POSITION_X[6] = location[0];
        AppConstants.TEXTVIEW_POSITION_Y[6] = location[1];

        textView = (TextView) findViewById(R.id.TextView7);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW_POSITION_X[7] = location[0];
        AppConstants.TEXTVIEW_POSITION_Y[7] = location[1];

        textView = (TextView) findViewById(R.id.TextView8);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW_POSITION_X[8] = location[0];
        AppConstants.TEXTVIEW_POSITION_Y[8] = location[1];

        textView = (TextView) findViewById(R.id.TextView9);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW_POSITION_X[9] = location[0];
        AppConstants.TEXTVIEW_POSITION_Y[9] = location[1];

        textView = (TextView) findViewById(R.id.TextViewAsterisk);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW_POSITION_X[10] = location[0];
        AppConstants.TEXTVIEW_POSITION_Y[10] = location[1];

        textView = (TextView) findViewById(R.id.TextView0);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW_POSITION_X[0] = location[0];
        AppConstants.TEXTVIEW_POSITION_Y[0] = location[1];

        textView = (TextView) findViewById(R.id.TextViewSharp);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW_POSITION_X[11] = location[0];
        AppConstants.TEXTVIEW_POSITION_Y[11] = location[1];

        AppConstants.keyboard = new Keyboard(12);

        Log.d("galileo_mytag", AppConstants.TEXTVIEW_POSITION_X[5] + "--------------" + AppConstants.TEXTVIEW_POSITION_Y[5]);
    }
//
//    public void setAppConstantsPositions()
//    {
//
//    }
//
//    class RequestTask extends AsyncTask<Void, Void, Void> {
//        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//        @Override
//        protected Void doInBackground(Void... v) {
//            Log.d("galileo_mytag", "BACKGROUNDddddddddddddd");
//            boolean moved, fiveFoundFlag = false;
//            SenseDataList.clear();
//            int currentAppStatus = AppStatus.searchingFor5;
//            while(true) {
//                if(!SenseDataList.isEmpty()) {
//                    Log.d("galileo_mytag", "LIST NOT EMPTY");
//                    if (SenseDataList.peek().type == MotionEvent.ACTION_UP || currentAppStatus == AppStatus.searchingFor5) {
//                        Log.d("galileo_mytag", "ACTION UP");
//                        moved = false;
//                        Touch end = SenseDataList.getFirst();
//                        Touch start = SenseDataList.getLast();
//                        if(SenseDataList.size() > 5)
//                            moved = true;
//                        SenseDataList.clear();
//                        if(currentAppStatus == AppStatus.searchingFor5) {
//                            currentAppStatus = ActionIdentifier.searchingForFive(end, vibrator, mTts);
//                            fiveFoundFlag = false;
//                        }
//                        else if(!fiveFoundFlag)
//                        {
//                            fiveFoundFlag = true;
//                        }
//                        else
//                            currentAppStatus = ActionIdentifier.IdentifyAction(start, end, moved, currentAppStatus, vibrator, mTts, T9ContactDictionary, getApplicationContext());
//                    }
//                }
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            //return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            //Do anything with response..
//        }
//    }
}
