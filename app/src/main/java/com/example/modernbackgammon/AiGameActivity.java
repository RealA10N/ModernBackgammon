package com.example.modernbackgammon;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.modernbackgammon.general.GameActivityUpdateHook;
import com.example.modernbackgammon.general.GameStateStorage;
import com.example.modernbackgammon.logic.SmartBoard;

public class AiGameActivity extends GameActivity {

    @Override
    protected void buildActivity(boolean hack) {
        GameStateStorage.storeGameMode(true);
        if (hack) board = new SmartBoard(new GameActivityUpdateHook(this), true);
        else board = GameStateStorage.loadGameBoard(new GameActivityUpdateHook(this));
        displayBoard = new BoardDesign(this, board);
        container = findViewById(R.id.container);
        container.addView(displayBoard);
        allJumps = board.getAvailableJumps();
        update();
    }

    @Override
    public void update() {
        super.update();

        // roll icon
        ImageView roll = findViewById(R.id.roll_icon);
        roll.setImageResource(
            (board.isWhitesTurn()?
                R.drawable.circle_button_selector :
                R.drawable.dice_button_selector)
        );

        // undo icon
        ImageView undo = findViewById(R.id.undo_icon);
        if (board.isBlacksTurn()) undo.setEnabled(false);
    }

    public void roll(View view) {
        super.roll(view);
        if (board.isBlacksTurn()) playAsAI();
        update();
    }

    public void playAsAI() {
        Toast.makeText(this, "Thinking...", Toast.LENGTH_SHORT).show();
        SmartBoard board = (SmartBoard) this.board;
        board.playBestMove();
        update();
    }

}