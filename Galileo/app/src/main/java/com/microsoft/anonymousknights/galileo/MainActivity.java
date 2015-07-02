package com.microsoft.anonymousknights.galileo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.concurrent.ConcurrentLinkedQueue;

import T9.T9;



public class MainActivity extends AppCompatActivity {

    ConcurrentLinkedQueue<Touch> SenseDataList;

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
        SenseDataList = new ConcurrentLinkedQueue<Touch>();
        LinearLayout baselayout = (LinearLayout) findViewById(R.id.base_layout);
        baselayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("Touch : " + motionEvent.getAction(),
                            String.valueOf(motionEvent.getX()) + "x" + String.valueOf(motionEvent.getY()));
                return true;
            }
        });
        baselayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Click: ", "CLICKEDDDDDDDDD");
            }
        });
	T9 t9 = new T9();
        t9.clear();
        int x = t9.filter('3');
        Log.d("AAAAAAAAAAAAAAAAAAAAA", Integer.toString(x));
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

        @Override
        protected Void doInBackground(Void... v) {
            while(true) {
                if (SenseDataList.peek().type == MotionEvent.ACTION_DOWN) {
                    ActionIdentifier.IdentifyAction(SenseDataList);
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
