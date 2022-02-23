package com.example.modernbackgammon.logic;
import java.util.Random;

public class Die {

    private Random rnd;
    int lowerBound, upperBound;

    public Die(int lowerBound, int upperBound) {
        rnd = new Random();
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public int roll() {
        return rnd.nextInt(upperBound - lowerBound + 1) + lowerBound;
    }

}