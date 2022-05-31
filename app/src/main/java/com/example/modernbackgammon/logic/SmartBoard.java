package com.example.modernbackgammon.logic;

import com.example.modernbackgammon.general.Hook;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SmartBoard extends GameBoard {

    public SmartBoard(Hook updateHook) { super(updateHook); }
    public SmartBoard(Hook updateHook, boolean hack) { super(updateHook, hack); }
    public SmartBoard(Hook updateHook, String repr) { super(updateHook, repr); }

    // ----------------------------------- STATE EVALUATION ------------------------------------ //

    public static final int SINGLE_SCORE_PENALTY = 3;

    public int calculateScore() {
        // Positive score : white winning
        // Negative score : black winning

        return blackScore() - whiteScore();
    }

    protected int blackScore() {
        int sum = 0;
        for (int i = 0; i < triangles.length; i++) {
            sum += triangles[i].countBlackCheckers() * (triangles.length - i);
            if (triangles[i].countBlackCheckers() == 1) sum += SINGLE_SCORE_PENALTY;
        }
        sum += triangles.length * blackHome.countCheckers();
        return sum;
    }

    protected int whiteScore() {
        int sum = 0;
        for (int i = 0; i < triangles.length; i++) {
            sum += triangles[i].countWhiteCheckers() * (i + 1);
            if (triangles[i].countWhiteCheckers() == 1) sum += SINGLE_SCORE_PENALTY;
        }
        sum += triangles.length * whiteHome.countCheckers();
        return sum;
    }

    // ------------------------------------- GAME PROGRESS -------------------------------------- //

    protected HashSet<GameMoveRecordGroup> getAvailableMoveGroups() {
        return getAvailableMoveGroups(new GameMoveRecordGroup());
    }

    private HashSet<GameMoveRecordGroup> getAvailableMoveGroups(GameMoveRecordGroup cur) {
        // returns all possible full moves (move groups)

        HashMap<GameMove, Integer> moves = getAvailableMoves();
        HashSet<GameMoveRecordGroup> ans = new HashSet<>();

        if (moves.isEmpty()) {
            ans.add(new GameMoveRecordGroup(cur));
            return ans;
        }

        for (Map.Entry<GameMove, Integer> moveEntry : moves.entrySet()) {
            GameMove move = moveEntry.getKey();
            GameMoveRecordGroup records = applyMove(move);
            cur.push(records);
            ans.addAll(getAvailableMoveGroups(cur));
            cur.multipop(records.size());
            revertMove();
        }

        return ans;
    }

    public GameMoveRecordGroup bestScoreMove() {
        Set<GameMoveRecordGroup> moves = getAvailableMoveGroups();
        if (moves.isEmpty()) return null;

        int mul = (isWhitesTurn()? 1 : -1);
        int max = Integer.MIN_VALUE;
        GameMoveRecordGroup ans = null;

        for (GameMoveRecordGroup record : moves) {
            record.applyMoves(jumps);
            if (mul * calculateScore() > max) {
                max = mul * calculateScore();
                ans = record;
            }
            record.revertMoves(jumps);
        }

        return ans;
    }

    public void playBestMove() {
        bestScoreMove().applyMoves(jumps);
    }

}
