package com.example.modernbackgammon.logic;

import java.util.ArrayList;
import java.util.Collections;

public class GameMoveRecordGroup {
    protected ArrayList<GameMoveRecord> records;

    public GameMoveRecordGroup() { records = new ArrayList<>(); }

    void push(GameMoveRecord record) {
        records.add(record);
    }

    void applyMoves(ArrayList<Integer> jumps) {
        for (GameMoveRecord record : records)
            record.applyMove(jumps);
    }

    void revertMoves(ArrayList<Integer> jumps) {
        ArrayList<GameMoveRecord> reversed = new ArrayList<>(records);
        Collections.reverse(reversed);
        for (GameMoveRecord record : reversed)
            record.revertMove(jumps);
    }
}
