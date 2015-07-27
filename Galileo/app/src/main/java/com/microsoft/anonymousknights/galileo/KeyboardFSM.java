package com.microsoft.anonymousknights.galileo;

import android.speech.tts.TextToSpeech;
import android.util.Log;

import T9.T9;

/**
 * Created by sam on 7/27/2015.
 */
public class KeyboardFSM {

    private static int PageStatus;
    private static T9 T9Dictioary;

    public static void FSM(int nextAction)
    {
        if(PageStatus == AppStatus.searchingFor5)
        {

        }
        else
        {
            switch(nextAction)
            {
                case 1: //UP: start search for 5
                    PageStatus = AppStatus.searchingFor5;
                    break;
                case 2: //DOWN: delete all data entered
                        PageStatus = AppStatus.enteringNumbers;
                        T9Dictioary.clear();
                        break;
                case 3: //LEFT: delete one last digit
                        speech.speak("BACK", TextToSpeech.QUEUE_FLUSH, null);
                        //T9 update
                    break;
                case 4: //RIGHT: Announce to call
                    //clear all
                    if(currentAppStatus != AppStatus.searchingFor5) {
                        if (T9Dictioary.getDictionary().getHead().getSubTreeSize() == 0) {
                            speech.speak("NO CONTACT FOUND. CALLING DIALED NUMBER", TextToSpeech.QUEUE_FLUSH, null);
                            Call("09800160757", context);
                        }
                        if (T9Dictioary.getDictionary().getHead().getSubTreeSize() < 9) {
                            list = T9Dictioary.traverseDictionary();
                            speech.speak("ANNOUNCING", TextToSpeech.QUEUE_FLUSH, null);
                            for (int i = 0; i < list.size(); i++) {
                                speech.speak("PRESS " + (i + 1) + " FOR " + list.get(i).getName(), TextToSpeech.QUEUE_ADD, null);
                            }
                        } else {
                            speech.speak("TOO MANY RESULTS", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                    break;
            }
        }
        else if(start.timestamp - end.timestamp < LongPressThresholdTime)
    {
        //Single Click
        char clickedNumber = getClickedNumber(start, end);
        if(currentAppStatus == AppStatus.enteringNumbers)
        {
            //Next number
            Log.d("IdentifyAction: ", "TAPPPPPEDDDD ");
            int count = T9Dictioary.filter(clickedNumber);
            speech.speak(count + " RESULTS", TextToSpeech.QUEUE_FLUSH, null);
        }
        else if(currentAppStatus == AppStatus.announcingResults)
        {
            //Call
            Log.d("IdentifyAction: ", "ANNOUNCING RESULTS CALLING");
            Call(list.get(clickedNumber-'0'-1).getNumber(), context);
        }
    }
        }
    }
}
