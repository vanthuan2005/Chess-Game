package com.example.chessgame;

import java.util.HashMap;
import java.util.ArrayList;

public class Chess {

    public static final int SIDE = 8;

    private int turn;
    private Piece piece1;
    private int[][] game;

    // Castling rights live here for the whole game.
    private boolean blackCastleRight;
    private boolean blackCastleLeft;
    private boolean whiteCastleRight;
    private boolean whiteCastleLeft;

    // Remember the selected piece so updateTurn() can permanently
    // remove castling rights after a king/rook actually moves.
    private int selectedRow = -1;
    private int selectedCol = -1;
    private int selectedPiece = 0;

    private HashMap<String, Integer> positionHistory;

    // One-level undo snapshot.
    private int[][] undoGame;
    private int undoTurn;
    private boolean undoBlackCastleRight;
    private boolean undoBlackCastleLeft;
    private boolean undoWhiteCastleRight;
    private boolean undoWhiteCastleLeft;
    private HashMap<String, Integer> undoPositionHistory;
    private boolean hasUndoState = false;

    public Chess() {
        game = new int[SIDE][SIDE];
        positionHistory = new HashMap<>();
        resetGame();
    }

    public int[][] play(int row, int col, int piece) {

        if (row >= 0 && col >= 0 &&
                row < SIDE && col < SIDE &&
                game[row][col] != 0) {

            syncCastlingRightsWithBoard();

            selectedRow = row;
            selectedCol = col;
            selectedPiece = piece;

            piece1 = new Piece(
                    row,
                    col,
                    piece,
                    game,
                    blackCastleRight,
                    blackCastleLeft,
                    whiteCastleRight,
                    whiteCastleLeft
            );

            if (turn == 1) {

                if (piece > 6) {
                    int[][] moves = piece1.possibleMoves();
                    return filterLegalMoves(row, col, moves);
                }

            } else {

                if (piece <= 6 && piece > 0) {
                    int[][] moves = piece1.possibleMoves();
                    return filterLegalMoves(row, col, moves);
                }
            }
        }

        return new int[SIDE][SIDE];
    }

    private int[][] filterLegalMoves(
            int oldRow,
            int oldCol,
            int[][] moves) {

        if (moves == null) {
            return new int[SIDE][SIDE];
        }

        for (int row = 0; row < SIDE; row++) {

            for (int col = 0; col < SIDE; col++) {

                if (moves[row][col] == 2 ||
                        moves[row][col] == 3) {

                    if (!isLegalMove(
                            oldRow,
                            oldCol,
                            row,
                            col)) {

                        moves[row][col] = 0;
                    }
                }

                // Keep original move codes 4/5, but a king
                // cannot castle while currently in check.
                if (moves[row][col] == 4 ||
                        moves[row][col] == 5) {

                    int movingPiece = game[oldRow][oldCol];

                    if ((movingPiece == 12 && isKingInCheck(1)) ||
                            (movingPiece == 6 && isKingInCheck(2))) {

                        moves[row][col] = 0;
                    }
                }
            }
        }

        return moves;
    }

    public boolean isLegalMove(
            int oldRow,
            int oldCol,
            int newRow,
            int newCol) {

        int movingPiece = game[oldRow][oldCol];
        int capturedPiece = game[newRow][newCol];

        game[newRow][newCol] = movingPiece;
        game[oldRow][oldCol] = 0;

        int side = movingPiece > 6 ? 1 : 2;

        boolean legal = !isKingInCheck(side);

        game[oldRow][oldCol] = movingPiece;
        game[newRow][newCol] = capturedPiece;

        return legal;
    }

    public boolean isKingInCheck(int side) {

        int king = side == 1 ? 12 : 6;

        int kingRow = -1;
        int kingCol = -1;

        for (int row = 0; row < SIDE; row++) {

            for (int col = 0; col < SIDE; col++) {

                if (game[row][col] == king) {

                    kingRow = row;
                    kingCol = col;
                    break;
                }
            }

            if (kingRow != -1) {
                break;
            }
        }

        if (kingRow == -1) {
            return true;
        }

        for (int row = 0; row < SIDE; row++) {

            for (int col = 0; col < SIDE; col++) {

                int p = game[row][col];

                if (p == 0) {
                    continue;
                }

                if (side == 1 &&
                        p >= 1 && p <= 6) {

                    Piece enemy =
                            new Piece(row, col, p, game);

                    int[][] moves =
                            enemy.possibleMoves();

                    if (moves != null &&
                            moves[kingRow][kingCol] == 3) {

                        return true;
                    }
                }

                if (side == 2 &&
                        p >= 7 && p <= 12) {

                    Piece enemy =
                            new Piece(row, col, p, game);

                    int[][] moves =
                            enemy.possibleMoves();

                    if (moves != null &&
                            moves[kingRow][kingCol] == 3) {

                        return true;
                    }
                }
            }
        }

        return false;
    }

    public int[] getKingPosition(int side) {

        int king = side == 1 ? 12 : 6;

        for (int row = 0; row < SIDE; row++) {

            for (int col = 0; col < SIDE; col++) {

                if (game[row][col] == king) {
                    return new int[]{row, col};
                }
            }
        }

        return new int[]{-1, -1};
    }

    public boolean hasAnyLegalMove(int side) {

        syncCastlingRightsWithBoard();

        for (int row = 0; row < SIDE; row++) {

            for (int col = 0; col < SIDE; col++) {

                int p = game[row][col];

                if (p == 0) {
                    continue;
                }

                if (side == 1 &&
                        (p < 7 || p > 12)) {
                    continue;
                }

                if (side == 2 &&
                        (p < 1 || p > 6)) {
                    continue;
                }

                Piece testPiece =
                        new Piece(
                                row,
                                col,
                                p,
                                game,
                                blackCastleRight,
                                blackCastleLeft,
                                whiteCastleRight,
                                whiteCastleLeft
                        );

                int[][] moves =
                        testPiece.possibleMoves();

                moves =
                        filterLegalMoves(
                                row,
                                col,
                                moves);

                for (int r = 0; r < SIDE; r++) {

                    for (int c = 0; c < SIDE; c++) {

                        if (moves[r][c] == 2 ||
                                moves[r][c] == 3 ||
                                moves[r][c] == 4 ||
                                moves[r][c] == 5) {

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean isCheckmate(int side) {

        return isKingInCheck(side)
                && !hasAnyLegalMove(side);
    }

    public boolean isStalemate(int side) {

        return !isKingInCheck(side)
                && !hasAnyLegalMove(side);
    }

    public boolean isDrawByStalemate() {

        if (turn == 1) {
            return isStalemate(1);
        }

        if (turn == 2) {
            return isStalemate(2);
        }

        return false;
    }

    public int[][] getGame() {

        return game;
    }

    // Old promotion methods kept for compatibility.
    public int[][] upgradeBlackPawn(
            int row,
            int col) {

        game[row][col] = 5;

        return game;
    }

    public int[][] upgradeWhitePawn(
            int row,
            int col) {

        game[row][col] = 11;

        return game;
    }

    // New promotion methods for Queen/Rook/Bishop/Knight selection.
    public int[][] upgradeBlackPawn(
            int row,
            int col,
            int piece) {

        game[row][col] = piece;

        return game;
    }

    public int[][] upgradeWhitePawn(
            int row,
            int col,
            int piece) {

        game[row][col] = piece;

        return game;
    }

    public int updateTurn() {

        updateCastlingRightsAfterMove();

        int currentTurn = turn;

        if (turn == 1) {
            turn = 2;
        } else {
            turn = 1;
        }

        selectedRow = -1;
        selectedCol = -1;
        selectedPiece = 0;

        return currentTurn;
    }

    private void updateCastlingRightsAfterMove() {

        if (selectedPiece == 6) {

            blackCastleLeft = false;
            blackCastleRight = false;
        }

        if (selectedPiece == 12) {

            whiteCastleLeft = false;
            whiteCastleRight = false;
        }

        if (selectedPiece == 2 &&
                selectedRow == 0 &&
                selectedCol == 0) {

            blackCastleLeft = false;
        }

        if (selectedPiece == 2 &&
                selectedRow == 0 &&
                selectedCol == SIDE - 1) {

            blackCastleRight = false;
        }

        if (selectedPiece == 8 &&
                selectedRow == SIDE - 1 &&
                selectedCol == 0) {

            whiteCastleLeft = false;
        }

        if (selectedPiece == 8 &&
                selectedRow == SIDE - 1 &&
                selectedCol == SIDE - 1) {

            whiteCastleRight = false;
        }

        syncCastlingRightsWithBoard();
    }

    private void syncCastlingRightsWithBoard() {

        if (game[0][0] != 2) {
            blackCastleLeft = false;
        }

        if (game[0][SIDE - 1] != 2) {
            blackCastleRight = false;
        }

        if (game[SIDE - 1][0] != 8) {
            whiteCastleLeft = false;
        }

        if (game[SIDE - 1][SIDE - 1] != 8) {
            whiteCastleRight = false;
        }

        if (game[0][4] != 6) {

            blackCastleLeft = false;
            blackCastleRight = false;
        }

        if (game[SIDE - 1][4] != 12) {

            whiteCastleLeft = false;
            whiteCastleRight = false;
        }
    }

    public int whoWon() {

        if (isCheckmate(1)) {
            return 1;
        }

        if (isCheckmate(2)) {
            return 2;
        }

        // Original fallback: king physically removed from board.
        int winner = 0;

        for (int r = 0; r < SIDE; r++) {

            for (int c = 0; c < SIDE; c++) {

                if (game[r][c] == 6) {

                    winner = 2;
                    break;
                }
            }
        }

        if (winner != 2) {
            return 2;
        }

        winner = 0;

        for (int r = 0; r < SIDE; r++) {

            for (int c = 0; c < SIDE; c++) {

                if (game[r][c] == 12) {

                    winner = 1;
                    break;
                }
            }
        }

        if (winner != 1) {
            return 1;
        }

        return 0;
    }

    public String result() {

        if (whoWon() == 1) {
            return "Black won";
        }

        if (whoWon() == 2) {
            return "White won";
        }

        if (isDrawByStalemate()) {
            return "Draw";
        }

        if (isFivefoldRepetition()) {
            return "Draw";
        }

        if (turn == 1) {
            return "White's turn";
        }

        if (turn == 2) {
            return "Black's turn";
        }

        return "If you're seeing this, something's gone wrong.";
    }


    public void saveStateForUndo() {

        undoGame = new int[SIDE][SIDE];

        for (int row = 0; row < SIDE; row++) {
            for (int col = 0; col < SIDE; col++) {
                undoGame[row][col] = game[row][col];
            }
        }

        undoTurn = turn;

        undoBlackCastleRight = blackCastleRight;
        undoBlackCastleLeft = blackCastleLeft;
        undoWhiteCastleRight = whiteCastleRight;
        undoWhiteCastleLeft = whiteCastleLeft;

        undoPositionHistory =
                new HashMap<>(positionHistory);

        hasUndoState = true;
    }

    public boolean undoLastMove() {

        if (!hasUndoState || undoGame == null) {
            return false;
        }

        for (int row = 0; row < SIDE; row++) {
            for (int col = 0; col < SIDE; col++) {
                game[row][col] = undoGame[row][col];
            }
        }

        turn = undoTurn;

        blackCastleRight = undoBlackCastleRight;
        blackCastleLeft = undoBlackCastleLeft;
        whiteCastleRight = undoWhiteCastleRight;
        whiteCastleLeft = undoWhiteCastleLeft;

        positionHistory =
                new HashMap<>(undoPositionHistory);

        selectedRow = -1;
        selectedCol = -1;
        selectedPiece = 0;

        hasUndoState = false;

        return true;
    }

    public boolean canUndo() {
        return hasUndoState;
    }

    public void resetGame() {

        for (int row = 0; row < SIDE; row++) {

            if (row == 0) {

                game[row][0] = 2;
                game[row][1] = 3;
                game[row][2] = 4;
                game[row][3] = 5;
                game[row][4] = 6;
                game[row][5] = 4;
                game[row][6] = 3;
                game[row][7] = 2;

            } else if (row == 7) {

                game[row][0] = 8;
                game[row][1] = 9;
                game[row][2] = 10;
                game[row][3] = 11;
                game[row][4] = 12;
                game[row][5] = 10;
                game[row][6] = 9;
                game[row][7] = 8;

            } else {

                for (int col = 0;
                     col < SIDE;
                     col++) {

                    if (row == 1) {
                        game[row][col] = 1;
                    } else if (row == 6) {
                        game[row][col] = 7;
                    } else {
                        game[row][col] = 0;
                    }
                }
            }
        }

        turn = 1;

        blackCastleRight = true;
        blackCastleLeft = true;
        whiteCastleRight = true;
        whiteCastleLeft = true;

        selectedRow = -1;
        selectedCol = -1;
        selectedPiece = 0;

        undoGame = null;
        undoPositionHistory = null;
        hasUndoState = false;

        if (positionHistory != null) {

            positionHistory.clear();
            recordCurrentPosition();
        }
    }

    public int getTurn() {

        return turn;
    }

    // Old MainActivity API kept intact.
    public void updateBlackCastleRight(
            boolean castle) {

        blackCastleRight = castle;
    }

    public void updateBlackCastleLeft(
            boolean castle) {

        blackCastleLeft = castle;
    }

    public void updateWhiteCastleRight(
            boolean castle) {

        whiteCastleRight = castle;
    }

    public void updateWhiteCastleLeft(
            boolean castle) {

        whiteCastleLeft = castle;
    }

    public boolean canBlackCastleRight() {
        return blackCastleRight;
    }

    public boolean canBlackCastleLeft() {
        return blackCastleLeft;
    }

    public boolean canWhiteCastleRight() {
        return whiteCastleRight;
    }

    public boolean canWhiteCastleLeft() {
        return whiteCastleLeft;
    }

    private String getPositionKey() {

        StringBuilder key =
                new StringBuilder();

        for (int row = 0; row < SIDE; row++) {

            for (int col = 0; col < SIDE; col++) {

                key.append(game[row][col])
                        .append(',');
            }
        }

        key.append("T")
                .append(turn);

        key.append("BCR")
                .append(blackCastleRight ? 1 : 0);

        key.append("BCL")
                .append(blackCastleLeft ? 1 : 0);

        key.append("WCR")
                .append(whiteCastleRight ? 1 : 0);

        key.append("WCL")
                .append(whiteCastleLeft ? 1 : 0);

        return key.toString();
    }

    public void recordCurrentPosition() {

        String key =
                getPositionKey();

        Integer count =
                positionHistory.get(key);

        if (count == null) {
            count = 0;
        }

        positionHistory.put(
                key,
                count + 1);
    }

    public boolean isFivefoldRepetition() {

        String key =
                getPositionKey();

        Integer count =
                positionHistory.get(key);

        return count != null
                && count >= 5;
    }
}
