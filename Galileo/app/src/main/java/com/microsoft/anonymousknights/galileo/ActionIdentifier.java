package com.microsoft.anonymousknights.galileo;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;

import Contacts.ContactData;

import java.util.LinkedList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;

import T9.T9;
import Vibrate.Vibrate;

/**
 * Created by sam on 7/2/2015.
 */
public class ActionIdentifier {

    private static final int LongPressThresholdTime = 200;
    private static final int MoveThresholdPos = 200;
    private static boolean searchingForFive = true;

    public static void IdentifyAction(BlockingDeque<Touch> SenseDataList, BlockingQueue<ActionData> ActionDataList, Vibrator vibrator, TextToSpeech speech)
    {
        ActionData data;
        while(true)
        {
            Touch touch = SenseDataList.pop();
            if(searchingForFive)
            {
                searchingForFive(touch, vibrator, speech);
            }
            else
            {
                if(touch.type == MotionEvent.ACTION_UP) {
                    Touch end = touch;
                    Touch start = SenseDataList.pollLast();
                    SenseDataList.clear();
                    Log.d("IdentifyAction: ", "ENTEREDDDDDDDDDD");
                    data = new ActionData();
                    data.nextChar = '.';
                    if (Math.abs(start.pos_x - end.pos_x) > MoveThresholdPos || Math.abs(start.pos_y - end.pos_y) > MoveThresholdPos) {
                        Log.d("IdentifyAction: ", "MOVEEEEEEEEEEEE");
                        //Move Action
                        data.nextAction = getMoveDirection(start, end);

                    } else if (start.timestamp - end.timestamp < LongPressThresholdTime) {
                        //Single Click
                        data.nextChar = getClickedNumber(start, end);
                        data.nextAction = AppConstants.SingleClick;
                    }
                    else {
                        data.nextAction = AppConstants.LongPress;
                    }
                    ActionDataList.add(data);
                }
            }
        }
    }

    public static int getMoveDirection(Touch start, Touch end)
    {
        if(Math.abs(end.pos_x - start.pos_x) > Math.abs(end.pos_y - start.pos_y))
        {
            if(end.pos_x > start.pos_x)
                return AppConstants.SwipeDirectionRight;
            return AppConstants.SwipeDirectionLeft;
        }
        if(end.pos_y > start.pos_y)
            return AppConstants.SwipeDirectionDown;
        return AppConstants.SwipeDirectionUp;

    }


    public static void searchingForFive(Touch point, Vibrator vibrator, TextToSpeech speech)
    {
        if(point.pos_x > (AppConstants.TEXTVIEW_POSITION_X[5] + (AppConstants.TEXTVIEW_WIDTH /4)) && point.pos_x < (AppConstants.TEXTVIEW_POSITION_X[5] + (3*AppConstants.TEXTVIEW_WIDTH /4)) && point.pos_y > (AppConstants.TEXTVIEW_POSITION_Y[5] + (AppConstants.TEXTVIEW_HEIGHT /4)) && point.pos_y < (AppConstants.TEXTVIEW_POSITION_Y[5] + (3*AppConstants.TEXTVIEW_HEIGHT /4))) {
            speech.speak("5 FOUND. START TYPING", TextToSpeech.QUEUE_FLUSH, null);
            searchingForFive = false;
        }
        Vibrate vibrate = new Vibrate(vibrator);
        int strength = (int)(Math.abs(point.pos_x - (AppConstants.TEXTVIEW_POSITION_X[5] + AppConstants.TEXTVIEW_WIDTH /2)) + Math.abs(point.pos_y - (AppConstants.TEXTVIEW_POSITION_Y[5] + AppConstants.TEXTVIEW_HEIGHT /2)));
        int complement = (int)(Math.abs(AppConstants.TEXTVIEW_POSITION_X[5] - (AppConstants.TEXTVIEW_POSITION_X[5] + AppConstants.TEXTVIEW_WIDTH /2)) + Math.abs(AppConstants.TEXTVIEW_POSITION_Y[5] - (AppConstants.TEXTVIEW_POSITION_Y[5] + AppConstants.TEXTVIEW_HEIGHT /2)));
        vibrate.vibrate(1, strength/10, (complement > strength)?(complement - strength)/10:0);
    }


    public static char getClickedNumber(Touch start, Touch end) {
        //return '.';
        int x = AppConstants.keyboard.getKey(end);
        AppConstants.keyboard.train(end, x);
        Log.e("KEYBOARDDDDDDDDDDDD: ", end.pos_x + " : " + end.pos_y + " : " + x);
        return (char)(x + '0');
    }

//        double mid_x = (start.pos_x + end.pos_x)/2;
//        double mid_y = (start.pos_y + end.pos_y)/2;
//        if(mid_x > AppConstants.TEXTVIEW1_POSITION_X && mid_y > AppConstants.TEXTVIEW1_POSITION_Y && mid_x < (AppConstants.TEXTVIEW1_POSITION_X + AppConstants.TEXTVIEW_WIDTH) && mid_y < (AppConstants.TEXTVIEW1_POSITION_Y + AppConstants.TEXTVIEW_WIDTH))
//            return '1';
//        if(mid_x > AppConstants.TEXTVIEW2_POSITION_X && mid_y > AppConstants.TEXTVIEW2_POSITION_Y && mid_x < (AppConstants.TEXTVIEW2_POSITION_X + AppConstants.TEXTVIEW_WIDTH) && mid_y < (AppConstants.TEXTVIEW2_POSITION_Y + AppConstants.TEXTVIEW_WIDTH))
//            return '2';
//        if(mid_x > AppConstants.TEXTVIEW3_POSITION_X && mid_y > AppConstants.TEXTVIEW3_POSITION_Y && mid_x < (AppConstants.TEXTVIEW3_POSITION_X + AppConstants.TEXTVIEW_WIDTH) && mid_y < (AppConstants.TEXTVIEW3_POSITION_Y + AppConstants.TEXTVIEW_WIDTH))
//            return '3';
//        if(mid_x > AppConstants.TEXTVIEW4_POSITION_X && mid_y > AppConstants.TEXTVIEW4_POSITION_Y && mid_x < (AppConstants.TEXTVIEW4_POSITION_X + AppConstants.TEXTVIEW_WIDTH) && mid_y < (AppConstants.TEXTVIEW4_POSITION_Y + AppConstants.TEXTVIEW_WIDTH))
//            return '4';
//        if(mid_x > AppConstants.TEXTVIEW5_POSITION_X && mid_y > AppConstants.TEXTVIEW5_POSITION_Y && mid_x < (AppConstants.TEXTVIEW5_POSITION_X + AppConstants.TEXTVIEW_WIDTH) && mid_y < (AppConstants.TEXTVIEW5_POSITION_Y + AppConstants.TEXTVIEW_WIDTH))
//            return '5';
//        if(mid_x > AppConstants.TEXTVIEW6_POSITION_X && mid_y > AppConstants.TEXTVIEW6_POSITION_Y && mid_x < (AppConstants.TEXTVIEW6_POSITION_X + AppConstants.TEXTVIEW_WIDTH) && mid_y < (AppConstants.TEXTVIEW6_POSITION_Y + AppConstants.TEXTVIEW_WIDTH))
//            return '6';
//        if(mid_x > AppConstants.TEXTVIEW7_POSITION_X && mid_y > AppConstants.TEXTVIEW7_POSITION_Y && mid_x < (AppConstants.TEXTVIEW7_POSITION_X + AppConstants.TEXTVIEW_WIDTH) && mid_y < (AppConstants.TEXTVIEW7_POSITION_Y + AppConstants.TEXTVIEW_WIDTH))
//            return '7';
//        if(mid_x > AppConstants.TEXTVIEW8_POSITION_X && mid_y > AppConstants.TEXTVIEW8_POSITION_Y && mid_x < (AppConstants.TEXTVIEW8_POSITION_X + AppConstants.TEXTVIEW_WIDTH) && mid_y < (AppConstants.TEXTVIEW8_POSITION_Y + AppConstants.TEXTVIEW_WIDTH))
//            return '8';
//        if(mid_x > AppConstants.TEXTVIEW9_POSITION_X && mid_y > AppConstants.TEXTVIEW9_POSITION_Y && mid_x < (AppConstants.TEXTVIEW9_POSITION_X + AppConstants.TEXTVIEW_WIDTH) && mid_y < (AppConstants.TEXTVIEW9_POSITION_Y + AppConstants.TEXTVIEW_WIDTH))
//            return '9';
//        if(mid_x > AppConstants.TEXTVIEW_ASHTERISK_POSITION_X && mid_y > AppConstants.TEXTVIEW_ASHTERISK_POSITION_Y && mid_x < (AppConstants.TEXTVIEW_ASHTERISK_POSITION_X + AppConstants.TEXTVIEW_WIDTH) && mid_y < (AppConstants.TEXTVIEW_ASHTERISK_POSITION_Y + AppConstants.TEXTVIEW_WIDTH))
//            return ':';
//        if(mid_x > AppConstants.TEXTVIEW0_POSITION_X && mid_y > AppConstants.TEXTVIEW0_POSITION_Y && mid_x < (AppConstants.TEXTVIEW0_POSITION_X + AppConstants.TEXTVIEW_WIDTH) && mid_y < (AppConstants.TEXTVIEW0_POSITION_Y + AppConstants.TEXTVIEW_WIDTH))
//            return '0';
//        return ';';
//    }



    /*

    public static void Call(String number, Context context)
    {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            callIntent.setData(Uri.parse("tel:" + number));
            context.startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {
            Log.e("galileo_mytag", "Call failed", activityException);
        }
    }

    @SuppressLint("NewApi")
    public static int IdentifyAction(Touch start, Touch end, boolean moved, int currentAppStatus, Vibrator vibrator, TextToSpeech speech, T9 T9Dictioary, Context context)
    {
        Log.d("galileo_mytag ", "ENTEREDDDDDDDDDD");
        //speech.speak("SHHHHHH KOI HAI", TextToSpeech.QUEUE_FLUSH, null);

        //Vibrate vibrate = new Vibrate(vibrator);
        //vibrate.vibrate(4, 200, 50);

        if(Math.abs(start.pos_x - end.pos_x) > MoveThresholdPos || Math.abs(start.pos_y - end.pos_y) > MoveThresholdPos)
        {
            Log.d("galileo_mytag ", "MOVEEEEEEEEEEEE");
            //Move Action
            int moveDirection = getMoveDirection(start, end);
            Log.d("galileo_mytag ", "MOVE DIRECTION " + moveDirection);
            switch(moveDirection)
            {
                case 1: //DOWN: delete all data entered
                    Log.d("galileo_mytag ", "SWIPE UP");
                    speech.speak("DELETED ALL INPUT", TextToSpeech.QUEUE_FLUSH, null);
                    //       Call("09800160757", context);
                    if(currentAppStatus != AppStatus.searchingFor5) {
                        currentAppStatus = AppStatus.enteringNumbers;
                        T9Dictioary.clear();
                    }
                    break;
                case 2: //UP: start search for 5
                    speech.speak("LOCATE 5", TextToSpeech.QUEUE_FLUSH, null);
                    currentAppStatus = AppStatus.searchingFor5;
                    break;
                case 3: //LEFT: delete one last digit
                    if(currentAppStatus != AppStatus.searchingFor5)
                    {
                        //Delete last digit
                        //speech.speak("BACK", TextToSpeech.QUEUE_FLUSH, null);
                        int count = T9Dictioary.filter('\b');
                        speech.speak("GOING BACK. " + count + " RESULTS", TextToSpeech.QUEUE_FLUSH, null);
                        //T9 update
                    }
                    break;
                case 4: //RIGHT: Announce to call
                    //clear all
                    if(currentAppStatus != AppStatus.searchingFor5) {
                        if (T9Dictioary.getDictionary().getHead() == null || T9Dictioary.getDictionary().getHead().getSubTreeSize() == 0) {
                            speech.speak("NO CONTACT FOUND. CALLING DIALED NUMBER", TextToSpeech.QUEUE_FLUSH, null);
                            String number = T9Dictioary.getCurrentString();
                            T9Dictioary.clear();
                            Log.d("galileo_mytag", "CONTACT NUMBER BEFORE:::: " + number);
                            number = (number.replace(':', '*'));
                            number = (number.replace(';', '#'));
                            Log.d("galileo_mytag", "CONTACT NUMBER AFTER:::: " + number);
                            Call(number, context);
                        }
                        else if (T9Dictioary.getDictionary().getHead().getSubTreeSize() < 9) {
                            list = T9Dictioary.traverseDictionary();
                            speech.speak("ANNOUNCING", TextToSpeech.QUEUE_FLUSH, null);
                            for (int i = 0; i < list.size(); i++) {
                                speech.speak("PRESS " + (i + 1) + " FOR " + list.get(i).getName(), TextToSpeech.QUEUE_ADD, null);
                            }
                            currentAppStatus = AppStatus.announcingResults;
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
                Log.d("galileo_mytag ", "TAPPPPPEDDDD ");
                int count = T9Dictioary.filter(clickedNumber);
                speech.speak(digit[clickedNumber-'0'] + ". " + count + " RESULTS", TextToSpeech.QUEUE_FLUSH, null);
            }
            else if(currentAppStatus == AppStatus.announcingResults)
            {
                //Call
                Log.d("galileo_mytag ", "ANNOUNCING RESULTS CALLING");
                T9Dictioary.clear();
                ContactData contact = list.get(clickedNumber - '0' - 1);
                list.clear();
                speech.speak("CALLING " + contact.getName(), TextToSpeech.QUEUE_FLUSH, null);
                currentAppStatus = AppStatus.enteringNumbers;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("galileo_mytag", "CONTACT NUMBER BEFORE:::: " + contact.getNumber());
                contact.setNumber(contact.getNumber().replace(':', '*'));
                contact.setNumber(contact.getNumber().replace(';', '#'));
                Log.d("galileo_mytag", "CONTACT NUMBER AFTER:::: " + contact.getNumber());
                Call(contact.getNumber(), context);
            }
        }
        else
        {
            //Long Press
            if(currentAppStatus == AppStatus.enteringNumbers)
            {
                currentAppStatus = AppStatus.announcingResults;
                //announce Results
            }
            else if(currentAppStatus == AppStatus.announcingResults)
            {
                currentAppStatus = AppStatus.enteringNumbers;
            }
        }
        return currentAppStatus;
    }
    */
}

