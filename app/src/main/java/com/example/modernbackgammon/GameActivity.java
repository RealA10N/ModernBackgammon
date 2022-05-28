package com.example.modernbackgammon;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.modernbackgammon.general.GameActivityUpdateHook;
import com.example.modernbackgammon.general.GameStateStorage;
import com.example.modernbackgammon.logic.GameBoard;

import java.util.ArrayList;
import java.util.Random;


public class GameActivity extends AppCompatActivity {

    protected GameBoard board;
    protected BoardDesign displayBoard;
    protected FrameLayout container;

    ArrayList<Integer> allJumps;
    int[] diceImageDrawables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        diceImageDrawables = new int[]{
                R.drawable.die1,
                R.drawable.die2,
                R.drawable.die3,
                R.drawable.die4,
                R.drawable.die5,
                R.drawable.die6,
        };

        boolean hack = getIntent().getBooleanExtra("hack", false);
        buildActivity(hack);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        buildActivity();
    }

    protected void buildActivity() { buildActivity(false); }

    protected void buildActivity(boolean hack) {
        if (hack) board = new GameBoard(new GameActivityUpdateHook(this), true);
        else board = GameStateStorage.loadGameBoard(new GameActivityUpdateHook(this));
        displayBoard = new BoardDesign(this, board);
        container = findViewById(R.id.container);
        container.addView(displayBoard);
        allJumps = board.getAvailableJumps();
        update();
    }

    public void roll(View view) {
        Random rnd = new Random();
        int a = 1+rnd.nextInt(6), b= 1+rnd.nextInt(6);
        board.flipTurn();

        allJumps = new ArrayList<>();
        for (int i=0; i <= (a==b? 1 : 0); i++) {
            allJumps.add(a); allJumps.add(b);
        }

        board.setAvailableJumps(allJumps);
        if (board.isEndTurn()) Toast.makeText(this, R.string.no_available_moves, Toast.LENGTH_SHORT).show();

        GameStateStorage.storeGameBoardState(board);
    }

    public void revert(View view) {
        board.revertMove();
        update();
    }

    public void homeClick(View btn) {
        displayBoard.onHomeClick();
    }

    public void update() {
        displayBoard.invalidate();

        // icons

        ImageView roll = findViewById(R.id.roll_icon);
        roll.setEnabled(board.isEndTurn());

        ImageView undo = findViewById(R.id.undo_icon);
        undo.setEnabled(board.canRevertMove());

        // text

        TextView text = findViewById(R.id.whos_turn_text);
        text.setText(board.isWhitesTurn()? R.string.white_turn : R.string.black_turn);

        TextView eaten = findViewById(R.id.eaten_text);
        String eaten_text = "";
        if (board.countHomeBlacks() > 0)
            eaten_text += getString(R.string.x_black_eaten, board.countHomeBlacks()) + "\n";
        if (board.countHomeWhites() > 0)
            eaten_text += getString(R.string.x_white_eaten, board.countHomeWhites()) + "\n";

        eaten.setText(eaten_text);
        eaten.setVisibility((eaten_text.isEmpty()? View.INVISIBLE : View.VISIBLE));

        // dice

        TableLayout table = findViewById(R.id.dice_table);
        table.removeAllViews();

        final int DICE_IN_ROW = 2;
        float dp = getResources().getDisplayMetrics().density;
        int rows = (allJumps.size()+(DICE_IN_ROW-1)) / DICE_IN_ROW;

        for (int r=0; r<rows; r++) {
            TableRow row = new TableRow(this);
            table.addView(row);
            for (int i = r*DICE_IN_ROW; i<Math.min((r+1)*DICE_IN_ROW, allJumps.size()); i++) {
                int v = allJumps.get(i);
                ImageView img = new ImageView(this);
                img.setImageResource(diceImageDrawables[v-1]);
                img.setLayoutParams(new TableRow.LayoutParams((int)(72*dp),(int)(72*dp)));
                row.addView(img);
            }
        }

        displayBoard.setEnabled(!board.isEndTurn());
        if (board.isEndGame()) onGameEnd();
    }

    public void onGameEnd() {
        GameStateStorage.resetGameBoardState();

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.game_end_dialog);
        dialog.setCancelable(false);
        dialog.show();

        TextView title = dialog.findViewById(R.id.end_game_dialog_winner);
        title.setText((board.isWhiteWin()? R.string.white : R.string.black));

        Button home = dialog.findViewById(R.id.end_game_dialog_home_btn);
        home.setOnClickListener(v -> { finish(); dialog.dismiss(); });

        Button newgame = dialog.findViewById(R.id.end_game_new_game_btn);
        newgame.setOnClickListener(v -> { onRestart(); dialog.dismiss(); });
    }

    public void goToHome(View view) { finish(); }

}