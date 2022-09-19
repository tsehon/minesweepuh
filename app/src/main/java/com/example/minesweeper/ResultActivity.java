package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    public static final String USER_WON = "USER_WON";
    public static final String SECONDS_ELAPSED = "SECONDS_ELAPSED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent result = getIntent();
        boolean userWon = result.getBooleanExtra(USER_WON, false);
        int seconds_elapsed = result.getIntExtra(SECONDS_ELAPSED, 0);

        TextView time_elapsed = (TextView) findViewById(R.id.timer_message);
        String timer_message = "Used " + seconds_elapsed + " seconds.";
        time_elapsed.setText(timer_message);

        TextView result_message = (TextView) findViewById(R.id.result_message);
        TextView result_sub_message = (TextView) findViewById(R.id.result_submessage);

        if (userWon) {
            result_message.setText("You won.");
            result_sub_message.setText("Good job!");
        } else {
            result_message.setText("You lost.");
            result_sub_message.setText("Nice try.");
        }

        Button play_again_button = (Button) findViewById(R.id.play_again_button);
        play_again_button.setText("Play Again");
        play_again_button.setOnClickListener(this::onPlayAgain);
    }

    private void onPlayAgain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}