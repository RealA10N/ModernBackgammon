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

    public static final int SINGLE_SCORE_PENALTY = 2;
    public static final int HOUSE_MOTIVATION = 2;
    public static final int OUTSIDE_MOTIVATION = 8;
    public static final int LAST_QUARTER_PENALTY = 2;
    public static final int LAST_EIGHTH_PENALTY = 2;
    public static final int TALL_HOUSE_PENALTY = 2;

    public int calculateScore() {
        // Positive score : white winning
        // Negative score : black winning

        return blackScore() - whiteScore();
    }

    protected int blackScore() {
        int sum = 0;
        for (int i = 0; i < triangles.length; i++) {
            sum += triangles[i].countBlackCheckers() * (triangles.length - i);
            sum += triangles[i].countBlackCheckers() * (triangles.length - i);
            if (triangles[i].countBlackCheckers() == 1) sum += SINGLE_SCORE_PENALTY;
            if (triangles[i].isBlackHouse()) sum -= HOUSE_MOTIVATION;
            if (triangles[i].countBlackCheckers() > 3) sum += TALL_HOUSE_PENALTY;
        }

        for (int i=0; i<BOARD_SIZE/4; i++)
            sum += triangles[i].countBlackCheckers() * LAST_QUARTER_PENALTY;
        for (int i=0; i<BOARD_SIZE/8; i++)
            sum += triangles[i].countBlackCheckers() * LAST_EIGHTH_PENALTY;
        sum += triangles.length * blackHome.countCheckers();
        sum -= blackEnd.countBlackCheckers() * OUTSIDE_MOTIVATION;
        return sum;
    }

    protected int whiteScore() {
        int sum = 0;
        for (int i = 0; i < triangles.length; i++) {
            sum += triangles[i].countWhiteCheckers() * (i + 1);
            sum += triangles[i].countWhiteCheckers() * (i + 1);
            if (triangles[i].countWhiteCheckers() == 1) sum += SINGLE_SCORE_PENALTY;
            if (triangles[i].isWhiteHouse()) sum -= HOUSE_MOTIVATION;
            if (triangles[i].countWhiteCheckers() > 3) sum += TALL_HOUSE_PENALTY;
        }
        for (int i=0; i<BOARD_SIZE/4; i++)
            sum += triangles[BOARD_SIZE-i-1].countWhiteCheckers() * LAST_QUARTER_PENALTY;
        for (int i=0; i<BOARD_SIZE/8; i++)
            sum += triangles[BOARD_SIZE-i-1].countWhiteCheckers() * LAST_EIGHTH_PENALTY;
        sum += triangles.length * whiteHome.countCheckers();
        sum -= blackEnd.countWhiteCheckers() * OUTSIDE_MOTIVATION;
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

}
