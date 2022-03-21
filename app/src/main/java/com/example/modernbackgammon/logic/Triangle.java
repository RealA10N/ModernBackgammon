package com.example.modernbackgammon.logic;

public class Triangle {
    /*
     * A point (triangle) in backgammon is represented as a single integer. if
     * value=0 Empty triangle. if value>0 |value| white checkers. if value<0 |value|
     * black checkers.
     */

    private int value = 0;

    final static String whiteChecker = "⚪️";
    final static String blackChecker = "⚫️";

    public Triangle(int initial) {
        value = initial;
    }

    @Override
    public String toString() {
        // create a string made up of n copies of string s
        if (value > 0) {
            return String.format("%0" + value + "d", 0).replace("0", whiteChecker);
        } else {
            return String.format("%0" + (-value) + "d", 0).replace("0", blackChecker);
        }
    }

    public void clear() { value = 0; }
    public int countCheckers() { return Math.abs(value); }
    public int countWhiteCheckers() {
        return Math.max(0, value);
    }
    public int countBlackCheckers() {
        return Math.max(0, -value);
    }

    // - - - Getters - - - //

    public boolean isEmpty() {
        return value == 0;
    }
    public boolean hasWhiteCheckers() {
        return value > 0;
    }
    public boolean hasBlackCheckers() {
        return value < 0;
    }

    // - - - House Getters - - - //

    public boolean isWhiteHouse() {
        return value > 1;
    }

    public boolean isBlackHouse() {
        return value < -1;
    }

    public boolean isHouse() {
        return isWhiteHouse() || isBlackHouse();
    }

    // - - - Add Checkers - - - //

    public boolean addWhiteChecker() {
        if (isBlackHouse())
            return false;
        value++;
        return true;

    }

    public boolean addBlackChecker() {
        if (isWhiteHouse())
            return false;
        value--;
        return true;
    }

    // - - - Remove Checkers - - - //

    public boolean removeWhiteChecker() {
        if (!hasWhiteCheckers())
            return false;
        value--;
        return true;
    }

    public boolean removeBlackChecker() {
        if (!hasBlackCheckers())
            return false;
        value++;
        return true;
    }
}
