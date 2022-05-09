package com.example.modernbackgammon.logic;

import java.util.Locale;

public class Board {

    static final int[] initial = { -2, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, -5, 5, 0, 0, 0, -3, 0, -5, 0, 0, 0, 0, 2 };
    static final int coloredCheckers = 15;
    protected Triangle[] triangles;
    public Triangle whiteHome, blackHome, whiteEnd, blackEnd;

    public Board() {
        triangles = new Triangle[initial.length];
        for (int i = 0; i < initial.length; i++) {
            triangles[i] = new Triangle(initial[i]);
        }

        whiteHome = new Triangle(0);
        blackHome = new Triangle(0);
        whiteEnd = new Triangle(0);
        blackEnd = new Triangle(0);
    }

    public Triangle getTriangle(int id) {
        if (id < 0 || id >= triangles.length) return null;
        return triangles[id];
    }

    public int countHomeWhites() { return whiteHome.countCheckers(); }
    public boolean hasHomeWhites() {
        return !whiteHome.isEmpty();
    }

    public int countHomeBlacks() { return blackHome.countCheckers(); }
    public boolean hasHomeBlacks() {
        return !blackHome.isEmpty();
    }

    public boolean isEndGame() {
        return whiteEnd.countCheckers() == coloredCheckers || blackEnd.countCheckers() == coloredCheckers;
    }

    public boolean canRemoveWhites() {
        for (int i = 6; i < triangles.length; i++) {
            if (triangles[i].hasWhiteCheckers())
                return false;
        }
        return true;
    }

    public boolean canRemoveBlacks() {
        for (int i = 0; i < triangles.length - 6; i++) {
            if (triangles[i].hasBlackCheckers())
                return false;
        }

        return true;
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < triangles.length; i++) {
            str += String.format(Locale.ENGLISH, "%2d | %s\n", i + 1, triangles[i]);
        }

        // Remove last '\n' char
        return str.substring(0, str.length() - 1);
    }

}