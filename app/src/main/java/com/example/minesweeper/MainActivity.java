package com.example.minesweeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.widget.GridLayout;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

class GridCell extends androidx.appcompat.widget.AppCompatTextView {
    int row;
    int column;

    boolean isFlagged = false;
    boolean isMine = false;
    boolean isPicked = false;

    int num_neighboring_mines = 0;

    public GridCell(Context context) {
        super(context);
        this.setTextSize(18);
        this.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        this.setTextColor(Color.GREEN);
        this.setBackgroundColor(Color.GREEN);
    }
}

public class MainActivity extends AppCompatActivity {

    private static final int ROW_COUNT = 10;
    private static final int COLUMN_COUNT = 8;
    private static final int NUM_MINES = 4;

    private int cellsToReveal = ROW_COUNT*COLUMN_COUNT-NUM_MINES;
    private int num_flags = NUM_MINES;

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<GridCell> cells;

    private enum Mode {
        PICK_MODE,
        FLAG_MODE
    }
    private Mode mode = Mode.PICK_MODE;

    private int timer = 0;
    private boolean timer_running = false;
    private boolean first_click = true;

    private boolean gameIsOver = false;
    private boolean userWon = false;

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

        cells = new ArrayList<>();

        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout0);

        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                GridCell gc = new GridCell(this);
                gc.row = i;
                gc.column = j;
                gc.setHeight(dpToPixel(32));
                gc.setWidth(dpToPixel(32));
                gc.setOnClickListener(this::onClickGridCell);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(2), dpToPixel(2), dpToPixel(2), dpToPixel(2));
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(gc, lp);
                cells.add(gc);
            }
        }

        setMines();

        TextView modeSwitch = (TextView) findViewById(R.id.modeSwitch);
        modeSwitch.setOnClickListener(this::switchMode);

        if (savedInstanceState != null) {
            timer = savedInstanceState.getInt("timer");
            timer_running = savedInstanceState.getBoolean("timer_running");
            first_click = savedInstanceState.getBoolean("first_click");
            num_flags = savedInstanceState.getInt("num_flags");
        }
    }

    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("timer", timer);
        savedInstanceState.putBoolean("timer_running", timer_running);
        savedInstanceState.putBoolean("first_click", first_click);
        savedInstanceState.putInt("num_flags", num_flags);
    }

    // this method allocates mines to the grid
    public void setMines() {
        Random rand = new Random();

        for (int mine = 0; mine < NUM_MINES; mine++) {
            // get a random GridCell
            int cell_no = rand.nextInt(80);
            GridCell gc = cells.get(cell_no);

            // mark GridCell as a mine
            if (!gc.isMine) {
                gc.isMine = true;
            } else {
                mine--;
            }

            // update "danger" of neighboring cells
            for (int i = gc.row-1; i <= gc.row+1; i++) {
                for (int j = gc.column-1; j <= gc.column+1; j++) {
                    if (i >= 0 && i < ROW_COUNT && j >= 0 && j < COLUMN_COUNT
                            && (i != gc.row || j != gc.column)) {
                        int neighbor_cell_no = (i*COLUMN_COUNT)+j;
                        GridCell neighbor = cells.get(neighbor_cell_no);
                        neighbor.num_neighboring_mines++;
                    }
                }
            }
        }
    }

    private void runClock() {
        final TextView timeView = (TextView) findViewById(R.id.timer);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                @SuppressLint("DefaultLocale") String time = String.format("%02d", timer);
                timeView.setText(time);

                if (timer_running) {
                    timer++;
                }

                handler.postDelayed(this, 1000);
            }
        });
    }

    private void placeFlag(GridCell gc) {
        if (gc.isPicked)
            return;

        gc.setText("🚩");
        gc.isFlagged = true;

        final TextView flag_count = (TextView) findViewById(R.id.num_flags);
        num_flags--;
        flag_count.setText(String.valueOf(num_flags));
    }

    public void removeFlag(GridCell gc) {
        gc.isFlagged = false;
        gc.setText(String.valueOf(gc.num_neighboring_mines));

        final TextView flag_count = (TextView) findViewById(R.id.num_flags);
        num_flags++;
        flag_count.setText(String.valueOf(Math.max(0, num_flags)));
    }

    public void revealCell(GridCell gc) {
        gc.setBackgroundColor(Color.LTGRAY);

        if (gc.isMine) {
            gc.setText("\uD83D\uDCA3");
            userWon = false;
            gameIsOver = true;
        } else {
            cellsToReveal--;
            gc.setText(String.valueOf(gc.num_neighboring_mines));

            if (gc.num_neighboring_mines == 0) {
                revealNeighbors(gc);
            }

            gc.setTextColor(Color.GRAY);
            gc.setBackgroundColor(Color.LTGRAY);
        }

        if (cellsToReveal == 0) {
            gameIsOver = true;
            userWon = true;
        }
    }

    public void revealNeighbors(GridCell gc) {
        for (int i = gc.row-1; i <= gc.row+1; i++) {
            for (int j = gc.column-1; j <= gc.column+1; j++) {
                if (i >= 0 && i < ROW_COUNT && j >= 0 && j < COLUMN_COUNT
                        && (i != gc.row || j != gc.column)) {

                    int neighbor_cell_no = (i*COLUMN_COUNT)+j;
                    GridCell neighbor = cells.get(neighbor_cell_no);

                    neighbor.setText(String.valueOf(neighbor.num_neighboring_mines));
                    neighbor.setTextColor(Color.GRAY);
                    neighbor.setBackgroundColor(Color.LTGRAY);
                }
            }
        }
    }

    public void pickGridCell(GridCell gc) {
        if (gc.isPicked || gc.isFlagged) {
            return;
        }

        gc.isPicked = true;
        revealCell(gc);

        if (first_click) {
            timer_running = true;
            runClock();
            first_click = false;
        }
    }

    public void onClickGridCell(View view){
        if (gameIsOver) {
            endGame();
        }

        GridCell gc = (GridCell) view;

        if (mode == Mode.PICK_MODE) {
            pickGridCell(gc);
        } else { // mode == Mode.FLAG_MODE
            if (gc.isFlagged) {
                removeFlag(gc);
            } else if (num_flags > 0) {
                placeFlag(gc);
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

    public void endGame() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("won", userWon);
    }
}