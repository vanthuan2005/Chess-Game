package com.example.chessgame;

public class Chess {
    public static final int SIDE = 8;

    private int turn;
    private Piece piece1;
    private int[][] game;

    private boolean blackCastleRight;
    private boolean blackCastleLeft;
    private boolean whiteCastleRight;
    private boolean whiteCastleLeft;

    private int[] whitePassant;
    private int[] blackPassant;

    private int selectedRow = -1;
    private int selectedCol = -1;
    private int selectedPiece = 0;

    private final ChessHistory history;

    public Chess() {
        game = new int[SIDE][SIDE];
        whitePassant = new int[SIDE];
        blackPassant = new int[SIDE];
        history = new ChessHistory();
        resetGame();
    }

    public int[][] play(int row, int col, int piece) {
        if (row >= 0 && col >= 0 && row < SIDE && col < SIDE && game[row][col] != 0) {
            syncCastlingRightsWithBoard();
            selectedRow = row;
            selectedCol = col;
            selectedPiece = piece;

            piece1 = new Piece(row, col, piece, game,
                    blackCastleRight, blackCastleLeft,
                    whiteCastleRight, whiteCastleLeft);

            if (turn == 1) {
                if (piece > 6) return rules().filterLegalMoves(row, col, piece1.possibleMoves());
            } else {
                if (piece <= 6 && piece > 0) return rules().filterLegalMoves(row, col, piece1.possibleMoves());
            }
        }
        return new int[SIDE][SIDE];
    }

    private ChessRules rules() {
        return new ChessRules(
                game,
                blackCastleRight, blackCastleLeft,
                whiteCastleRight, whiteCastleLeft,
                whitePassant, blackPassant);
    }

    public boolean isLegalMove(int oldRow, int oldCol, int newRow, int newCol) {
        return rules().isLegalMove(oldRow, oldCol, newRow, newCol);
    }

    public boolean isKingInCheck(int side) {
        return rules().isKingInCheck(side);
    }

    public int[] getKingPosition(int side) {
        return rules().getKingPosition(side);
    }

    public boolean hasAnyLegalMove(int side) {
        syncCastlingRightsWithBoard();
        return rules().hasAnyLegalMove(side);
    }

    public boolean isCheckmate(int side) {
        return isKingInCheck(side) && !hasAnyLegalMove(side);
    }

    public boolean isStalemate(int side) {
        return !isKingInCheck(side) && !hasAnyLegalMove(side);
    }

    public boolean isDrawByStalemate() {
        if (turn == 1) return isStalemate(1);
        if (turn == 2) return isStalemate(2);
        return false;
    }

    public int[][] getGame() {
        return game;
    }

    public int[][] upgradeBlackPawn(int row, int col) {
        game[row][col] = 5;
        return game;
    }

    public int[][] upgradeWhitePawn(int row, int col) {
        game[row][col] = 11;
        return game;
    }

    public int[][] upgradeBlackPawn(int row, int col, int piece) {
        game[row][col] = piece;
        return game;
    }

    public int[][] upgradeWhitePawn(int row, int col, int piece) {
        game[row][col] = piece;
        return game;
    }

    public int updateTurn() {
        updateCastlingRightsAfterMove();
        int currentTurn = turn;
        turn = turn == 1 ? 2 : 1;
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
        if (selectedPiece == 2 && selectedRow == 0 && selectedCol == 0) blackCastleLeft = false;
        if (selectedPiece == 2 && selectedRow == 0 && selectedCol == SIDE - 1) blackCastleRight = false;
        if (selectedPiece == 8 && selectedRow == SIDE - 1 && selectedCol == 0) whiteCastleLeft = false;
        if (selectedPiece == 8 && selectedRow == SIDE - 1 && selectedCol == SIDE - 1) whiteCastleRight = false;
        syncCastlingRightsWithBoard();
    }

    private void syncCastlingRightsWithBoard() {
        if (game[0][0] != 2) blackCastleLeft = false;
        if (game[0][SIDE - 1] != 2) blackCastleRight = false;
        if (game[SIDE - 1][0] != 8) whiteCastleLeft = false;
        if (game[SIDE - 1][SIDE - 1] != 8) whiteCastleRight = false;

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
        if (isCheckmate(1)) return 1;
        if (isCheckmate(2)) return 2;

        int winner = 0;
        for (int r = 0; r < SIDE; r++) {
            for (int c = 0; c < SIDE; c++) {
                if (game[r][c] == 6) {
                    winner = 2;
                    break;
                }
            }
        }
        if (winner != 2) return 2;

        winner = 0;
        for (int r = 0; r < SIDE; r++) {
            for (int c = 0; c < SIDE; c++) {
                if (game[r][c] == 12) {
                    winner = 1;
                    break;
                }
            }
        }
        if (winner != 1) return 1;
        return 0;
    }

    public String result() {
        if (whoWon() == 1) return "Black won";
        if (whoWon() == 2) return "White won";
        if (isDrawByStalemate()) return "Draw";
        if (isFivefoldRepetition()) return "Draw";
        if (turn == 1) return "White's turn";
        if (turn == 2) return "Black's turn";
        return "If you're seeing this, something's gone wrong.";
    }

    public void saveStateForUndo() {
        history.saveUndo(
                game, turn,
                blackCastleRight, blackCastleLeft,
                whiteCastleRight, whiteCastleLeft,
                whitePassant, blackPassant);
    }

    public boolean undoLastMove() {
        ChessHistory.Snapshot snapshot = history.restoreUndo();
        if (snapshot == null) return false;

        for (int row = 0; row < SIDE; row++)
            for (int col = 0; col < SIDE; col++)
                game[row][col] = snapshot.game[row][col];

        turn = snapshot.turn;
        blackCastleRight = snapshot.blackCastleRight;
        blackCastleLeft = snapshot.blackCastleLeft;
        whiteCastleRight = snapshot.whiteCastleRight;
        whiteCastleLeft = snapshot.whiteCastleLeft;
        whitePassant = snapshot.whitePassant.clone();
        blackPassant = snapshot.blackPassant.clone();

        selectedRow = -1;
        selectedCol = -1;
        selectedPiece = 0;
        return true;
    }

    public boolean canUndo() {
        return history.canUndo();
    }

    public void resetGame() {
        for (int row = 0; row < SIDE; row++) {
            if (row == 0) {
                game[row][0] = 2; game[row][1] = 3; game[row][2] = 4; game[row][3] = 5;
                game[row][4] = 6; game[row][5] = 4; game[row][6] = 3; game[row][7] = 2;
            } else if (row == 7) {
                game[row][0] = 8; game[row][1] = 9; game[row][2] = 10; game[row][3] = 11;
                game[row][4] = 12; game[row][5] = 10; game[row][6] = 9; game[row][7] = 8;
            } else {
                for (int col = 0; col < SIDE; col++) {
                    if (row == 1) game[row][col] = 1;
                    else if (row == 6) game[row][col] = 7;
                    else game[row][col] = 0;
                }
            }
        }

        turn = 1;
        blackCastleRight = true;
        blackCastleLeft = true;
        whiteCastleRight = true;
        whiteCastleLeft = true;
        whitePassant = new int[SIDE];
        blackPassant = new int[SIDE];
        selectedRow = -1;
        selectedCol = -1;
        selectedPiece = 0;

        history.reset(
                game, turn,
                blackCastleRight, blackCastleLeft,
                whiteCastleRight, whiteCastleLeft,
                whitePassant, blackPassant);
    }

    public int getTurn() {
        return turn;
    }

    public void setEnPassantState(int[] whitePassant, int[] blackPassant) {
        this.whitePassant =
                whitePassant == null ? new int[SIDE] : whitePassant.clone();
        this.blackPassant =
                blackPassant == null ? new int[SIDE] : blackPassant.clone();
    }

    public void updateBlackCastleRight(boolean castle) { blackCastleRight = castle; }
    public void updateBlackCastleLeft(boolean castle) { blackCastleLeft = castle; }
    public void updateWhiteCastleRight(boolean castle) { whiteCastleRight = castle; }
    public void updateWhiteCastleLeft(boolean castle) { whiteCastleLeft = castle; }
    public boolean canBlackCastleRight() { return blackCastleRight; }
    public boolean canBlackCastleLeft() { return blackCastleLeft; }
    public boolean canWhiteCastleRight() { return whiteCastleRight; }
    public boolean canWhiteCastleLeft() { return whiteCastleLeft; }

    public void recordCurrentPosition() {
        history.record(
                game, turn,
                blackCastleRight, blackCastleLeft,
                whiteCastleRight, whiteCastleLeft,
                whitePassant, blackPassant);
    }

    public boolean isFivefoldRepetition() {
        return history.isFivefold(
                game, turn,
                blackCastleRight, blackCastleLeft,
                whiteCastleRight, whiteCastleLeft,
                whitePassant, blackPassant);
    }
}
