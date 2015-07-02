package com.microsoft.anonymousknights.galileo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

import T9.T9;



public class MainActivity extends AppCompatActivity {

    ConcurrentLinkedDeque<Touch> SenseDataList;
    Vibrator vibrator;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // define the RelativeLayout layout parameters.
//        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.MATCH_PARENT);
//
//        setContentView(new GenerateInterface(this).createGridLayout(), relativeLayoutParams);
        setContentView(R.layout.activity_main);
        SenseDataList = new ConcurrentLinkedDeque<Touch>();
        vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        LinearLayout baselayout = (LinearLayout) findViewById(R.id.base_layout);
        baselayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.e("Touch : " + motionEvent.getAction(),
                            String.valueOf(motionEvent.getX()) + "x" + String.valueOf(motionEvent.getY()));
                    SenseDataList.addFirst(new Touch(motionEvent.getX(), motionEvent.getY(), motionEvent.getAction(), System.currentTimeMillis()));
                }
                return true;
            }
        });
//        baselayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e("Click: ", "CLICKEDDDDDDDDD");
//            }
//        });
        new RequestTask().execute();
//	T9 t9 = new T9();
//        t9.clear();
//        int x = t9.filter('3');
//        Log.d("AAAAAAAAAAAAAAAAAAAAA", Integer.toString(x));
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

    class RequestTask extends AsyncTask<Void, Void, Void> {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected Void doInBackground(Void... v) {
            Log.d("ASYNC TASK: ", "BACKGROUNDddddddddddddd");
            long previousClickTime = 0;
            boolean moved;
            int currentAppStatus = 0;
            while(true) {
                if(!SenseDataList.isEmpty()) {
                    Log.d("ASYNC TASK: ", "LIST NOT EMPTY");
                    if (SenseDataList.peek().type == MotionEvent.ACTION_UP) {
                        Log.d("ASYNC TASK: ", "ACTION UP");
                        moved = false;
                        Touch end = SenseDataList.getFirst();
                        Touch start = SenseDataList.getLast();
                        //if(SenseDataList.size() > 4)
                        moved = true;
                        SenseDataList.clear();
                        ActionIdentifier.IdentifyAction(start, end, previousClickTime, moved, currentAppStatus, vibrator);
                        previousClickTime = end.timestamp;
                    }
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //Do anything with response..
        }
    }
}
