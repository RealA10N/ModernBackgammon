package com.example.modernbackgammon.logic;

import androidx.annotation.NonNull;

import java.util.HashSet;

public class GameBoard extends Board {

    boolean whitesTurn;

    public GameBoard(boolean whitesStart) {
        super();
        whitesTurn = whitesStart;
    }

    public boolean isWhitesTurn() { return whitesTurn; }
    public boolean isBlacksTurn() { return !whitesTurn; }

    public boolean isValidMove(GameMove move) {
        if (move == null) return false;
        Triangle from = getTriangle(move.from), to = getTriangle(move.to);
        if (from == null || to == null) return false;

        if (isWhitesTurn()) {
            if (from.countWhiteCheckers() < 1) return false;
            if (to.countBlackCheckers() > 1) return false;
        }

        if (isBlacksTurn()) {
            if (from.countBlackCheckers() < 1) return false;
            if (to.countWhiteCheckers() > 1) return false;
        }

        return true;
    }

    HashSet<GameMove> getAvailableMoves(int[] jumps) {
        HashSet<GameMove> moves = new HashSet<GameMove>();

        for (int jump : jumps) {
            if (whitesTurn) jump *= -1;
            for (int i = 0; i < triangles.length; i++) {
                GameMove move = new GameMove(i, i+jump);
                if (isValidMove(move)) moves.add(move);
            }
        }

        return moves;
    }

    public boolean applyMove(@NonNull GameMove move) {
        if (!isValidMove(move)) return false;
        Triangle from = getTriangle(move.from), to = getTriangle(move.to);

        if (isWhitesTurn()) {
            from.removeWhiteChecker();
            if (to.hasBlackCheckers()) to.clear();
            to.addWhiteChecker();
        }

        if (isBlacksTurn()) {
            from.removeBlackChecker();
            if (to.hasWhiteCheckers()) to.clear();
            to.addBlackChecker();
        }

        whitesTurn = !whitesTurn;
        return true;
    }

}
