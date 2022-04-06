package com.example.modernbackgammon.logic;

public class GameMoveRecord {
    Triangle from, to;
    int jump;
    boolean eats;

    GameMoveRecord(Triangle from, Triangle to, int jump, boolean eats) {
        this.from = from;
        this.to = to;
        this.jump = jump;
        this.eats = eats;
    }
}
