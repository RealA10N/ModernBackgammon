package com.example.modernbackgammon.logic;

import androidx.annotation.NonNull;

import com.example.modernbackgammon.general.Hook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringJoiner;

public class GameBoard extends SmartBoard {

    Boolean whitesTurn = null;
    ArrayList<Integer> jumps;
    Stack<GameMoveRecordGroup> movesStack;
    Hook updateHook;

    public GameBoard(Hook updateHook) {
        super();
        this.updateHook = updateHook;
        movesStack = new Stack<>();
        jumps = new ArrayList<>();
    }

    public GameBoard(Hook updateHook, String repr) {
        this(updateHook);
        String[] sections = repr.split("!");

        whitesTurn = (sections[0] == "1" ? true : false);

        int i = 0;
        for (String s : sections[1].split(",")) {
            triangles[i++] = new Triangle(Integer.parseInt(s));
        }

        String[] specials = sections[2].split(",");
        whiteHome = new Triangle(Integer.parseInt(specials[0]));
        whiteEnd = new Triangle(Integer.parseInt(specials[1]));
        blackHome = new Triangle(Integer.parseInt(specials[2]));
        blackEnd = new Triangle(Integer.parseInt(specials[3]));

        jumps = new ArrayList<>();
        for (String j : sections[3].split(",")) jumps.add(Integer.parseInt(j));
    }

    public String repr() {
        StringJoiner sections = new StringJoiner("!");
        sections.add(isWhitesTurn()? "1" : "0");

        StringJoiner boardSection = new StringJoiner(",");
        for (int i = 0; i < triangles.length; i++) boardSection.add(getTriangle(i).repr());
        sections.add(boardSection.toString());

        StringJoiner specialSection = new StringJoiner(",");
        for (Triangle t : new Triangle[]{whiteHome, whiteEnd, blackHome, blackEnd})
            specialSection.add(t.repr());
        sections.add(specialSection.toString());

        StringJoiner jumpsSection = new StringJoiner(",");
        for (int j : jumps) jumpsSection.add(Integer.toString(j));
        sections.add(jumpsSection.toString());

        return sections.toString();
    }

    public boolean isWhitesTurn() { return whitesTurn != null && whitesTurn; }
    public boolean isBlacksTurn() { return whitesTurn != null && !whitesTurn; }
    public void flipTurn() {
        if (whitesTurn == null) whitesTurn = true;
        whitesTurn = !whitesTurn;
        movesStack = new Stack<>();
    }

    public boolean isEndTurn() {
        return jumps == null || jumps.size() == 0 || getAvailableMoves().isEmpty();
    }

    public boolean isValidMove(GameMove move) {
        if (move == null || move.from == null || move.to == null) return false;

        if (isWhitesTurn()) {
            if (move.from.countWhiteCheckers() < 1) return false;
            if (move.to.countBlackCheckers() > 1) return false;
            if (move.to == whiteEnd && !canRemoveWhites()) return false;
        }

        if (isBlacksTurn()) {
            if (move.from.countBlackCheckers() < 1) return false;
            if (move.to.countWhiteCheckers() > 1) return false;
            if (move.to == blackEnd && !canRemoveBlacks()) return false;
        }

        return true;
    }

    public void setAvailableJumps(ArrayList<Integer> jumps) {
        this.jumps = new ArrayList<>(jumps);
        updateHook.trigger();
    }

    public ArrayList<Integer> getAvailableJumps() { return new ArrayList<>(jumps); }

    @NonNull
    private HashMap<GameMove, Integer> getAvailableMoves() {
        HashMap<GameMove, Integer> moves = new HashMap<>();

        Triangle end, home; int starti, delta, endi;
        if (isWhitesTurn()) { end = whiteEnd; home = whiteHome; starti = triangles.length-1; delta = -1; endi = -1; }
        else { end = blackEnd; home = blackHome; starti = 0; delta = 1; endi = triangles.length; }

        if ((isWhitesTurn() && !whiteHome.isEmpty()) || isBlacksTurn() && !blackHome.isEmpty()) {
            // can't move regularly, has checkers that need to be pulled out of home

            for (int jump : jumps) {
                GameMove move = new GameMove(home, getTriangle(starti+((jump-1)*delta)));
                if (isValidMove(move)) moves.put(move, jump);
            }

        } else {
            // regular moves

            boolean seen = false;
            for (int i = starti; i != endi; i += delta) {
                for (int jump : jumps) {
                    GameMove move = new GameMove(getTriangle(i), getTriangle(i+(jump*delta)));
                    if (move.to == null) {
                        move.to = end;
                        Triangle before = getTriangle(i+((jump-1)*delta));
                        if ((!seen || before != null) && isValidMove(move)) moves.put(move, jump);
                    } else if (isValidMove(move)) moves.put(move, Math.abs(jump));

                    if (isWhitesTurn() && getTriangle(i).hasWhiteCheckers()) seen = true;
                    if (isBlacksTurn() && getTriangle(i).hasBlackCheckers()) seen = true;
                }
            }
        }

        return moves;
    }

    public boolean revertMove() {
        if (movesStack.empty()) return false;
        movesStack.pop().revertMoves(jumps);
        updateHook.trigger();
        return true;
    }

    public boolean applyMove(@NonNull GameMove move) {
        HashMap<GameMove, Integer> available = getAvailableMoves();
        Integer jump = available.get(move);
        if (jump == null) return false;

        GameMoveRecordGroup movesGroup = new GameMoveRecordGroup();

        if (isWhitesTurn() && move.to.hasBlackCheckers())
            movesGroup.push(new GameMoveRecord(move.to, blackHome, false));

        if (isBlacksTurn() && move.to.hasWhiteCheckers())
            movesGroup.push(new GameMoveRecord(move.to, whiteHome, true));

        movesGroup.push(new GameMoveRecord(move.from, move.to, isWhitesTurn(), jump));
        movesGroup.applyMoves(jumps);
        movesStack.push(movesGroup);
        updateHook.trigger();
        return true;
    }

}
