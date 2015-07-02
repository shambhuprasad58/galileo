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

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

import Speech.Speech;
import T9.T9;



public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private static final int MY_DATA_CHECK_CODE = 1234;
    ConcurrentLinkedDeque<Touch> SenseDataList;
    Vibrator vibrator;
    Speech speech;
    TextToSpeech mTts;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // define the RelativeLayout layout parameters.
        //        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
        //                RelativeLayout.LayoutParams.MATCH_PARENT,
        //                RelativeLayout.LayoutParams.MATCH_PARENT);
        //
        //        setContentView(new GenerateInterface(this).createGridLayout(), relativeLayoutParams);
        setContentView(R.layout.activity_main);
        SenseDataList = new ConcurrentLinkedDeque<Touch>();
        vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        LinearLayout baselayout = (LinearLayout) findViewById(R.id.base_layout);

        baselayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.e("Touch : " + motionEvent.getAction(),
                            String.valueOf(motionEvent.getX()) + "x" + String.valueOf(motionEvent.getY()));
                    SenseDataList.addFirst(new Touch(motionEvent.getX(), motionEvent.getY(), motionEvent.getAction(), System.currentTimeMillis()));
                }
                return true;
            }
        });

//        baselayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e("Click: ", "CLICKEDDDDDDDDD");
//                speech.speakOut("BHAK SAALA");
//            }
//        });
//	T9 t9 = new T9();
//        t9.clear();
//        int x = t9.filter('3');
//        Log.d("AAAAAAAAAAAAAAAAAAAAA", Integer.toString(x));

        // Fire off an intent to check if a TTS engine is installed
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
    }

    /**
     * This is the callback from the TTS engine check, if a TTS is installed we
     * create a new TTS instance (which in turn calls onInit), if not then we will
     * create an intent to go off and install a TTS engine
     * @param requestCode int Request code returned from the check for TTS engine.
     * @param resultCode int Result code returned from the check for TTS engine.
     * @param data Intent Intent returned from the TTS check.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
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

    @Override
    protected void onDestroy() {
        speech.toEndSpeech();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Executed when a new TTS is instantiated. Some static text is spoken via TTS here.
     * @param i
     */
    public void onInit(int i)
    {
        mTts.speak("Hello folks, welcome to my little demo on Text To Speech.",
                TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
                null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
/*
    @Override
    public void onInit(int status) {
        Log.d("SPEEEEECH", "INITIALIZINGGGGGGGGGGGG");
        speech = new Speech(status);
    }
*/
    class RequestTask extends AsyncTask<Void, Void, Void> {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected Void doInBackground(Void... v) {
            Log.d("ASYNC TASK: ", "BACKGROUNDddddddddddddd");
            long previousClickTime = 0;
            boolean moved;
            int currentAppStatus = 0;
            while(true) {
                if(!SenseDataList.isEmpty()) {
                    Log.d("ASYNC TASK: ", "LIST NOT EMPTY");
                    if (SenseDataList.peek().type == MotionEvent.ACTION_UP) {
                        Log.d("ASYNC TASK: ", "ACTION UP");
                        moved = false;
                        Touch end = SenseDataList.getFirst();
                        Touch start = SenseDataList.getLast();
                        //if(SenseDataList.size() > 4)
                        moved = true;
                        SenseDataList.clear();
                        ActionIdentifier.IdentifyAction(start, end, previousClickTime, moved, currentAppStatus, vibrator, mTts);
                        previousClickTime = end.timestamp;
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
