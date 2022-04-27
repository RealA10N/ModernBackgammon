package com.example.modernbackgammon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.modernbackgammon.general.Hook;
import com.example.modernbackgammon.logic.GameBoard;

import java.util.Random;

class GameActivityUpdateHook extends Hook {
    GameActivity activity;
    GameActivityUpdateHook(GameActivity activity) {
        this.activity = activity;
    }

    @Override
    public void trigger() {
        this.activity.update();
    }
}

public class GameActivity extends AppCompatActivity {

    protected GameBoard board;
    protected BoardDesign displayBoard;
    protected FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        board = new GameBoard(new GameActivityUpdateHook(this), true);
        displayBoard = new BoardDesign(this, board);
        container = findViewById(R.id.container);
        container.addView(displayBoard);
    }

    public void roll(View btn) {
        TextView text = findViewById(R.id.dicetext);
        Random rnd = new Random();
        int a = 1+rnd.nextInt(6), b= 1+rnd.nextInt(6);
        board.flipTurn();
        board.setAvailableMoves(new int[]{a,b });
        text.setText(String.format("%s move %d, %d", (board.isWhitesTurn()? "Red" : "Black"), a, b));
    }

    public void revert(View btn) {
        if (!board.revertMove()) Toast.makeText(this, "No moves to revert!", Toast.LENGTH_SHORT).show();
        update();
    }

    public void update() {
        displayBoard.invalidate();
        Button roll = findViewById(R.id.roller);
        roll.setEnabled(board.isEndTurn());
    }
}