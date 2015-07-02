package com.microsoft.anonymousknights.galileo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

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
        LinearLayout baselayout = (LinearLayout) findViewById(R.id.base_layout);
        baselayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    Log.e("Touch : DOWN",
                            String.valueOf(motionEvent.getX()) + "x" + String.valueOf(motionEvent.getY()));
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    Log.e("Touch UP: ",
                            String.valueOf(motionEvent.getX()) + "x" + String.valueOf(motionEvent.getY()));
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE){
                    Log.e("Touch MOVE: ",
                            String.valueOf(motionEvent.getX()) + "x" + String.valueOf(motionEvent.getY()));
                }
                return true;
            }
        });
        baselayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Click: ", "CLICKEDDDDDDDDD");
            }
        });
    }

    public void textviewClick(View v)
    {
        Log.e("Click: ",((TextView)v).getText().toString());
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
}
