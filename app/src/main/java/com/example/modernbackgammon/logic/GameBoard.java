package com.example.modernbackgammon.logic;

import androidx.annotation.NonNull;

import com.example.modernbackgammon.general.Hook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class GameBoard extends Board {

    boolean whitesTurn;
    ArrayList<Integer> jumps;
    Stack<GameMoveRecordGroup> movesStack;
    Hook updateHook;

    public GameBoard(Hook updateHook, boolean whitesStart) {
        super();
        this.updateHook = updateHook;
        whitesTurn = whitesStart;
        movesStack = new Stack<>();
    }

    public boolean isWhitesTurn() { return whitesTurn; }
    public boolean isBlacksTurn() { return !whitesTurn; }
    public void flipTurn() {
        whitesTurn = !whitesTurn;
        movesStack = new Stack<>();
    }

    public boolean isEndTurn() {
        return jumps == null || jumps.size() == 0;
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

    public void setAvailableMoves(@NonNull int[] jumps) {
        this.jumps = new ArrayList<>();
        for (int j : jumps) this.jumps.add(j);
        updateHook.trigger();
    }

    @NonNull
    private HashMap<GameMove, Integer> getAvailableMoves() {
        HashMap<GameMove, Integer> moves = new HashMap<>();
        for (int jump : jumps) {
            if (whitesTurn) jump *= -1;
            for (int i = 0; i < triangles.length; i++) {
                GameMove move = new GameMove(i, i+jump);
                if (isValidMove(move)) moves.put(move, Math.abs(jump));
            }
        }
        return moves;
    }

    public boolean revertMove() {
        if (movesStack.empty()) return false;
        movesStack.pop().revertMoves(jumps);
        updateHook.trigger();
        return true;
    }

    public boolean applyMove(@NonNull GameMove move) {
        HashMap<GameMove, Integer> available = getAvailableMoves();
        Integer jump = available.get(move);
        if (jump == null) return false;

        GameMoveRecordGroup movesGroup = new GameMoveRecordGroup();
        Triangle from = getTriangle(move.from), to = getTriangle(move.to);

        if (isWhitesTurn() && to.hasBlackCheckers())
            movesGroup.push(new GameMoveRecord(to, blackHome, false));

        if (isBlacksTurn() && to.hasWhiteCheckers())
            movesGroup.push(new GameMoveRecord(to, whiteHome, true));

        movesGroup.push(new GameMoveRecord(from, to, isWhitesTurn(), jump));
        movesGroup.applyMoves(jumps);
        movesStack.push(movesGroup);
        updateHook.trigger();
        return true;
    }

}
