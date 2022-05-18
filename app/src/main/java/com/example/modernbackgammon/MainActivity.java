package com.example.modernbackgammon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.modernbackgammon.general.GameStateStorage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GameStateStorage.loadDatabase(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.resume_game_btn).setVisibility(
                (GameStateStorage.hasGameStored()? View.VISIBLE : View.INVISIBLE)
        );
    }

    public void newGame(View view) {
        GameStateStorage.resetGameBoardState();
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void resumeGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

}