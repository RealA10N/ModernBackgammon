package com.example.modernbackgammon;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.modernbackgammon.general.Hook;
import com.example.modernbackgammon.logic.GameBoard;

import java.util.ArrayList;
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
    protected SharedPreferences sp;

    private final String SP_KEY = "gamestate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        sp=getSharedPreferences("game", MODE_PRIVATE);
        buildActivity();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        buildActivity();
    }

    protected void buildActivity() {
        String repr = sp.getString(SP_KEY, "");
        if (repr.isEmpty()) board = new GameBoard(new GameActivityUpdateHook(this));
        else board = new GameBoard(new GameActivityUpdateHook(this), repr);
        displayBoard = new BoardDesign(this, board);
        container = findViewById(R.id.container);
        container.addView(displayBoard);
    }

    public void roll(View btn) {
        saveGameState();

        Random rnd = new Random();
        int a = 1+rnd.nextInt(6), b= 1+rnd.nextInt(6);
        board.flipTurn();

        ArrayList<Integer> jumps = new ArrayList<>();
        for (int i=0; i <= (a==b? 1 : 0); i++) {
            jumps.add(a); jumps.add(b);
        }

        board.setAvailableJumps(jumps);
        if (board.isEndTurn()) Toast.makeText(this, "No moves available!", Toast.LENGTH_SHORT).show();
    }

    public void revert(View btn) {
        if (!board.revertMove()) Toast.makeText(this, "No moves to revert!", Toast.LENGTH_SHORT).show();
        update();
    }

    public void homeClick(View btn) {
        displayBoard.onHomeClick();
    }

    protected void saveGameState() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SP_KEY, board.repr());
        editor.commit();
    }

    protected void deleteSavedGameState() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SP_KEY, "");
        editor.commit();
    }

    public void update() {
        displayBoard.invalidate();
        Button roll = findViewById(R.id.roller);
        roll.setEnabled(board.isEndTurn());

        TextView house = findViewById(R.id.hometext);
        house.setText(String.format("Eaten: %d whites, %d blacks", board.countHomeWhites(), board.countHomeBlacks()));

        TextView text = findViewById(R.id.dicetext);
        String s = (board.isWhitesTurn()? "Whites" : "Blacks") + " move ";
        for (int jump : board.getAvailableJumps()) {
            s += String.format("%d ", jump);
        }
        text.setText(s);

        displayBoard.setEnabled(!board.isEndTurn());
        if (board.isEndGame()) onGameEnd();
    }

    public void onGameEnd() {
        deleteSavedGameState();

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.game_end_dialog);
        dialog.setCancelable(false);
        dialog.show();

        TextView title = dialog.findViewById(R.id.end_game_dialog_winner);
        title.setText((board.isWhiteWin()? "White" : "Black"));

        Button home = dialog.findViewById(R.id.end_game_dialog_home_btn);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); dialog.dismiss(); }
        });

        Button newgame = dialog.findViewById(R.id.end_game_new_game_btn);
        newgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onRestart(); dialog.dismiss(); }
        });
    }

}