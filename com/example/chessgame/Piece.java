package com.example.chessgame;

/**
 * Lightweight chess-piece facade. Movement generation is delegated to
 * PieceMoveGenerator so this class keeps one clear responsibility.
 */
public class Piece {
    private final PieceMoveGenerator moveGenerator;

    public Piece(int r, int c, int p, int[][] g) {
        moveGenerator = new PieceMoveGenerator(r, c, p, g);
    }

    public Piece(int r, int c, int p, int[][] g,
                 boolean blackCastleRight,
                 boolean blackCastleLeft,
                 boolean whiteCastleRight,
                 boolean whiteCastleLeft) {
        moveGenerator = new PieceMoveGenerator(
                r, c, p, g,
                blackCastleRight, blackCastleLeft,
                whiteCastleRight, whiteCastleLeft);
    }

    public int[][] possibleMoves() {
        return moveGenerator.possibleMoves();
    }
}
