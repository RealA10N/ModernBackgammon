package com.example.modernbackgammon.logic;

import java.util.ArrayList;
import java.util.Collections;

public class GameMoveRecordGroup {
    protected ArrayList<GameMoveRecord> records;

    public GameMoveRecordGroup() { records = new ArrayList<>(); }
    public GameMoveRecordGroup(GameMoveRecordGroup group) {
        records = new ArrayList<>(group.records);
    }

    void push(GameMoveRecord record) {
        records.add(record);
    }

    void push(GameMoveRecordGroup group) {
        for (GameMoveRecord record : group.records) push(record);
    }

    void pop() { records.remove(records.size()-1); }
    void multipop(int n) { for(int i=0; i<n; i++) pop(); }
    int size() { return records.size(); }

    public void applyMoves(ArrayList<Integer> jumps) {
        for (GameMoveRecord record : records)
            record.applyMove(jumps);
    }

    public void revertMoves(ArrayList<Integer> jumps) {
        ArrayList<GameMoveRecord> reversed = new ArrayList<>(records);
        Collections.reverse(reversed);
        for (GameMoveRecord record : reversed)
            record.revertMove(jumps);
    }
}
