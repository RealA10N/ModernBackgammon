package com.example.modernbackgammon;

import android.view.View;
import android.widget.ImageView;

import com.example.modernbackgammon.general.GameActivityUpdateHook;
import com.example.modernbackgammon.general.GameStateStorage;
import com.example.modernbackgammon.logic.GameMoveRecordGroup;
import com.example.modernbackgammon.logic.SmartBoard;

import java.util.Timer;
import java.util.TimerTask;

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

        displayBoard.setEnabled(board.isWhitesTurn() && !board.isEndTurn());

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

    protected void applyMoveGroupAnimation(GameMoveRecordGroup group) {
        final int TIME_INTERVAL = 750;

        for (int i = 1; i <= group.size(); i++) {
            int moveIndex = i - 1;
            new Timer().schedule(new TimerTask() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            group.applyMove(moveIndex, board.jumps);
                            update();
                        }
                    });
                }
            }, i * TIME_INTERVAL);
        }
    }

    public void playAsAI() {
        SmartBoard board = (SmartBoard) this.board;
        applyMoveGroupAnimation(board.bestScoreMove());
        update();
    }

}