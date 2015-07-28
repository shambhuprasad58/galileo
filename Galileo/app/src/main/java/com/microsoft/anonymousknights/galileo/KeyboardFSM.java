package com.microsoft.anonymousknights.galileo;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

import Contacts.ContactData;
import T9.T9;

/**
 * Created by sam on 7/27/2015.
 */
public class KeyboardFSM {

    private static int PageStatus;
    private static int mode;
    public static T9 T9Dictioary;
    private static LinkedList<ContactData> list;
    private static long previousTapTime = System.currentTimeMillis();
    private static char previousKey = '.';
    private static String SMSName, SMSNumber, SMSText;
    private static String EmailId, EmailSubject, EmailBody;
    private static boolean T9typingMode = true;      //true for T9, false for english
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

    public static void FSM(BlockingQueue<ActionData> ActionDataList, Vibrator vibrator, TextToSpeech speech, T9 T9ContactDictioary, T9 T9WordDictioary, Context context)
    {
        int nextAction;
        char nextChar;
        int count = 0;
        String currentString = "";
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
                    currentString = "";
                    break;
                case AppConstants.SwipeDirectionUp: //UP: start search for 5
                    speech.speak("LOCATE 5", TextToSpeech.QUEUE_FLUSH, null);
                    PageStatus = AppStatus.searchingFor5;
                    break;
                case AppConstants.SwipeDirectionLeft: //LEFT: delete one last digit
                    //Delete last digit
                    //speech.speak("BACK", TextToSpeech.QUEUE_FLUSH, null);
                    if(T9typingMode) {
                        count = T9Dictioary.filter('\b');
                        speech.speak("GOING BACK. " + count + " RESULTS", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else {
                        currentString = currentString.substring(0, currentString.length() - 1);
                        speech.speak("GOING BACK", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    break;
                case AppConstants.SwipeDirectionRight: //RIGHT: Announce to call
                    //clear all
                        if(T9typingMode) {
                            if (T9Dictioary.getDictionary().getHead() == null || T9Dictioary.getDictionary().getHead().getSubTreeSize() == 0) {
                                currentString = T9Dictioary.getCurrentString();
                                T9Dictioary.clear();
                            }
                            else if (T9Dictioary.getDictionary().getHead().getSubTreeSize() < 9) {
                                list = T9Dictioary.traverseDictionary();
                                speech.speak("ANNOUNCING", TextToSpeech.QUEUE_FLUSH, null);
                                for (int i = 0; i < list.size(); i++) {
                                    speech.speak("PRESS " + (i + 1) + " FOR " + list.get(i).getName(), TextToSpeech.QUEUE_ADD, null);
                                }
                                PageStatus = AppStatus.announcingResults;
                                break;
                            } else {
                                speech.speak("TOO MANY RESULTS", TextToSpeech.QUEUE_FLUSH, null);
                                break;
                            }
                        }
                        switch (mode) {
                            case AppConstants.CallMode:
                                Call(null, currentString, context, speech);
                                break;
                            case AppConstants.SMSContactMode:
                                SMSName = null;
                                SMSNumber = currentString;
                                mode = AppConstants.SMSTextMode;
                                T9Dictioary = T9WordDictioary;
                                break;
                            case AppConstants.SMSTextMode:
                                SMSText = SMSText.concat(" " + currentString);
                                break;
                            case AppConstants.EmailIdMode:
                                EmailId = currentString;
                                mode = AppConstants.EmailSubjectMode;
                                break;
                            case AppConstants.EmailSubjectMode:
                                EmailSubject = EmailSubject.concat(" " + currentString);
                                mode = AppConstants.EmailBodyMode;
                                break;
                            case AppConstants.EmailBodyMode:
                                EmailBody = EmailBody.concat(currentString);
                                EMAIL(context, speech);
                                break;
                        }
                    break;
                case AppConstants.LongPress:
                        if(T9typingMode) {
                            currentString = T9Dictioary.getCurrentString();
                            T9Dictioary.clear();
                        }
                        switch (mode)
                        {
                            case AppConstants.CallMode: Call(null, currentString, context, speech); break;
                            case AppConstants.SMSContactMode: SMSName = null; SMSNumber = currentString; mode = AppConstants.SMSTextMode; break;
                            case AppConstants.SMSTextMode: SMSText = SMSText + currentString; SMS(context, speech); break;
                            case AppConstants.EmailIdMode: EmailId = currentString; mode = AppConstants.EmailSubjectMode; break;
                            case AppConstants.EmailSubjectMode: EmailBody = EmailBody + currentString; mode = AppConstants.EmailBodyMode; break;
                            case AppConstants.EmailBodyMode: EMAIL(context, speech); break;
                        }
                    break;
                case AppConstants.SingleClick:
                    //Single Click
                    if (PageStatus == AppStatus.enteringNumbers) {
                        //Next number
                        if (T9typingMode) {
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
                                T9Dictioary = T9WordDictioary;
                                T9Dictioary.clear();
                                break;
                            case AppConstants.SMSTextMode:
                                SMSText = SMSText.concat(contact.getName());
                                break;
                            case AppConstants.EmailIdMode:
                                EmailId = contact.getName();
                                mode = AppConstants.EmailSubjectMode;
                                break;
                            case AppConstants.EmailSubjectMode:
                                EmailSubject = EmailSubject.concat(contact.getName());
                                break;
                            case AppConstants.EmailBodyMode:
                                EmailBody = EmailBody.concat(contact.getName());
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

    public static void SMS(Context context, TextToSpeech speech)
    {
        try {
            if(SMSName == null)
            {
                speech.speak("SENDING SMS TO " + SMSNumber, TextToSpeech.QUEUE_FLUSH, null);
            }
            else
            {
                speech.speak("SENDING SMS to " + SMSName, TextToSpeech.QUEUE_FLUSH, null);
                PageStatus = AppStatus.enteringNumbers;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("galileo_mytag", "CONTACT NUMBER BEFORE:::: " + SMSNumber);
            SMSNumber = (SMSNumber.replace(':', '*'));
            SMSNumber = (SMSNumber.replace(';', '#'));
            Log.d("galileo_mytag", "CONTACT NUMBER AFTER:::: " + SMSNumber);
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts(SMSText, SMSNumber, null)));
        } catch (ActivityNotFoundException activityException) {
            Log.e("galileo_mytag", "Call failed", activityException);
        }
    }

    public static void EMAIL(Context context, TextToSpeech speech)
    {
        try {
            speech.speak("SENDING EMAIL TO " + EmailId, TextToSpeech.QUEUE_FLUSH, null);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Log.d("galileo_mytag", "CONTACT NUMBER BEFORE:::: " + number);
            //number = (number.replace(':', '*'));
            //number = (number.replace(';', '#'));
            //Log.d("galileo_mytag", "CONTACT NUMBER AFTER:::: " + number);
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{EmailId});
            i.putExtra(Intent.EXTRA_SUBJECT, EmailSubject);
            i.putExtra(Intent.EXTRA_TEXT , EmailBody);
            context.startActivity(i);
        } catch (ActivityNotFoundException activityException) {
            Log.e("galileo_mytag", "Call failed", activityException);
        }
    }
}
