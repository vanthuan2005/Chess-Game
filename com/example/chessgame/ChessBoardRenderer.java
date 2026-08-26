package com.example.chessgame;

import android.app.Activity;
import android.widget.Button;

/** Handles only board drawing/highlighting. No chess rules are implemented here. */
class ChessBoardRenderer {
    private final Activity activity;
    private final Button[][] buttons;
    private final Chess chessGame;
    private int[][] game;

    ChessBoardRenderer(Activity activity, Button[][] buttons, int[][] game, Chess chessGame) {
        this.activity = activity;
        this.buttons = buttons;
        this.game = game;
        this.chessGame = chessGame;
    }

    void setGame(int[][] game) { this.game = game; }

    void resetButtons() {
        for (int row = 0; row < Chess.SIDE; row++)
            for (int col = 0; col < Chess.SIDE; col++) movePiece(row, col);
    }

    void enableButtons(boolean enabled) {
        for (int row = 0; row < Chess.SIDE; row++)
            for (int col = 0; col < Chess.SIDE; col++) buttons[row][col].setEnabled(enabled);
    }

    void resetBackgrounds() {
        int i = 0;
        int p = 1;
        for (int r = 0; r < Chess.SIDE - 1; r += 2) {
            for (int c = 0; c < Chess.SIDE; c++) {
                buttons[r + i][c].setBackground(activity.getDrawable(R.drawable.white_box));
                buttons[r + p][c].setBackground(activity.getDrawable(R.drawable.black_box));
                i = i == 0 ? 1 : 0;
                p = p == 0 ? 1 : 0;
            }
        }
    }

    void movePiece(int row, int col) {
        int value = game[row][col];
        if (value == 1) buttons[row][col].setForeground(activity.getDrawable(R.drawable.black_pawn));
        if (value == 2) buttons[row][col].setForeground(activity.getDrawable(R.drawable.black_castle));
        if (value == 3) buttons[row][col].setForeground(activity.getDrawable(R.drawable.black_horse));
        if (value == 4) buttons[row][col].setForeground(activity.getDrawable(R.drawable.black_bishop));
        if (value == 5) buttons[row][col].setForeground(activity.getDrawable(R.drawable.black_queen));
        if (value == 6) buttons[row][col].setForeground(activity.getDrawable(R.drawable.black_king));
        if (value == 7) buttons[row][col].setForeground(activity.getDrawable(R.drawable.white_pawn));
        if (value == 8) buttons[row][col].setForeground(activity.getDrawable(R.drawable.white_castle));
        if (value == 9) buttons[row][col].setForeground(activity.getDrawable(R.drawable.white_horse));
        if (value == 10) buttons[row][col].setForeground(activity.getDrawable(R.drawable.white_bishop));
        if (value == 11) buttons[row][col].setForeground(activity.getDrawable(R.drawable.white_queen));
        if (value == 12) buttons[row][col].setForeground(activity.getDrawable(R.drawable.white_king));
        if (value == 0) buttons[row][col].setForeground(null);
    }

    void highlightCheckedKing() {
        if (chessGame.isKingInCheck(1)) {
            int[] kingPos = chessGame.getKingPosition(1);
            if (kingPos[0] >= 0 && kingPos[1] >= 0)
                buttons[kingPos[0]][kingPos[1]].setBackground(activity.getDrawable(R.drawable.red_box));
        }
        if (chessGame.isKingInCheck(2)) {
            int[] kingPos = chessGame.getKingPosition(2);
            if (kingPos[0] >= 0 && kingPos[1] >= 0)
                buttons[kingPos[0]][kingPos[1]].setBackground(activity.getDrawable(R.drawable.red_box));
        }
    }
}
