package com.example.modernbackgammon.logic;

import java.util.Locale;

public class Board {

    static final int[] initial = { -2, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, -5, 5, 0, 0, 0, -3, 0, -5, 0, 0, 0, 0, 2 };
    protected Triangle[] triangles;

    public Board() {
        triangles = new Triangle[initial.length];
        for (int i = 0; i < initial.length; i++) {
            triangles[i] = new Triangle(initial[i]);
        }
    }

    public Triangle getTriangle(int id) {
        if (id < 0 || id >= triangles.length) return null;
        return triangles[id];
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