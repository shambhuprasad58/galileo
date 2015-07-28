package com.microsoft.anonymousknights.galileo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;

import Shaker.ShakeListener;
import T9.T9;
import  Contacts.*;
import KeyboardPlus.*;

public class MainActivity extends AppCompatActivity{

    private static final int MY_DATA_CHECK_CODE = 1234;
    BlockingDeque<Touch> SenseDataList;
    BlockingQueue<ActionData> ActionDataList;
    Vibrator vibrator;
    TextToSpeech mTts;
    T9 T9Dictionary;
    T9 wordDictionary;
    ShakeListener mShaker;
    Context mContext;


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
        T9Dictionary = new T9();

        //wordDictionary = new T9();
        try {
            File fl = new File(getApplicationContext().getCacheDir(), "crap");
            FileInputStream fi = new FileInputStream(fl);
            ObjectInputStream in = new ObjectInputStream(fi);
            wordDictionary = (T9)in.readObject();
            wordDictionary.clear();
            in.close();
            fi.close();
        }
        catch (Exception e)
        {
            //TODO: Create Dictionary
            wordDictionary = new T9();
            try {
                InputStream in = this.getAssets().open("wordDict.txt");
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                String tmpstring = r.readLine();
                System.out.println(tmpstring);
            }
            catch (IOException e2)
            {
                Log.d("error:", "Unable to read resource file.");
            }

        }

        //T9 wordList =
        int x = T9Dictionary.filter('1');
        T9Dictionary.clear();
        x = T9Dictionary.filter('3');

/*
        try{
            //File outputDir = getApplicationContext().getCacheDir();// context being the Activity pointer
            //File outputFile = File.createTempFile("test", "ser", outputDir);

            FileOutputStream fo = new FileOutputStream(new File(getApplicationContext().getCacheDir(), "cachefile"));
            ObjectOutputStream oout = new ObjectOutputStream(fo);
            oout.writeObject(T9Dictionary);
            oout.close();
            fo.close();

            File fl = new File(getApplicationContext().getCacheDir(), "cachefile");
            FileInputStream fi = new FileInputStream(fl);
            ObjectInputStream in = new ObjectInputStream(fi);
            T9 t2 = (T9)in.readObject();
            t2.clear();
            x = t2.filter('1');
            t2.clear();
            x = t2.filter('3');
            long x2 = fl.length();
            in.close();
            fi.close();
        }
        catch (Exception e)
        {
            Log.d("xxxxx", "Output not found.");
        }
*/

        Contacts allPhoneContacts = new Contacts(this);
        Log.d("galileo_mytag", "Contacts class created");
        allPhoneContacts.fetchList(T9Dictionary);

        LinearLayout baselayout = (LinearLayout) findViewById(R.id.base_layout);
        baselayout.getX();

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
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener () {
            public void onShake()
            {
                //vibrator.vibrate(100);
                if(mTts != null)
                {
                    mTts.speak("Closing Application. Thank You.", TextToSpeech.QUEUE_FLUSH, null);
                    //TODO: Dump word list
                    try {
                        FileOutputStream fo = new FileOutputStream(new File(getApplicationContext().getCacheDir(), "cachefile"));
                        ObjectOutputStream oout = new ObjectOutputStream(fo);
                        oout.writeObject(wordDictionary);
                        oout.close();
                        fo.close();
                    }
                    catch (Exception e)
                    {
                        Log.d("Galileo", "Warning: Could not save word dictionary");
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                finish();
                System.exit(0);
            }
        });

        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        retrievePositions();
        if (requestCode == MY_DATA_CHECK_CODE)
        {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
            {
                // success, create the TTS instance
                mTts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        Log.d("galileo_mytag", "xxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                        mTts.speak("Hello folks, welcome to galileo. LOCATE 5",
                                TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
                                null);

                        //new RequestTask().execute();
                    }
                });
            }
            else
            {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
        createThreads();
    }

    public void createThreads()
    {
        Thread ActionThread = new Thread() {
            @Override
            public void run() {
                try {
                    ActionIdentifier.IdentifyAction(SenseDataList, ActionDataList, vibrator, mTts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        ActionThread.start();

        Thread FSMThread = new Thread() {
            @Override
            public void run() {
                try {
                    KeyboardFSM.FSM(ActionDataList, vibrator, mTts, T9Dictionary, mContext);
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
        AppConstants.TEXTVIEW_POSITION_X[10] = location[1];

        textView = (TextView) findViewById(R.id.TextView0);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW_POSITION_X[0] = location[0];
        AppConstants.TEXTVIEW_POSITION_Y[0] = location[1];

        textView = (TextView) findViewById(R.id.TextViewSharp);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW_POSITION_X[11] = location[0];
        AppConstants.TEXTVIEW_POSITION_X[11] = location[1];

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
//                            currentAppStatus = ActionIdentifier.IdentifyAction(start, end, moved, currentAppStatus, vibrator, mTts, T9Dictionary, getApplicationContext());
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
