package com.example.modernbackgammon.logic;


import java.util.Objects;

public class GameMove {
    public Triangle from, to;

    public GameMove(Triangle from, Triangle to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameMove gameMove = (GameMove) o;
        return from == gameMove.from && to == gameMove.to;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
