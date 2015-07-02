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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.ConcurrentLinkedDeque;

import T9.T9;


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
        LinearLayout baselayout = (LinearLayout) findViewById(R.id.base_layout);
        baselayout.getX();

        baselayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.e("Touch : " + motionEvent.getAction(),
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
        TextView textView5 = (TextView) findViewById(R.id.TextView5);
        int[] location = new int[2];
        textView5.getLocationOnScreen(location);
        AppConstants.TEXTVIEW5_POSITION_X = location[0];
        AppConstants.TEXTVIEW5_POSITION_Y = location[1];
        AppConstants.TEXTVIEW5_WIDTH = textView5.getWidth();
        AppConstants.TEXTVIEW5_HEIGHT = textView5.getHeight();
        Log.d("TEXTVIEW5 POSITION: ", AppConstants.TEXTVIEW5_POSITION_X + "--------------" + AppConstants.TEXTVIEW5_POSITION_Y);
        if (requestCode == MY_DATA_CHECK_CODE)
        {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
            {
                // success, create the TTS instance
                mTts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        Log.d("xxxxxxxxxxx", "xxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                        mTts.speak("Hello folks, welcome to my little demo on Text To Speech.",
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

    class RequestTask extends AsyncTask<Void, Void, Void> {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected Void doInBackground(Void... v) {
            Log.d("ASYNC TASK: ", "BACKGROUNDddddddddddddd");
            boolean moved;
            SenseDataList.clear();
            int currentAppStatus = AppStatus.enteringNumbers;
            while(true) {
                if(!SenseDataList.isEmpty()) {
                    Log.d("ASYNC TASK: ", "LIST NOT EMPTY");
                    if (SenseDataList.peek().type == MotionEvent.ACTION_UP) {
                        Log.d("ASYNC TASK: ", "ACTION UP");
                        moved = false;
                        Touch end = SenseDataList.getFirst();
                        Touch start = SenseDataList.getLast();
                        if(SenseDataList.size() > 5)
                            moved = true;
                        SenseDataList.clear();
                        if(currentAppStatus == AppStatus.searchingFor5)
                            currentAppStatus = ActionIdentifier.searchingForFive(end, vibrator, mTts);
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
