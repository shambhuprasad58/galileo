package com.microsoft.anonymousknights.galileo;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

import Contacts.ContactData;
import T9.T9;

/**
 * Created by sam on 7/27/2015.
 */
public class KeyboardFSM {

    private static int PageStatus = AppConstants.enteringNumbers;
    private static int mode;
    public static T9 T9Dictioary;
    private static LinkedList<ContactData> list;
    private static long previousTapTime = System.currentTimeMillis();
    private static char previousKey = '.';
    private static String SMSName = "", SMSNumber, SMSText = "";
    private static String EmailId = "", EmailSubject = "", EmailBody = "";
    private static boolean T9typingMode = true;      //true for T9, false for english
    private static int tapCount = 1;
    /*
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
            */

    static String[] digit=
            {
                    "0",
                    "1",
                    "2",
                    "3",
                    "4",
                    "5",
                    "6",
                    "7",
                    "8",
                    "9",
                    "STAR",
                    "HASH"
            };
    static char[][] chars=
            {
                    {'0', ' '},
                    {'1','.',',','@'},
                    {'2', 'A', 'B', 'C'},
                    {'3', 'D', 'E', 'F'},
                    {'4', 'G', 'H', 'I'},
                    {'5', 'J', 'K', 'L'},
                    {'6', 'M', 'N', 'O'},
                    {'7', 'P', 'Q', 'R', 'S'},
                    {'8', 'T', 'U', 'V'},
                    {'9', 'W', 'X', 'Y', 'Z'}
            };

    static String[][] speechchars=
            {
                    {"0", "space"},
                    {"1", "dot", "comma", "AT THE RATE"},
                    {"2", "A", "B", "C"},
                    {"3", "D", "E", "F"},
                    {"4", "G", "H", "I"},
                    {"5", "J", "K", "L"},
                    {"6", "M", "N", "O"},
                    {"7", "P", "Q", "R", "S"},
                    {"8", "T", "U", "V"},
                    {"9", "W", "X", "Y", "Z"},
                    {"hash"}
            };

    public static void FSM(BlockingQueue<ActionData> ActionDataList, Vibrator vibrator, TextToSpeech speech, T9 T9ContactDictionary, T9 T9WordDictionary, Context context)
    {
        int nextAction;
        char nextChar;
        int count = 0;
        T9Dictioary = T9ContactDictionary;
        int errorCount = 0;
        switch (AppConstants.CurrentAction)
        {
            case AppConstants.CallAction: mode = AppConstants.CallMode; break;
            case AppConstants.SMSAction: mode = AppConstants.SMSContactMode; break;
            case AppConstants.EmailAction: mode = AppConstants.EmailIdMode; T9typingMode = false; T9Dictioary = T9WordDictionary; break;
        }
        String currentString = "";
        while(true) {
            try {
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
                        PageStatus = AppConstants.enteringNumbers;
                        T9Dictioary.clear();
                        currentString = "";
                        break;
                    case AppConstants.SwipeDirectionUp: //UP: start search for 5
                        speech.speak("LOCATE 5", TextToSpeech.QUEUE_FLUSH, null);
                        PageStatus = AppConstants.searchingFor5;
                        break;
                    case AppConstants.SwipeDirectionLeft: //LEFT: delete one last digit
                        //Delete last digit
                        //speech.speak("BACK", TextToSpeech.QUEUE_FLUSH, null);
                        if (T9typingMode) {
                            count = T9Dictioary.filter('\b');
                            speech.speak("GOING BACK. " + count + " RESULTS", TextToSpeech.QUEUE_FLUSH, null);
                        } else {
                            if (!currentString.equalsIgnoreCase(""))
                                currentString = currentString.substring(0, currentString.length() - 1);
                            speech.speak("GOING BACK", TextToSpeech.QUEUE_FLUSH, null);
                        }
                        break;
                    case AppConstants.SwipeDirectionRight: //RIGHT: Announce to call
                        //clear all
                        if (T9typingMode) {
                            if (T9Dictioary.getDictionary().getHead() == null || T9Dictioary.getDictionary().getHead().getSubTreeSize() == 0) {
                                currentString = currentString + T9Dictioary.getCurrentString();
                                T9Dictioary.clear();
                            } else if (T9Dictioary.getDictionary().getHead().getSubTreeSize() < 9) {
                                list = T9Dictioary.traverseDictionary();
                                speech.speak("ANNOUNCING", TextToSpeech.QUEUE_FLUSH, null);
                                for (int i = 0; i < list.size(); i++) {
                                    speech.speak("PRESS " + (i + 1) + " FOR " + list.get(i).getName(), TextToSpeech.QUEUE_ADD, null);
                                }
                                PageStatus = AppConstants.announcingResults;
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
                                T9Dictioary = T9WordDictionary;
                                speech.speak("ENTER SMS TEXT", TextToSpeech.QUEUE_FLUSH, null);
                                break;
                            case AppConstants.SMSTextMode:
                                SMSText = SMSText.concat(currentString);
                                break;
                            case AppConstants.EmailIdMode:
                                EmailId = currentString;
                                mode = AppConstants.EmailSubjectMode;
                                speech.speak("ENTER EMAIL SUBJECT", TextToSpeech.QUEUE_FLUSH, null);
                                break;
                            case AppConstants.EmailSubjectMode:
                                EmailSubject = EmailSubject.concat(currentString);
                                mode = AppConstants.EmailBodyMode;
                                speech.speak("Enter Email Body", TextToSpeech.QUEUE_FLUSH, null);
                                break;
                            case AppConstants.EmailBodyMode:
                                EmailBody = EmailBody.concat(currentString);
                                EMAIL(context, speech);
                                break;
                        }
                        currentString = "";
                        break;
                    case AppConstants.LongPress:
                        if (T9typingMode) {
                            currentString = T9Dictioary.getCurrentString();
                            T9Dictioary.clear();
                        }
                        switch (mode) {
                            case AppConstants.CallMode:
                                Call(null, currentString, context, speech);
                                break;
                            case AppConstants.SMSContactMode:
                                SMSName = null;
                                SMSNumber = currentString;
                                mode = AppConstants.SMSTextMode;
                                speech.speak("ENTER SMS TEXT", TextToSpeech.QUEUE_FLUSH, null);
                                break;
                            case AppConstants.SMSTextMode:
                                SMSText = SMSText + currentString;
                                SMS(context, speech);
                                break;
                            case AppConstants.EmailIdMode:
                                EmailId = currentString;
                                mode = AppConstants.EmailSubjectMode;
                                speech.speak("ENTER EMAIL SUBJECT", TextToSpeech.QUEUE_FLUSH, null);
                                break;
                            case AppConstants.EmailSubjectMode:
                                EmailSubject = EmailSubject + currentString;
                                mode = AppConstants.EmailBodyMode;
                                speech.speak("Enter Email Body", TextToSpeech.QUEUE_FLUSH, null);
                                break;
                            case AppConstants.EmailBodyMode:
                                EmailSubject = EmailSubject + currentString;
                                EMAIL(context, speech);
                                break;
                        }
                        currentString = "";
                        break;
                    case AppConstants.SingleClick:
                        //Single Click
                        if (PageStatus == AppConstants.enteringNumbers) {
                            //Next number
                            if (nextChar == ';') {
                                if (mode == AppConstants.SMSTextMode || mode == AppConstants.EmailBodyMode || mode == AppConstants.EmailSubjectMode) {
                                    speech.speak("CHANGING DICTIONARY MODE", TextToSpeech.QUEUE_FLUSH, null);
                                    if (T9typingMode) {
                                        currentString = currentString + T9Dictioary.getCurrentString();
                                        T9Dictioary.clear();
                                        T9typingMode = false;
                                    } else
                                        T9typingMode = true;
                                    switch (mode) {
                                        case AppConstants.SMSTextMode:
                                            SMSText = SMSText.concat(currentString);
                                            break;
                                        case AppConstants.EmailSubjectMode:
                                            EmailSubject = EmailSubject.concat(currentString);
                                            break;
                                        case AppConstants.EmailBodyMode:
                                            EmailBody = EmailBody.concat(currentString);
                                            break;
                                    }
                                    currentString = "";
                                    break;
                                }
                            }
                            if(mode == AppConstants.SMSTextMode || mode == AppConstants.EmailSubjectMode || mode == AppConstants.EmailBodyMode) {
                                if (T9typingMode && nextChar == '0') {
                                    currentString = T9Dictioary.getCurrentString();
                                    T9Dictioary.clear();
                                    switch (mode) {
                                        case AppConstants.SMSTextMode:
                                            SMSText = SMSText.concat(currentString + " ");
                                            break;
                                        case AppConstants.EmailSubjectMode:
                                            EmailSubject = EmailSubject.concat(currentString + " ");
                                            break;
                                        case AppConstants.EmailBodyMode:
                                            EmailBody = EmailBody.concat(currentString + " ");
                                            break;
                                    }
                                    currentString = "";
                                    break;
                                }
                            }
                            if (T9typingMode) {
                                Log.d("galileo_mytag ", "T9  TAPPPPPEDDDD ");
                                count = T9Dictioary.filter(nextChar);
                                speech.speak(digit[nextChar - '0'] + ". " + count + " RESULTS", TextToSpeech.QUEUE_FLUSH, null);
                            } else {
                                if ((System.currentTimeMillis() - previousTapTime) < AppConstants.doubleTapThreshold && previousKey == nextChar) {
                                    //T9Dictioary.filter('\b');
                                    //count = T9Dictioary.filter(chars[nextChar][tapCount]);
                                    //speech.speak(chars[nextChar][tapCount] + ". " + count + " RESULTS", TextToSpeech.QUEUE_FLUSH, null);
                                    Log.d("galileo_mytag ", "ENGLISH  MULTI TAPPPPPEDDDD " + speechchars[nextChar - '0'][tapCount]);
                                    tapCount = (tapCount + 1) % chars[nextChar - '0'].length;
                                    currentString = currentString.substring(0, currentString.length() - 1);
                                    currentString = currentString + chars[nextChar - '0'][tapCount];
                                    speech.speak(speechchars[nextChar - '0'][tapCount], TextToSpeech.QUEUE_ADD, null);
                                } else {
                                    //count = T9Dictioary.filter(nextChar);
                                    //speech.speak(digit[nextChar - '0'] + ". " + count + " RESULTS", TextToSpeech.QUEUE_FLUSH, null);
                                    tapCount = 1;
                                    Log.d("galileo_mytag ", "ENGLISH NEW TAPPPPPEDDDD " + chars[nextChar - '0'][1]);
                                    currentString = currentString + chars[nextChar - '0'][1];
                                    speech.speak(speechchars[nextChar - '0'][tapCount], TextToSpeech.QUEUE_FLUSH, null);
                                }
                                previousKey = nextChar;
                                previousTapTime = System.currentTimeMillis();
                            }
                        } else if (PageStatus == AppConstants.announcingResults) {
                            //Call
                            Log.d("galileo_mytag ", "ANNOUNCING RESULTS CALLING");
                            T9Dictioary.clear();
                            ContactData contact = list.get(nextChar - '0' - 1);
                            PageStatus = AppConstants.enteringNumbers;
                            switch (mode) {
                                case AppConstants.CallMode:
                                    Call(contact.getName(), contact.getNumber(), context, speech);
                                    list.clear();
                                    break;
                                case AppConstants.SMSContactMode:
                                    SMSName = contact.getName();
                                    SMSNumber = contact.getNumber();
                                    mode = AppConstants.SMSTextMode;
                                    T9Dictioary = T9WordDictionary;
                                    speech.speak(SMSName + " selected. ENTER SMS TEXT", TextToSpeech.QUEUE_FLUSH, null);
                                    T9Dictioary.clear();
                                    break;
                                case AppConstants.SMSTextMode:
                                    SMSText = SMSText.concat(contact.getName());
                                    speech.speak(contact.getName() + "SELECTED", TextToSpeech.QUEUE_FLUSH, null);
                                    break;
                                case AppConstants.EmailIdMode:
                                    EmailId = contact.getName();
                                    speech.speak(EmailId + " is email id. ENTER SUBJECT TEXT", TextToSpeech.QUEUE_FLUSH, null);
                                    mode = AppConstants.EmailSubjectMode;
                                    break;
                                case AppConstants.EmailSubjectMode:
                                    EmailSubject = EmailSubject.concat(contact.getName());
                                    speech.speak(contact.getName() + "SELECTED", TextToSpeech.QUEUE_FLUSH, null);
                                    break;
                                case AppConstants.EmailBodyMode:
                                    EmailBody = EmailBody.concat(contact.getName());
                                    speech.speak(contact.getName() + "SELECTED", TextToSpeech.QUEUE_FLUSH, null);
                                    break;
                            }
                        }
                        break;
                }
            }catch (Exception ex)
            {
                ex.printStackTrace();
                speech.speak("OOPS. THAT WAS UNEXPECTED", TextToSpeech.QUEUE_FLUSH, null);
                errorCount++;
                if(errorCount >= 3) {
                    System.exit(0);
                }
            }
        }
    }
    public static void Call(String name, String number, Context context, TextToSpeech speech)
    {
        if(number == null || number.equalsIgnoreCase("")) {
            speech.speak("NO NUMBER DIALED", TextToSpeech.QUEUE_FLUSH, null);
            return;
        }
        try {
            if(name == null)
            {
                speech.speak("NO CONTACT FOUND. CALLING DIALED NUMBER", TextToSpeech.QUEUE_FLUSH, null);
            }
            else
            {
                speech.speak("CALLING " + name, TextToSpeech.QUEUE_FLUSH, null);
                PageStatus = AppConstants.enteringNumbers;
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
        if(SMSNumber == null || SMSNumber.equalsIgnoreCase("")) {
            speech.speak("NO NUMBER DIALED", TextToSpeech.QUEUE_FLUSH, null);
            return;
        }
        try {
            if(SMSName == null)
            {
                speech.speak("SENDING SMS TO " + SMSNumber, TextToSpeech.QUEUE_FLUSH, null);
            }
            else
            {
                speech.speak("SENDING SMS to " + SMSName, TextToSpeech.QUEUE_FLUSH, null);
                PageStatus = AppConstants.enteringNumbers;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("galileo_mytag", "CONTACT NUMBER BEFORE:::: " + SMSNumber);
            SMSNumber = (SMSNumber.replace(':', '*'));
            SMSNumber = (SMSNumber.replace(';', '#'));
            SMSNumber = (SMSNumber.replace(" ", ""));
            Log.d("galileo_mytag", "CONTACT NUMBER AFTER:::: " + SMSNumber);
//            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//            sendIntent.putExtra(SMSText, "default content");
//            sendIntent.setType("vnd.android-dir/mms-sms");
//            context.startActivity(sendIntent);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(SMSNumber, null, SMSText, null, null);
        } catch (ActivityNotFoundException activityException) {
            Log.e("galileo_mytag", "SMS failed", activityException);
        }
    }

    public static void EMAIL(Context context, TextToSpeech speech)
    {
        if(EmailId == null || EmailId.equalsIgnoreCase("") || !isValidEmail(EmailId)) {
            speech.speak("INVALID EMAIL ID", TextToSpeech.QUEUE_FLUSH, null);
            return;
        }
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
            Log.e("galileo_mytag", "EMAIL failed", activityException);
        }
    }

    public final static boolean isValidEmail(String target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
