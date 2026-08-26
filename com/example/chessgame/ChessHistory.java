package com.example.chessgame;

import java.util.HashMap;

/** Owns repetition history and the one-level undo snapshot. */
class ChessHistory {
    static class Snapshot {
        final int[][] game;
        final int turn;
        final boolean blackCastleRight, blackCastleLeft, whiteCastleRight, whiteCastleLeft;
        final int[] whitePassant, blackPassant;

        Snapshot(int[][] game, int turn,
                 boolean bcr, boolean bcl, boolean wcr, boolean wcl,
                 int[] whitePassant, int[] blackPassant) {
            this.game = game;
            this.turn = turn;
            blackCastleRight = bcr;
            blackCastleLeft = bcl;
            whiteCastleRight = wcr;
            whiteCastleLeft = wcl;
            this.whitePassant = copyArray(whitePassant);
            this.blackPassant = copyArray(blackPassant);
        }
    }

    private HashMap<String, Integer> positionHistory = new HashMap<>();
    private Snapshot undoSnapshot;
    private HashMap<String, Integer> undoPositionHistory;

    void reset(int[][] game, int turn,
               boolean bcr, boolean bcl, boolean wcr, boolean wcl,
               int[] whitePassant, int[] blackPassant) {
        undoSnapshot = null;
        undoPositionHistory = null;
        positionHistory.clear();
        record(game, turn, bcr, bcl, wcr, wcl, whitePassant, blackPassant);
    }

    void saveUndo(int[][] game, int turn,
                  boolean bcr, boolean bcl, boolean wcr, boolean wcl,
                  int[] whitePassant, int[] blackPassant) {
        undoSnapshot = new Snapshot(
                copyBoard(game), turn, bcr, bcl, wcr, wcl,
                whitePassant, blackPassant);
        undoPositionHistory = new HashMap<>(positionHistory);
    }

    Snapshot restoreUndo() {
        if (undoSnapshot == null) return null;

        Snapshot result = undoSnapshot;
        positionHistory = new HashMap<>(undoPositionHistory);
        undoSnapshot = null;
        undoPositionHistory = null;
        return result;
    }

    boolean canUndo() {
        return undoSnapshot != null;
    }

    void record(int[][] game, int turn,
                boolean bcr, boolean bcl, boolean wcr, boolean wcl,
                int[] whitePassant, int[] blackPassant) {
        String key = key(game, turn, bcr, bcl, wcr, wcl, whitePassant, blackPassant);
        Integer count = positionHistory.get(key);
        if (count == null) count = 0;
        positionHistory.put(key, count + 1);
    }

    boolean isFivefold(int[][] game, int turn,
                       boolean bcr, boolean bcl, boolean wcr, boolean wcl,
                       int[] whitePassant, int[] blackPassant) {
        Integer count = positionHistory.get(
                key(game, turn, bcr, bcl, wcr, wcl, whitePassant, blackPassant));
        return count != null && count >= 5;
    }

    private int[][] copyBoard(int[][] game) {
        int[][] copy = new int[Chess.SIDE][Chess.SIDE];
        for (int r = 0; r < Chess.SIDE; r++)
            for (int c = 0; c < Chess.SIDE; c++)
                copy[r][c] = game[r][c];
        return copy;
    }

    private static int[] copyArray(int[] source) {
        if (source == null) return new int[Chess.SIDE];
        return source.clone();
    }

    private String key(int[][] game, int turn,
                       boolean bcr, boolean bcl, boolean wcr, boolean wcl,
                       int[] whitePassant, int[] blackPassant) {
        StringBuilder key = new StringBuilder();

        for (int row = 0; row < Chess.SIDE; row++)
            for (int col = 0; col < Chess.SIDE; col++)
                key.append(game[row][col]).append(',');

        key.append("T").append(turn);
        key.append("BCR").append(bcr ? 1 : 0);
        key.append("BCL").append(bcl ? 1 : 0);
        key.append("WCR").append(wcr ? 1 : 0);
        key.append("WCL").append(wcl ? 1 : 0);

        key.append("WEP");
        appendArray(key, whitePassant);
        key.append("BEP");
        appendArray(key, blackPassant);

        return key.toString();
    }

    private void appendArray(StringBuilder key, int[] array) {
        if (array == null) {
            for (int i = 0; i < Chess.SIDE; i++) key.append('0');
            return;
        }

        for (int i = 0; i < Chess.SIDE; i++) {
            // Only value 1 means an en-passant right is currently active.
            // Values 0 and 2 are both equivalent to "no en-passant right"
            // for repetition purposes.
            key.append(array[i] == 1 ? '1' : '0');
        }
    }
}
