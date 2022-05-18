package com.example.modernbackgammon.general;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.modernbackgammon.logic.GameBoard;

public class GameStateStorage {

    private static SharedPreferences sp;
    private final static String SP_KEY = "board";

    public static void loadDatabase(Context context) {
        sp = context.getSharedPreferences("sp", MODE_PRIVATE);
    }

    public static boolean hasGameStored() {
        return !sp.getString(SP_KEY, "").equals("");
    }

    public static GameBoard loadGameBoard(GameActivityUpdateHook hook) {
        if (!hasGameStored()) return new GameBoard(hook);
        else return new GameBoard(hook, sp.getString(SP_KEY, ""));
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
