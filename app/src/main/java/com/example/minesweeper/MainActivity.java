package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.widget.GridLayout;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int ROW_COUNT = 10;
    private static final int COLUMN_COUNT = 8;

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;

    // tracks flagged cells
    private ArrayList<Boolean> flagged;

    private enum Mode {
        PICK_MODE,
        FLAG_MODE
    }
    private Mode mode = Mode.PICK_MODE;

    private int timer = 0;
    private boolean timer_running = false;
    private boolean first_click = true;

    private int num_flags = 4;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView flag_count = (TextView) findViewById(R.id.num_flags);
        flag_count.setText(String.valueOf(num_flags));

        cell_tvs = new ArrayList<TextView>();
        flagged = new ArrayList<Boolean>();

        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout0);

        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                TextView tv = new TextView(this);
                tv.setHeight(dpToPixel(32));
                tv.setWidth(dpToPixel(32));
                tv.setTextSize(18);
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.GRAY);
                tv.setOnClickListener(this::onClickGridCell);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(2), dpToPixel(2), dpToPixel(2), dpToPixel(2));
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
                flagged.add(false);
            }
        }

        TextView modeSwitch = (TextView) findViewById(R.id.modeSwitch);
        modeSwitch.setOnClickListener(this::switchMode);

        if (savedInstanceState != null) {
            timer = savedInstanceState.getInt("timer");
            timer_running = savedInstanceState.getBoolean("timer_running");
            first_click = savedInstanceState.getBoolean("first_click");
            num_flags = savedInstanceState.getInt("num_flags");
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("timer", timer);
        savedInstanceState.putBoolean("timer_running", timer_running);
        savedInstanceState.putBoolean("first_click", first_click);
        savedInstanceState.putInt("num_flags", num_flags);
    }

    private void runClock() {
        final TextView timeView = (TextView) findViewById(R.id.timer);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int seconds = timer;
                String time = String.format("%02d", seconds);
                timeView.setText(time);

                if (timer_running) {
                    timer++;
                }

                handler.postDelayed(this, 1000);
            }
        });
    }

    private void placeFlag(TextView tv, int cell_no) {
        final TextView flag_count = (TextView) findViewById(R.id.num_flags);
        tv.setText("🚩");

        num_flags++;
        flag_count.setText(String.valueOf(num_flags));
        flagged.get(findIndexOfCellTextView(tv));
    }



    public void removeFlag(TextView tv, int cell_no) {
        final TextView flag_count = (TextView) findViewById(R.id.num_flags);
        tv.setText("X");

        num_flags--;
        flag_count.setText(String.valueOf(Math.max(0, num_flags)));
        flagged.get(cell_no) = true;
    }

    public void pickGridCell(TextView tv) {
        tv.setText("X");

        if (tv.getCurrentTextColor() == Color.GRAY) {
            tv.setTextColor(Color.GREEN);
            tv.setBackgroundColor(Color.parseColor("lime"));
        } else {
            tv.setTextColor(Color.GRAY);
            tv.setBackgroundColor(Color.LTGRAY);
        }

        if (first_click) {
            timer_running = true;
            runClock();
            first_click = false;
        }
    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    public void onClickGridCell(View view){
        TextView tv = (TextView) view;

        /*
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;
        */

        if (mode == Mode.PICK_MODE) {
            pickGridCell(tv);
        } else { // mode == Mode.FLAG_MODE
            int cell_no = findIndexOfCellTextView(tv);
            boolean isFlagged = flagged.get(cell_no); //tv.getText().toString() == "\ud83d\udea9";
            if (isFlagged) {
                removeFlag(tv, cell_no);
            } else {
                placeFlag(tv, cell_no);
            }
        }
    }

    public void switchMode(View view) {
        TextView tv = (TextView) view;

        if (mode == Mode.PICK_MODE) {
            mode = Mode.FLAG_MODE;
            tv.setText("\ud83d\udea9");
        } else {
            mode = Mode.PICK_MODE;
            tv.setText("\u26CF");
        }
    }






}