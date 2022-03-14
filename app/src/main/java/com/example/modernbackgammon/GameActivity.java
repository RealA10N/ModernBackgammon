package com.example.modernbackgammon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.modernbackgammon.logic.Board;

public class GameActivity extends AppCompatActivity {

    protected BoardDesign displayBoard;
    protected FrameLayout container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        displayBoard = new BoardDesign(this);
        container = findViewById(R.id.container);
        container.addView(displayBoard);
    }

}