package com.microsoft.anonymousknights.galileo;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

import Contacts.Contacts;
import Contacts.ContactData;

import T9.T9;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    //public class AndroidTextToSpeechActivity

    private TextToSpeech tts;
    private Button btnSpeak;
    private EditText txtText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        T9 t9 = new T9();
//        t9.addToDictionary("23", "ad");
//        t9.addToDictionary("23", "be");
//        t9.addToDictionary("24",  "ag");
//        t9.addToDictionary("34", "dg");
//        t9.clear();
//        int x = t9.filter('3');
//        Log.d("AAAAAAAAAAAAAAAAAAAAA", Integer.toString(x));


        Log.d("MY MSG.............", "Hereeeeeeeeeeeeeeeeeee");
        System.out.print("asdghawsgdhasdghgasd----------------");
        Contacts allPhoneContacts = new Contacts(this);
        Log.d("MY MSG.............", "Contacts class created");
        allPhoneContacts.fetchList(t9);
        t9.clear();
        int x = t9.filter('7');
        Log.d("AAAAAAAAAAAAAAAAAAAAA", Integer.toString(x));
        //tts = new TextToSpeech(this,this);
//        txtText = (EditText) findViewById(R.id.speakerText);
//        btnSpeak = (Button) findViewById(R.id.btnSpeak);
//         final String txtText = "Dying is the day worth living for !";
//         speakOut(txtText);
//        btnSpeak.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//            speakOut();
//            }
//
//            });
    }
    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.d("TTS-----------", "This Language is not supported");
            } else {
                btnSpeak.setEnabled(true);
                speakOut("Hello. Voice output started. 1 2 3");
            }

        } else {
            Log.e("TTS--------------", "Initilization Failed!");
        }
    }

    private void speakOut() {

        String text = txtText.getText().toString();
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,null);
        //tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    private void speakOut(String toSpeak) {

        String text = toSpeak;
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,null);
        //tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
