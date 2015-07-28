package com.microsoft.anonymousknights.galileo;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.LinkedList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

import Contacts.ContactData;
import T9.T9;

/**
 * Created by sam on 7/27/2015.
 */
public class KeyboardFSM {

    private static int PageStatus;
    private static int mode;
    private static T9 T9Dictioary;
    private static LinkedList<ContactData> list;
    private static long previousTapTime = System.currentTimeMillis();
    private static char previousKey = '.';
    private static String SMSName, SMSNumber, SMSText;
    private static String EmailName, EmailId, EmailSubject, EmailBody;
    private static boolean typingMode = true;      //true for T9, false for english
    private static int tapCount = 1;
    static String[] digit=
            {
                    "0",
                    "1",
                    "2 A B C",
                    "3 D E F",
                    "4 G H I",
                    "5 J K L",
                    "6 M N O",
                    "7 P Q R S",
                    "8 T U V",
                    "9 W X Y Z",
                    "STAR",
                    "HASH"
            };

    static char[][] chars=
            {
                    {'0'},
                    {'1'},
                    {'2', 'A', 'B', 'C'},
                    {'3', 'D', 'E', 'F'},
                    {'4', 'G', 'H', 'I'},
                    {'5', 'J', 'K', 'L'},
                    {'6', 'M', 'N', 'O'},
                    {'7', 'P', 'Q', 'R', 'S'},
                    {'8', 'T', 'U', 'V'},
                    {'9', 'W', 'X', 'Y', 'Z'},
            };

    public static void FSM(BlockingQueue<ActionData> ActionDataList, Vibrator vibrator, TextToSpeech speech, T9 T9Dictioary, Context context)
    {
        int nextAction;
        char nextChar;
        int count = 0;
        while(true) {
            ActionData data = null;
            try {
                data = ActionDataList.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            nextAction = data.nextAction;
            nextChar = data.nextChar;
            switch (nextAction) {
                case AppConstants.SwipeDirectionDown: //DOWN: delete all data entered
                    Log.d("galileo_mytag ", "SWIPE UP");
                    speech.speak("DELETED ALL INPUT", TextToSpeech.QUEUE_FLUSH, null);
                    //       Call("09800160757", context);
                    PageStatus = AppStatus.enteringNumbers;
                    T9Dictioary.clear();
                    break;
                case AppConstants.SwipeDirectionUp: //UP: start search for 5
                    speech.speak("LOCATE 5", TextToSpeech.QUEUE_FLUSH, null);
                    PageStatus = AppStatus.searchingFor5;
                    break;
                case AppConstants.SwipeDirectionLeft: //LEFT: delete one last digit
                    //Delete last digit
                    //speech.speak("BACK", TextToSpeech.QUEUE_FLUSH, null);
                    count = T9Dictioary.filter('\b');
                    speech.speak("GOING BACK. " + count + " RESULTS", TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case AppConstants.SwipeDirectionRight: //RIGHT: Announce to call
                    //clear all
                    if (T9Dictioary.getDictionary().getHead() == null || T9Dictioary.getDictionary().getHead().getSubTreeSize() == 0) {
                        String currentString = T9Dictioary.getCurrentString();
                        T9Dictioary.clear();
                        switch (mode) {
                            case AppConstants.CallMode:
                                Call(null, currentString, context, speech);
                                break;
                            case AppConstants.SMSContactMode:
                                SMSName = null;
                                SMSNumber = currentString;
                                mode = AppConstants.SMSTextMode;
                                break;
                            case AppConstants.SMSTextMode:
                                SMSText = SMSText.concat(currentString);
                                break;
                            case AppConstants.EmailIdMode:
                                EmailName = null;
                                EmailId = currentString;
                                mode = AppConstants.EmailBodyMode;
                                break;
                            case AppConstants.EmailSubjectMode:
                                EmailSubject = EmailSubject.concat(currentString);
                                break;
                            case AppConstants.EmailBodyMode:
                                EmailSubject = EmailSubject.concat(currentString);
                                break;
                        }
                    } else if (T9Dictioary.getDictionary().getHead().getSubTreeSize() < 9) {
                        list = T9Dictioary.traverseDictionary();
                        speech.speak("ANNOUNCING", TextToSpeech.QUEUE_FLUSH, null);
                        for (int i = 0; i < list.size(); i++) {
                            speech.speak("PRESS " + (i + 1) + " FOR " + list.get(i).getName(), TextToSpeech.QUEUE_ADD, null);
                        }
                        PageStatus = AppStatus.announcingResults;
                    } else {
                        speech.speak("TOO MANY RESULTS", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    break;
                case AppConstants.LongPress:
                    break;
                case AppConstants.SingleClick:
                    //Single Click
                    if (PageStatus == AppStatus.enteringNumbers) {
                        //Next number
                        if (typingMode) {
                            Log.d("galileo_mytag ", "TAPPPPPEDDDD ");
                            count = T9Dictioary.filter(nextChar);
                            speech.speak(digit[nextChar - '0'] + ". " + count + " RESULTS", TextToSpeech.QUEUE_FLUSH, null);
                        } else {
                            if ((System.currentTimeMillis() - previousTapTime) < AppConstants.doubleTapThreshold && previousKey == nextChar) {
                                T9Dictioary.filter('\b');
                                count = T9Dictioary.filter(chars[nextChar][tapCount]);
                                speech.speak(chars[nextChar][tapCount] + ". " + count + " RESULTS", TextToSpeech.QUEUE_FLUSH, null);
                                tapCount = (tapCount + 1) % chars[nextChar].length;
                            } else {
                                Log.d("galileo_mytag ", "TAPPPPPEDDDD ");
                                count = T9Dictioary.filter(nextChar);
                                speech.speak(digit[nextChar - '0'] + ". " + count + " RESULTS", TextToSpeech.QUEUE_FLUSH, null);
                            }
                            previousTapTime = System.currentTimeMillis();
                        }
                    } else if (PageStatus == AppStatus.announcingResults) {
                        //Call
                        Log.d("galileo_mytag ", "ANNOUNCING RESULTS CALLING");
                        T9Dictioary.clear();
                        ContactData contact = list.get(nextChar - '0' - 1);
                        switch (mode) {
                            case AppConstants.CallMode:
                                Call(contact.getName(), contact.getNumber(), context, speech);
                                list.clear();
                                break;
                            case AppConstants.SMSContactMode:
                                SMSName = contact.getName();
                                SMSNumber = contact.getNumber();
                                mode = AppConstants.SMSTextMode;
                                break;
                            case AppConstants.SMSTextMode:
                                SMSText = SMSText.concat(contact.getName());
                                break;
                            case AppConstants.EmailIdMode:
                                EmailName = null;
                                EmailId = contact.getName();
                                mode = AppConstants.EmailBodyMode;
                                break;
                            case AppConstants.EmailSubjectMode:
                                EmailSubject = EmailSubject.concat(contact.getName());
                                break;
                            case AppConstants.EmailBodyMode:
                                EmailSubject = EmailSubject.concat(contact.getName());
                                break;
                        }
                    }
                    break;
            }
        }
    }
    public static void Call(String name, String number, Context context, TextToSpeech speech)
    {
        try {
            if(name == null)
            {
                speech.speak("NO CONTACT FOUND. CALLING DIALED NUMBER", TextToSpeech.QUEUE_FLUSH, null);
            }
            else
            {
                speech.speak("CALLING " + name, TextToSpeech.QUEUE_FLUSH, null);
                PageStatus = AppStatus.enteringNumbers;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("galileo_mytag", "CONTACT NUMBER BEFORE:::: " + number);
            number = (number.replace(':', '*'));
            number = (number.replace(';', '#'));
            Log.d("galileo_mytag", "CONTACT NUMBER AFTER:::: " + number);
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            callIntent.setData(Uri.parse("tel:" + number));
            context.startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {
            Log.e("galileo_mytag", "Call failed", activityException);
        }
    }
}
