package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    public static final boolean USER_RESULT = "won";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        boolean userWon = getBooleanExtra(USER_RESULT);

        TextView result_message = (TextView) findViewById(R.id.result_message);
        if (userWon) {
            result_message.setText("You won.");
        } else {
            result_message.setText("You lost.");
        }

        Button play_again_button = (Button) findViewById(R.id.play_again_button);
        play_again_button.setOnClickListener(this::onPlayAgain);
    }

    private void onPlayAgain(View view) {

    }
}