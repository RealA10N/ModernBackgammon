package com.example.modernbackgammon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.modernbackgammon.logic.Board;
import com.example.modernbackgammon.logic.GameBoard;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    protected GameBoard board;
    protected BoardDesign displayBoard;
    protected FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        board = new GameBoard(true);
        displayBoard = new BoardDesign(this, board);
        container = findViewById(R.id.container);
        container.addView(displayBoard);
    }

    public void roll(View btn) {
        TextView text = findViewById(R.id.dicetext);
        Random rnd = new Random();
        int a = 1+rnd.nextInt(6), b= 1+rnd.nextInt(6);
        board.flipTurn();
        board.setAvailableMoves(new int[]{a, b});
        text.setText(String.format("%s move %d", (board.isWhitesTurn()? "Whitred" : "Black"), a));
    }
}