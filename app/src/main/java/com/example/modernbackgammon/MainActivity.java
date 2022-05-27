package com.example.modernbackgammon;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.modernbackgammon.general.GameStateStorage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GameStateStorage.loadDatabase(this);

        findViewById(R.id.new_game_btn)
                .setOnLongClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("hack", true);
            startActivity(intent);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.resume_game_btn).setVisibility(
                (GameStateStorage.hasGameStored()? View.VISIBLE : View.INVISIBLE)
        );
    }


    public void resumeGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void newGame(View view) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.new_game_dialog);
        dialog.setCancelable(true);
        dialog.show();

        Button btn = dialog.findViewById(R.id.pvp_button);
        btn.setOnClickListener(v -> {
            GameStateStorage.resetGameBoardState();
            Intent intent = new Intent(this, GameActivity.class);
            dialog.dismiss();
            startActivity(intent);
        });


        btn = dialog.findViewById(R.id.pvai_button);
        btn.setOnClickListener(v -> {
            GameStateStorage.resetGameBoardState();
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("pvai", true);
            Toast.makeText(this, "AI not yet implemented. Proceeding with local PvP.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            startActivity(intent);
        });
    }

}