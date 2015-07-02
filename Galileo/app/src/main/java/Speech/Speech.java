package Speech;

import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by sprasa on 7/2/2015.
 */
public class Speech{
    TextToSpeech tts;
    public void speakOut(String toSpeak) {

        String text = toSpeak;
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,null);
        //tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public  Speech(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.d("TTS-----------", "This Language is not supported");
            } else {
                //btnSpeak.setEnabled(true);
                speakOut("Hello. Voice output started. ");
            }

        } else {
            Log.e("TTS--------------", "Initilization Failed!");
        }
    }


    public void toEndSpeech(){
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }


}
