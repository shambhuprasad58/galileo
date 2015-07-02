package com.microsoft.anonymousknights.galileo;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import Contacts.ContactData;
import Speech.Speech;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import T9.T9;
import Vibrate.Vibrate;

/**
 * Created by sam on 7/2/2015.
 */
public class ActionIdentifier {
    private static final int LongPressThresholdTime = 200;
    private static final int MoveThresholdPos = 200;
    private static LinkedList<ContactData> list;
    @SuppressLint("NewApi")
    public static int IdentifyAction(Touch start, Touch end, boolean moved, int currentAppStatus, Vibrator vibrator, TextToSpeech speech, T9 T9Dictioary, Context context)
    {
        Log.d("IdentifyAction: ", "ENTEREDDDDDDDDDD");
        //speech.speak("SHHHHHH KOI HAI", TextToSpeech.QUEUE_FLUSH, null);

        //Vibrate vibrate = new Vibrate(vibrator);
        //vibrate.vibrate(4, 200, 50);

        if(Math.abs(start.pos_x - end.pos_x) > MoveThresholdPos || Math.abs(start.pos_y - end.pos_y) > MoveThresholdPos)
        {
            Log.d("IdentifyAction: ", "MOVEEEEEEEEEEEE");
            //Move Action
            int moveDirection = getMoveDirection(start, end);
            Log.d("IdentifyAction: ", "MOVE DIRECTION " + moveDirection);
            switch(moveDirection)
            {
                case 1: //UP: start search for 5
                    currentAppStatus = AppStatus.searchingFor5;
                    break;
                case 2: //DOWN: delete all data entered
                    Log.d("IdentifyAction: ", "SWIPE DOWN");
                    if(currentAppStatus != AppStatus.searchingFor5) {
                        currentAppStatus = AppStatus.enteringNumbers;
                        T9Dictioary.clear();
                    }
                    break;
                case 3: //LEFT: delete one last digit
                    if(currentAppStatus != AppStatus.searchingFor5)
                    {
                        //Delete last digit
                        speech.speak("BACK", TextToSpeech.QUEUE_FLUSH, null);
                        //T9 update
                    }
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
//        else
//        {
//            //Long Press
//            if(currentAppStatus == AppStatus.enteringNumbers)
//            {
//                currentAppStatus = AppStatus.announcingResults;
//                //announce Results
//            }
//            else if(currentAppStatus == AppStatus.announcingResults)
//            {
//                currentAppStatus = AppStatus.enteringNumbers;
//            }
//        }
        return currentAppStatus;
    }

    public static int getMoveDirection(Touch start, Touch end)
    {
        if(Math.abs(end.pos_x - start.pos_x) > Math.abs(end.pos_y - start.pos_y))
        {
            if(end.pos_x > start.pos_x)
                return 4;
            return 3;
        }
        if(end.pos_y > start.pos_y)
            return 1;
        return 2;

    }

    public static char getClickedNumber(Touch start, Touch end)
    {
        return '2';
    }

    public static void Call(String number, Context context)
    {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));
            context.startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {
            Log.e("Calling a Phone Number", "Call failed", activityException);
        }
    }

    public static int searchingForFive(Touch point, Vibrator vibrator, TextToSpeech speech)
    {
        if(point.pos_x > (AppConstants.TEXTVIEW5_POSITION_X + (AppConstants.TEXTVIEW5_WIDTH/4)) && point.pos_x < (AppConstants.TEXTVIEW5_POSITION_X + (3*AppConstants.TEXTVIEW5_WIDTH/4)) && point.pos_y > (AppConstants.TEXTVIEW5_POSITION_Y + (AppConstants.TEXTVIEW5_HEIGHT/4)) && point.pos_y < (AppConstants.TEXTVIEW5_POSITION_Y + (3*AppConstants.TEXTVIEW5_HEIGHT/4))) {
            speech.speak("5 FOUND. START TYPING", TextToSpeech.QUEUE_FLUSH, null);
            return AppStatus.enteringNumbers;
        }
        Vibrate vibrate = new Vibrate(vibrator);
        int strength = (int)(Math.abs(point.pos_x - (AppConstants.TEXTVIEW5_POSITION_X + AppConstants.TEXTVIEW5_WIDTH/2)) + Math.abs(point.pos_y - (AppConstants.TEXTVIEW5_POSITION_Y + AppConstants.TEXTVIEW5_HEIGHT/2)));
        vibrate.vibrate(1, 10, strength);
        return AppStatus.searchingFor5;
    }
}
