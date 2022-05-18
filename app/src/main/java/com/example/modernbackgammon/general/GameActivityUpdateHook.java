package com.example.modernbackgammon.general;

import com.example.modernbackgammon.GameActivity;

public class GameActivityUpdateHook extends Hook {
    GameActivity activity;
    public GameActivityUpdateHook(GameActivity activity) {
        this.activity = activity;
    }

    @Override
    public void trigger() {
        this.activity.update();
    }
}