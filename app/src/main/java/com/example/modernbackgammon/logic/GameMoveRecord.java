package com.example.modernbackgammon.logic;

import java.util.ArrayList;

public class GameMoveRecord {
    Triangle from, to;
    boolean whitesMove;
    Integer jump = null;

    GameMoveRecord(Triangle from, Triangle to, boolean whitesMove) {
        this.from = from;
        this.to = to;
        this.whitesMove = whitesMove;
    }

    GameMoveRecord(Triangle from, Triangle to, boolean whitesMove, Integer jump) {
        this(from, to, whitesMove);
        this.jump = jump;
    }

    public void applyMove(ArrayList<Integer> jumps) {
        if (whitesMove) {
            from.removeWhiteChecker();
            to.addWhiteChecker();
        } else {
            from.removeBlackChecker();
            to.addBlackChecker();
        }

        if (jump != null) jumps.remove(jump);
    }

    public void revertMove(ArrayList<Integer> jumps) {
        if (whitesMove) {
            to.removeWhiteChecker();
            from.addWhiteChecker();
        } else {
            to.removeBlackChecker();
            from.addBlackChecker();
        }

        if (jump != null) jumps.add(jump);
    }

}