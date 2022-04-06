package com.example.modernbackgammon.logic;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.Stack;

public class GameBoard extends Board {

    boolean whitesTurn;
    int[] jumps;
    Stack<GameMoveRecord> movesStack;

    public GameBoard(boolean whitesStart) {
        super();
        whitesTurn = whitesStart;
        movesStack = new Stack<>();
    }

    public boolean isWhitesTurn() { return whitesTurn; }
    public boolean isBlacksTurn() { return !whitesTurn; }
    public void flipTurn() {
        whitesTurn = !whitesTurn;
        movesStack = new Stack<>();
    }

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

    public void setAvailableMoves(int[] jumps) {
        this.jumps = jumps;
    }

    private HashSet<GameMove> getAvailableMoves() {
        HashSet<GameMove> moves = new HashSet<>();
        for (int jump : jumps) {
            if (whitesTurn) jump *= -1;
            for (int i = 0; i < triangles.length; i++) {
                GameMove move = new GameMove(i, i+jump);
                if (isValidMove(move)) moves.add(move);
            }
        }
        return moves;
    }

    public boolean revertMove() {
        if (movesStack.empty()) return false;
        GameMoveRecord record = movesStack.pop();

        if (isWhitesTurn()) {
            record.to.removeWhiteChecker();
            record.from.addWhiteChecker();
            if (record.eats) record.to.addBlackChecker();
        } else {
            record.to.removeBlackChecker();
            record.from.addBlackChecker();
            if (record.eats) record.to.addWhiteChecker();
        }

        return true;
    }

    public boolean applyMove(@NonNull GameMove move) {
        HashSet<GameMove> available = getAvailableMoves();
        if (!available.contains(move)) return false;

        Triangle from = getTriangle(move.from), to = getTriangle(move.to);
        boolean eats = false;

        if (isWhitesTurn()) {
            from.removeWhiteChecker();
            if (to.hasBlackCheckers()) { to.clear(); eats = true; }
            to.addWhiteChecker();
        }

        if (isBlacksTurn()) {
            from.removeBlackChecker();
            if (to.hasWhiteCheckers()) { to.clear(); eats = true; }
            to.addBlackChecker();
        }

        GameMoveRecord record = new GameMoveRecord(from, to, eats);
        movesStack.push(record);

        return true;
    }

}
