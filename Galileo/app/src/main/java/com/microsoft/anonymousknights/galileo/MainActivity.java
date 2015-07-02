package com.microsoft.anonymousknights.galileo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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

import java.util.concurrent.ConcurrentLinkedDeque;

import T9.T9;
import  Contacts.*;


public class MainActivity extends AppCompatActivity{

    private static final int MY_DATA_CHECK_CODE = 1234;
    ConcurrentLinkedDeque<Touch> SenseDataList;
    Vibrator vibrator;
    TextToSpeech mTts;
    T9 T9Dictionary;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SenseDataList = new ConcurrentLinkedDeque<Touch>();
        vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        T9Dictionary = new T9();
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
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
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

                        new RequestTask().execute();
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
    }

    public void retrievePositions()
    {
        TextView textView = (TextView) findViewById(R.id.TextView5);
        AppConstants.TEXTVIEW_WIDTH = textView.getWidth();
        AppConstants.TEXTVIEW_HEIGHT = textView.getHeight();
        int[] location = new int[2];

        textView = (TextView) findViewById(R.id.TextView1);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW1_POSITION_X = location[0];
        AppConstants.TEXTVIEW1_POSITION_Y = location[1];

        textView = (TextView) findViewById(R.id.TextView2);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW2_POSITION_X = location[0];
        AppConstants.TEXTVIEW2_POSITION_Y = location[1];

        textView = (TextView) findViewById(R.id.TextView3);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW3_POSITION_X = location[0];
        AppConstants.TEXTVIEW3_POSITION_Y = location[1];

        textView = (TextView) findViewById(R.id.TextView4);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW4_POSITION_X = location[0];
        AppConstants.TEXTVIEW4_POSITION_Y = location[1];

        textView = (TextView) findViewById(R.id.TextView5);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW5_POSITION_X = location[0];
        AppConstants.TEXTVIEW5_POSITION_Y = location[1];

        textView = (TextView) findViewById(R.id.TextView6);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW6_POSITION_X = location[0];
        AppConstants.TEXTVIEW6_POSITION_Y = location[1];

        textView = (TextView) findViewById(R.id.TextView7);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW7_POSITION_X = location[0];
        AppConstants.TEXTVIEW7_POSITION_Y = location[1];

        textView = (TextView) findViewById(R.id.TextView8);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW8_POSITION_X = location[0];
        AppConstants.TEXTVIEW8_POSITION_Y = location[1];

        textView = (TextView) findViewById(R.id.TextView9);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW9_POSITION_X = location[0];
        AppConstants.TEXTVIEW9_POSITION_Y = location[1];

        textView = (TextView) findViewById(R.id.TextViewAsterisk);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW_ASHTERISK_POSITION_X = location[0];
        AppConstants.TEXTVIEW_ASHTERISK_POSITION_Y = location[1];

        textView = (TextView) findViewById(R.id.TextView0);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW0_POSITION_X = location[0];
        AppConstants.TEXTVIEW0_POSITION_Y = location[1];

        textView = (TextView) findViewById(R.id.TextViewSharp);
        textView.getLocationOnScreen(location);
        AppConstants.TEXTVIEW_SHARP_POSITION_X = location[0];
        AppConstants.TEXTVIEW_SHARP_POSITION_Y = location[1];

        Log.d("galileo_mytag", AppConstants.TEXTVIEW5_POSITION_X + "--------------" + AppConstants.TEXTVIEW5_POSITION_Y);
    }

    public void setAppConstantsPositions()
    {

    }

    class RequestTask extends AsyncTask<Void, Void, Void> {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected Void doInBackground(Void... v) {
            Log.d("galileo_mytag", "BACKGROUNDddddddddddddd");
            boolean moved, fiveFoundFlag = false;
            SenseDataList.clear();
            int currentAppStatus = AppStatus.searchingFor5;
            while(true) {
                if(!SenseDataList.isEmpty()) {
                    Log.d("galileo_mytag", "LIST NOT EMPTY");
                    if (SenseDataList.peek().type == MotionEvent.ACTION_UP || currentAppStatus == AppStatus.searchingFor5) {
                        Log.d("galileo_mytag", "ACTION UP");
                        moved = false;
                        Touch end = SenseDataList.getFirst();
                        Touch start = SenseDataList.getLast();
                        if(SenseDataList.size() > 5)
                            moved = true;
                        SenseDataList.clear();
                        if(currentAppStatus == AppStatus.searchingFor5) {
                            currentAppStatus = ActionIdentifier.searchingForFive(end, vibrator, mTts);
                            fiveFoundFlag = false;
                        }
                        else if(!fiveFoundFlag)
                        {
                            fiveFoundFlag = true;
                        }
                        else
                            currentAppStatus = ActionIdentifier.IdentifyAction(start, end, moved, currentAppStatus, vibrator, mTts, T9Dictionary, getApplicationContext());
                    }
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //Do anything with response..
        }
    }
}
