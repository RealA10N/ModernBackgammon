package com.example.modernbackgammon;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.modernbackgammon.logic.Board;
import com.example.modernbackgammon.logic.Triangle;

public class BoardDesign extends View {

    Bitmap blackChip, whiteChip, background;
    Board board;

    // canvas dimensions will stay updated.
    int canvasWidth = 0, canvasHeight = 0;
    final int CHIPS_IN_ROW = 12;

    Triangle movingTriangle;
    Bitmap movingChip;
    int movingX, movingY;
    boolean whitesTurn = true;

    public BoardDesign(Context context) {
        super(context);
        board = new Board();
        background = BitmapFactory.decodeResource(getResources(), R.drawable.gameboard);
        blackChip = BitmapFactory.decodeResource(getResources(), R.drawable.default_black_chip);
        whiteChip = BitmapFactory.decodeResource(getResources(), R.drawable.default_red_chip);
    }

    // --------------------------------------- LISTENERS ---------------------------------------- //

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas == null) return;
        super.onDraw(canvas);
        resizeBitmaps(canvas);
        drawBackground(canvas);
        drawBoardCheckers(canvas);
        drawMovingChecker(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        movingX = (int) event.getX(); movingY = (int) event.getY();
        Triangle triangle = locationToTriangle(movingX, movingY);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (triangle.hasWhiteCheckers() && whitesTurn) {
                movingChip = whiteChip;
                movingTriangle = triangle;
                triangle.removeWhiteChecker();
            } else if (triangle.hasBlackCheckers() && !whitesTurn) {
                movingChip = blackChip;
                movingTriangle = triangle;
                triangle.removeBlackChecker();
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP && movingTriangle != null) {
            if (whitesTurn) {
                if (triangle.countBlackCheckers() == 0) triangle.addWhiteChecker();
                else movingTriangle.addWhiteChecker();
            }
            else {
                if (triangle.countWhiteCheckers() == 0) triangle.addBlackChecker();
                else movingTriangle.addBlackChecker();
            }
            movingTriangle = null;
        }

        invalidate();
        return true;
    }

    // ---------------------------------------- DRAWERS ----------------------------------------- //

    public void drawBackground(Canvas canvas) {
        canvas.drawBitmap(background, 0,0,null);
    }

    public void drawBoardCheckers(@NonNull Canvas canvas) {
        int dx = canvas.getWidth() / CHIPS_IN_ROW;

        for (int i=0; i<CHIPS_IN_ROW; i++) {
            Triangle triangle = board.getTriangle(i);
            drawChipsTopRow(canvas, blackChip, i*dx, triangle.countBlackCheckers());
            drawChipsTopRow(canvas, whiteChip, i*dx, triangle.countWhiteCheckers());
        }

        for (int i=0; i<CHIPS_IN_ROW; i++) {
            int x = (CHIPS_IN_ROW - i - 1) * dx;
            Triangle triangle = board.getTriangle(CHIPS_IN_ROW + i);
            drawChipsBottomRow(canvas, blackChip, x, triangle.countBlackCheckers());
            drawChipsBottomRow(canvas, whiteChip, x, triangle.countWhiteCheckers());
        }
    }

    public void drawMovingChecker(Canvas canvas) {
        if (movingTriangle != null)
            drawChips(canvas, movingChip,
                    movingX - (movingChip.getWidth()/2),
                    movingY - (movingChip.getHeight()/2),
                    0, 1);

    }
    private void drawChips(Canvas canvas, Bitmap chip, int x, int y, int dy, int num) {
        for (int i=0; i<num; i++) canvas.drawBitmap(chip, x, y + (i*dy), null);
    }

    private void drawChipsTopRow(Canvas canvas, Bitmap chip, int x, int num) {
        if (num == 0) return;
        int height = (canvas.getHeight()/2) - chip.getHeight();
        int dy = Math.min(chip.getHeight(), height/num);
        int y = 0;
        drawChips(canvas, chip, x, y, dy, num);
    }

    private void drawChipsBottomRow(Canvas canvas, Bitmap chip, int x, int num) {
        if (num == 0) return;
        int height = canvas.getHeight()/2 - chip.getHeight();
        int dy = - Math.min(chip.getHeight(), height/num);
        int y = canvas.getHeight() - chip.getHeight();
        drawChips(canvas, chip, x, y, dy, num);
    }

    // ----------------------------------------- HELPERS ---------------------------------------- //

    private Triangle locationToTriangle(int x, int y) {
        int i = (x * CHIPS_IN_ROW) / canvasWidth;
        int j = (y * 2) / canvasHeight;
        if (j==1) i = CHIPS_IN_ROW - i - 1;
        return board.getTriangle((CHIPS_IN_ROW * j) + i);
    }

    public void resizeBitmaps(Canvas canvas) {
        if (canvas.getWidth() == canvasWidth && canvas.getHeight() == canvasHeight) return;
        canvasWidth = canvas.getWidth(); canvasHeight = canvas.getHeight();
        background = Bitmap.createScaledBitmap(background, canvasWidth, canvasHeight, false);

        int chipSize = canvasWidth / CHIPS_IN_ROW;
        blackChip = Bitmap.createScaledBitmap(blackChip, chipSize, chipSize, false);
        whiteChip = Bitmap.createScaledBitmap(whiteChip, chipSize, chipSize, false);
    }

}
