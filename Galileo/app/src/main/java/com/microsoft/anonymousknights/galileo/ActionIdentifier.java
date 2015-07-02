package com.microsoft.anonymousknights.galileo;

import android.annotation.SuppressLint;
import android.os.Vibrator;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import Vibrate.Vibrate;

/**
 * Created by sam on 7/2/2015.
 */
public class ActionIdentifier {
    private static final int LongPressThresholdTime = 200;
    private static final int MoveThresholdPos = 200;
    @SuppressLint("NewApi")
    public static int IdentifyAction(Touch start, Touch end, long previousClickTime, boolean moved, int currentAppStatus, Vibrator vibrator)
    {
        Log.d("ACTION IDENTIFIER: ", "VIBRATION CHECK");
        Vibrate vibrate = new Vibrate(vibrator);
        vibrate.vibrate(4, 200, 50);
        if(true)
            return 1;
        if(Math.abs(start.pos_x - end.pos_x) > MoveThresholdPos || Math.abs(start.pos_y - end.pos_y) > MoveThresholdPos)
        {
            //Move Action
            int moveDirection = getMoveDirection(start, end);
            switch(moveDirection)
            {
                case 1: //UP: start search for 5
                    currentAppStatus = AppStatus.searchingFor5;
                    break;
                case 2: //DOWN: delete all data entered
                    currentAppStatus = AppStatus.enteringNumbers;
                    break;
                case 3: //LEFT: delete one last digit
                    if(currentAppStatus != AppStatus.searchingFor5)
                    {
                        //Delete last digit
                    }
                    break;
                case 4: //RIGHT: call
                    //clear all
                    break;
            }
        }
        else if(start.timestamp - end.timestamp < LongPressThresholdTime)
        {
            //Single Click
            if(currentAppStatus == AppStatus.enteringNumbers)
            {
                //Next number
            }
            else if(currentAppStatus == AppStatus.announcingResults)
            {
                //Call
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

    public static int getClickedNumber(Touch start, Touch end)
    {
        return 1;
    }
}
