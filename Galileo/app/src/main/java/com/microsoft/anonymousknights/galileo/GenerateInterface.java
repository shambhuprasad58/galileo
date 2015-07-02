package com.microsoft.anonymousknights.galileo;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by sam on 7/2/2015.
 */
public class GenerateInterface {
    Button[] buttons = new Button[12];
    Context context;
    private GridViewAdapter adapter;
    private ArrayList<String> items;

    public GenerateInterface(Context activityContext)
    {
        this.context = activityContext;
    }

    public View createGridLayout() {
        // create a RelativeLayout
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        RelativeLayout relativeLayout = new RelativeLayout(context);
        adapter = new GridViewAdapter();
        GridLayout layout = new GridLayout(context);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setRowCount(5);
        layout.setColumnCount(3);
        items = new ArrayList<String>();
        for (int i = 1; i < 10; i++) {
            LinearLayout cell = new LinearLayout(context);
            cell.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            Button button = new Button(context);
            button.setWidth(width/3);
            button.setHeight(height / 6);
            button.setText(String.valueOf(i));
            cell.addView(button);
            layout.addView(cell, new GridLayout.LayoutParams(
                    GridLayout.spec(i/3+1),
                    GridLayout.spec(i%3)));
        }
        items.add("*");
        items.add("0");
        items.add("#");
        relativeLayout.addView(layout);
        return relativeLayout;
    }

    class GridViewAdapter extends BaseAdapter {

        public int getCount() {
            return items.size();
        }

        public Object getItem(int position) {
            return items.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Button button = new Button(context);
            button.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            button.setText(items.get(position));
            return button;
        }
    }
}