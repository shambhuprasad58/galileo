package com.microsoft.anonymousknights.galileo;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import Shaker.ShakeListener;


public class IntentSelectActivity extends AppCompatActivity {

    ShakeListener mShaker;
    Touch DOWN = null;
    private static final int MY_DATA_CHECK_CODE = 1234;
    private static final int MAIN_ACTIVITY_CHECK_CODE = 1235;
    private static boolean MAIN_ACTIVITY_BACK = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_select);
        LinearLayout baselayout = (LinearLayout) findViewById(R.id.intent_select_base_layout);
        baselayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    Log.e("galileo_mytag" + motionEvent.getAction(),
                            String.valueOf(motionEvent.getX()) + "x" + String.valueOf(motionEvent.getY()) + "xxxx" + motionEvent.getRawX() + "x" + motionEvent.getRawY());
                    DOWN = new Touch(motionEvent.getRawX(), motionEvent.getRawY(), motionEvent.getAction(), System.currentTimeMillis());
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    Log.e("galileo_mytag" + motionEvent.getAction(),
                            String.valueOf(motionEvent.getX()) + "x" + String.valueOf(motionEvent.getY()) + "xxxx" + motionEvent.getRawX() + "x" + motionEvent.getRawY());
                    if(DOWN == null)
                        return true;
                    Touch UP = new Touch(motionEvent.getRawX(), motionEvent.getRawY(), motionEvent.getAction(), System.currentTimeMillis());
                    int action = ActionIdentifier.getMoveDirection(DOWN, UP);
                    switch(action)
                    {
                        case AppConstants.SwipeDirectionRight: AppConstants.CurrentAction = AppConstants.CallAction; AppConstants.currentActionSpeech = "CALL"; break;
                        case AppConstants.SwipeDirectionLeft: AppConstants.CurrentAction = AppConstants.SMSAction; AppConstants.currentActionSpeech = "SMS"; break;
                        case AppConstants.SwipeDirectionUp:  AppConstants.speech.speak("Swipe up is invalid input.", TextToSpeech.QUEUE_FLUSH, null); //Invalid input
                                                                break;
                        case AppConstants.SwipeDirectionDown: AppConstants.CurrentAction = AppConstants.EmailAction; AppConstants.currentActionSpeech = "EMAIL"; break;
                    }
                    ActionIdentifier.searchingForFiveStateVariable = true;
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivityForResult(intent, MAIN_ACTIVITY_CHECK_CODE);
                }
                return true;
            }
        });

        mShaker = new ShakeListener(this);
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener () {
            public void onShake()
            {
                //vibrator.vibrate(100);
                if(MAIN_ACTIVITY_BACK)
                {
                    Log.d("galileo_mytag", "IGNORING CLOSE APP");
                    MAIN_ACTIVITY_BACK = false;
                    mShaker.mShakeCount = 0;
                    return;
                }
                if(AppConstants.speech != null)
                {
                    AppConstants.speech.speak("Closing Application. Thank You.", TextToSpeech.QUEUE_FLUSH, null);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_intent_select, menu);
        return true;
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

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == MY_DATA_CHECK_CODE)
        {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
            {
                // success, create the TTS instance
                AppConstants.speech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        Log.d("galileo_mytag", "xxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                        AppConstants.speech.speak("Hello folks, welcome to galileo.",
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
        else if(requestCode == MAIN_ACTIVITY_CHECK_CODE)
        {
            Log.d("galileo_mytag", "MAIN_ACTIVITY_BACK: TRUE");
            MAIN_ACTIVITY_BACK = true;
        }
    }
}
