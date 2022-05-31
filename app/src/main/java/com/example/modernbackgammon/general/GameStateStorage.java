package com.example.modernbackgammon.general;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.modernbackgammon.GameActivity;
import com.example.modernbackgammon.logic.GameBoard;
import com.example.modernbackgammon.logic.SmartBoard;

public class GameStateStorage {

    private static SharedPreferences sp;
    private final static String SP_KEY = "board";
    private final static String AI_KEY = "ai";

    public static void loadDatabase(Context context) {
        sp = context.getSharedPreferences("sp", MODE_PRIVATE);
    }

    public static boolean hasGameStored() {
        return !sp.getString(SP_KEY, "").equals("");
    }

    public static GameBoard loadGameBoard(GameActivityUpdateHook hook) {
        if (isGameModeAI()) {
            if (!hasGameStored()) return new SmartBoard(hook);
            else return new SmartBoard(hook, sp.getString(SP_KEY, ""));
        } else {
            if (!hasGameStored()) return new GameBoard(hook);
            else return new GameBoard(hook, sp.getString(SP_KEY, ""));
        }
    }

    public static void storeGameMode(boolean ai) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(AI_KEY, ai);
        editor.commit();
    }

    public static boolean isGameModeAI() {
        return sp.getBoolean(AI_KEY, false);
    }

    public static void storeGameBoardState(GameBoard board) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SP_KEY, board.repr());
        editor.commit();
    }

    public static void resetGameBoardState() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SP_KEY, "");
        editor.commit();
    }
}
