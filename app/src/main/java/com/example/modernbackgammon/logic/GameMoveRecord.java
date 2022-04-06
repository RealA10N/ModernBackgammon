package com.example.modernbackgammon.logic;

public class GameMoveRecord {
    Triangle from, to;
    boolean eats;

    GameMoveRecord(Triangle from, Triangle to, boolean eats) {
        this.from = from;
        this.to = to;
        this.eats = eats;
    }
}
