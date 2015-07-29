package com.microsoft.anonymousknights.galileo;

import android.speech.tts.TextToSpeech;

import KeyboardPlus.Keyboard;

/**
 * Created by sam on 7/3/2015.
 */
public class AppConstants {
    public static float TEXTVIEW_POSITION_X[] = new float[12];
    public static float TEXTVIEW_POSITION_Y[] = new float[12];
    public static TextToSpeech speech;
    public static int CurrentAction;

    public static float TEXTVIEW_WIDTH;
    public static float TEXTVIEW_HEIGHT;
    public static float MIN_KEY_RADIUS;

    public static final int T9Mode = 1;
    public static final int EnglishMode = 2;
    public static final int CallMode = 3;
    public static final int SMSContactMode = 4;
    public static final int SMSTextMode = 5;
    public static final int EmailIdMode = 6;
    public static final int EmailSubjectMode = 7;
    public static final int EmailBodyMode = 8;

    public static final int doubleTapThreshold = 800;       //in millisecond

    public static final int SwipeDirectionUp = 1001;
    public static final int SwipeDirectionDown = 1002;
    public static final int SwipeDirectionLeft = 1003;
    public static final int SwipeDirectionRight = 1004;

    public static final int SingleClick = 1005;
    public static final int LongPress = 1006;

    public static final int CallAction = 1101;
    public static final int SMSAction = 1102;
    public static final int EmailAction = 1103;

    public static Keyboard keyboard;

    public static final int searchingFor5 = 1201;
    public static final int enteringNumbers = 1202;
    public static final int announcingResults = 1203;

    public static String currentActionSpeech;


    /*
    public static float TEXTVIEW1_POSITION_Y;
    public static float TEXTVIEW2_POSITION_X;
    public static float TEXTVIEW2_POSITION_Y;
    public static float TEXTVIEW3_POSITION_X;
    public static float TEXTVIEW3_POSITION_Y;
    public static float TEXTVIEW4_POSITION_X;
    public static float TEXTVIEW4_POSITION_Y;
    public static float TEXTVIEW5_POSITION_X;
    public static float TEXTVIEW5_POSITION_Y;
    public static float TEXTVIEW6_POSITION_X;
    public static float TEXTVIEW6_POSITION_Y;
    public static float TEXTVIEW7_POSITION_X;
    public static float TEXTVIEW7_POSITION_Y;
    public static float TEXTVIEW8_POSITION_X;
    public static float TEXTVIEW8_POSITION_Y;
    public static float TEXTVIEW9_POSITION_X;
    public static float TEXTVIEW9_POSITION_Y;
    public static float TEXTVIEW_ASHTERISK_POSITION_X;
    public static float TEXTVIEW_ASHTERISK_POSITION_Y;
    public static float TEXTVIEW0_POSITION_X;
    public static float TEXTVIEW0_POSITION_Y;
    public static float TEXTVIEW_SHARP_POSITION_X;
    public static float TEXTVIEW_SHARP_POSITION_Y;
*/
}
