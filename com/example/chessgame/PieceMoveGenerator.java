package com.example.chessgame;

class PieceMoveGenerator {
    private final int row;
    private final int col;
    private final int piece;
    private final int[][] game;
    private final boolean blackCastleRight;
    private final boolean blackCastleLeft;
    private final boolean whiteCastleRight;
    private final boolean whiteCastleLeft;

    PieceMoveGenerator(int r, int c, int p, int[][] g) {
        this(r, c, p, g, false, false, false, false);
    }

    PieceMoveGenerator(int r, int c, int p, int[][] g,
                       boolean blackCastleRight, boolean blackCastleLeft,
                       boolean whiteCastleRight, boolean whiteCastleLeft) {
        row = r; col = c; piece = p; game = g;
        this.blackCastleRight = blackCastleRight;
        this.blackCastleLeft = blackCastleLeft;
        this.whiteCastleRight = whiteCastleRight;
        this.whiteCastleLeft = whiteCastleLeft;
    }

    int[][] possibleMoves() {
        int[][] moves = new int[Chess.SIDE][Chess.SIDE];
        PawnMoveGenerator pawn = new PawnMoveGenerator(row, col, game);
        KnightMoveGenerator knight = new KnightMoveGenerator(row, col, game);
        SlidingMoveGenerator sliding = new SlidingMoveGenerator(row, col, game);
        KingMoveGenerator king = new KingMoveGenerator(row, col, game,
                blackCastleRight, blackCastleLeft, whiteCastleRight, whiteCastleLeft);

        if (piece == 1) return pawn.blackPawn(moves);
        if (piece == 2) return sliding.blackCastle(moves);
        if (piece == 3) return knight.blackKnight(moves);
        if (piece == 4) return sliding.blackBishop(moves);
        if (piece == 5) return sliding.blackQueen(moves);
        if (piece == 6) return king.blackKing(moves);
        if (piece == 7) return pawn.whitePawn(moves);
        if (piece == 8) return sliding.whiteCastle(moves);
        if (piece == 9) return knight.whiteKnight(moves);
        if (piece == 10) return sliding.whiteBishop(moves);
        if (piece == 11) return sliding.whiteQueen(moves);
        if (piece == 12) return king.whiteKing(moves);
        return null;
    }
}
