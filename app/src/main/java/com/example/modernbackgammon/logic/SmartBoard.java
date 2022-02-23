package com.example.modernbackgammon.logic;


import java.util.Locale;

public class SmartBoard extends Board {

    public int whiteScore() {
        int sum = 0;
        for (int i = 0; i < triangles.length; i++) {
            sum += triangles[i].countWhiteCheckers() * (i + 1);
        }
        return sum;
    }

    public int blackScore() {
        int sum = 0;
        for (int i = 0; i < triangles.length; i++) {
            sum += triangles[i].countBlackCheckers() * (triangles.length - i);
        }
        return sum;
    }

    public int score() { return whiteScore() - blackScore(); }

    @Override
    public String toString() {
        String str = super.toString();
        str += String.format(Locale.ENGLISH, "\n\n⬜️ %d | ⬛️ %d", whiteScore(), blackScore());
        return str;
    }
}
