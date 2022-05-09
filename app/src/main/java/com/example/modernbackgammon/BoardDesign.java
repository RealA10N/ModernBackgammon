package com.example.modernbackgammon;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.modernbackgammon.logic.GameBoard;
import com.example.modernbackgammon.logic.GameMove;
import com.example.modernbackgammon.logic.Triangle;

import java.util.Date;

public class BoardDesign extends View {

    Bitmap background, blackChip, whiteChip, highlightImg;
    GameBoard board;

    // canvas dimensions will stay updated.
    int canvasWidth = 0, canvasHeight = 0;
    final int CHIPS_IN_ROW = 12;

    long last_touch_down = 0;

    GameMove move = null;
    Bitmap movingChip = null;
    int movingX, movingY;

    private boolean state_enabled = true;

    public BoardDesign(Context context, GameBoard board) {
        super(context);
        this.board = board;
        background = BitmapFactory.decodeResource(getResources(), R.drawable.gameboard);
        blackChip = BitmapFactory.decodeResource(getResources(), R.drawable.default_black_chip);
        whiteChip = BitmapFactory.decodeResource(getResources(), R.drawable.default_red_chip);
        // highlight: TODO
    }

    public void setEnabled(boolean enabled) { state_enabled = enabled; }
    public boolean isEnabled() { return state_enabled; }

    // --------------------------------------- LISTENERS ---------------------------------------- //

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas == null) return;
        super.onDraw(canvas);
        resizeBitmaps(canvas);
        drawBackground(canvas);
        drawHighlights(canvas);
        drawBoardCheckers(canvas);
        drawMovingChecker(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return true;
        long time = new Date().getTime();

        movingX = (int) event.getX(); movingY = (int) event.getY();
        int triangleId = locationToTriangleId(movingX, movingY);
        Triangle triangle = board.getTriangle(triangleId);

        if (event.getAction() == MotionEvent.ACTION_DOWN && triangle != null) {
            if (move != null) {
                // if starting point is already selected
                move.to = triangle;
                board.applyMove(move);
                move = null;
                movingChip = null;

            } else {
                // if a start of a new move
                last_touch_down = time;
                if (board.isWhitesTurn() && triangle.hasWhiteCheckers()) {
                    movingChip = whiteChip;
                    move = new GameMove(triangle, null);
                } else if (board.isBlacksTurn() && triangle.hasBlackCheckers()) {
                    movingChip = blackChip;
                    move = new GameMove(triangle, null);
                }
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP && triangle != null && move != null) {
            if (time - last_touch_down >= 100) {
                move.to = triangle;
                board.applyMove(move);
                move = null;
                last_touch_down = 0;
            }
            movingChip = null;
        }

        invalidate();
        return true;
    }

    public void onHomeClick() {
        if (move != null && movingChip == null) {
            if (board.isWhitesTurn()) move.to = board.whiteEnd;
            else move.to = board.blackEnd;
            board.applyMove(move);
            move = null;
        }
    }

    // ---------------------------------------- DRAWERS ----------------------------------------- //

    public void drawBackground(Canvas canvas) {
        canvas.drawBitmap(background, 0,0,null);
    }

    public void drawBoardCheckers(@NonNull Canvas canvas) {
        int dx = canvas.getWidth() / CHIPS_IN_ROW;

        for (int i=0; i<CHIPS_IN_ROW; i++) {
            Triangle triangle = board.getTriangle(i);
            int num = triangle.countCheckers();
            if (movingChip != null && move.from == board.getTriangle(i)) num--;
            if (triangle.hasBlackCheckers()) drawChipsTopRow(canvas, blackChip, i*dx, num);
            else drawChipsTopRow(canvas, whiteChip, i*dx, num);
        }

        for (int i=0; i<CHIPS_IN_ROW; i++) {
            int x = (CHIPS_IN_ROW - i - 1) * dx;
            Triangle triangle = board.getTriangle(CHIPS_IN_ROW + i);
            int num = triangle.countCheckers();
            if (movingChip != null && move.from == board.getTriangle(CHIPS_IN_ROW + i)) num--;
            if (triangle.hasBlackCheckers()) drawChipsBottomRow(canvas, blackChip, x, num);
            else drawChipsBottomRow(canvas, whiteChip, x, num);
        }
    }

    public void drawMovingChecker(Canvas canvas) {
        if (movingChip != null) {
            Bitmap[] chips = new Bitmap[1];
            chips[0] = movingChip;
            drawChips(canvas, chips,
                    movingX - (movingChip.getWidth()/2),
                    movingY - (movingChip.getHeight()/2),
                    0, 1);
        }
    }

    private void drawHighlights(Canvas canvas) {
        if (move == null || move.from == null) return;
        canvas.drawBitmap(whiteChip, 100,100,null);
        // highlight: TODO
    }

    private void drawChips(Canvas canvas, Bitmap[] chips, int x, int y, int dy, int num) {
        for (int i=0; i<num; i++) canvas.drawBitmap(chips[i], x, y + (i*dy), null);
    }

    private void drawChipsTopRow(Canvas canvas, Bitmap chip, int x, int num) {
        int y = 0, dy = 0;
        int height = (canvas.getHeight()/2) - chip.getHeight();
        if (num != 0) dy = Math.min(chip.getHeight(), height/num);

        Bitmap[] chips = new Bitmap[num];
        for (int i=0; i<num; i++) chips[i] = chip;
        drawChips(canvas, chips, x, y, dy, num);
    }

    private void drawChipsBottomRow(Canvas canvas, Bitmap chip, int x, int num) {
        int dy=0, y = canvas.getHeight() - chip.getHeight();
        int height = canvas.getHeight()/2 - chip.getHeight();
        if (num != 0) dy = -Math.min(chip.getHeight(), height/num);

        Bitmap[] chips = new Bitmap[num];
        for (int i=0; i<num; i++) chips[i] = chip;
        drawChips(canvas, chips, x, y, dy, num);
    }

    // ----------------------------------------- HELPERS ---------------------------------------- //

    private int locationToTriangleId(int x, int y) {
        if (x < 0 || x >= canvasWidth || y < 0 || y >= canvasHeight) return -1;
        int i = (x * CHIPS_IN_ROW) / canvasWidth;
        int j = (y * 2) / canvasHeight;
        if (j==1) i = CHIPS_IN_ROW - i - 1;
        return (CHIPS_IN_ROW * j) + i;
    }

    public void resizeBitmaps(Canvas canvas) {
        if (canvas.getWidth() == canvasWidth && canvas.getHeight() == canvasHeight) return;
        canvasWidth = canvas.getWidth(); canvasHeight = canvas.getHeight();
        background = Bitmap.createScaledBitmap(background, canvasWidth, canvasHeight, false);

        int chipSize = canvasWidth / CHIPS_IN_ROW;
        blackChip = Bitmap.createScaledBitmap(blackChip, chipSize, chipSize, false);
        whiteChip = Bitmap.createScaledBitmap(whiteChip, chipSize, chipSize, false);
//        highlightImg = Bitmap.createScaledBitmap(highlightImg, chipSize, canvasHeight/2, false); TODO
    }
}
