package com.example.chessgame;

/** Pure rule-checking helper. It does not own the game lifecycle. */
class ChessRules {
    private final int[][] game;
    private final boolean blackCastleRight;
    private final boolean blackCastleLeft;
    private final boolean whiteCastleRight;
    private final boolean whiteCastleLeft;
    private final int[] whitePassant;
    private final int[] blackPassant;

    ChessRules(int[][] game, boolean blackCastleRight, boolean blackCastleLeft,
               boolean whiteCastleRight, boolean whiteCastleLeft,
               int[] whitePassant, int[] blackPassant) {
        this.game = game;
        this.blackCastleRight = blackCastleRight;
        this.blackCastleLeft = blackCastleLeft;
        this.whiteCastleRight = whiteCastleRight;
        this.whiteCastleLeft = whiteCastleLeft;
        this.whitePassant = whitePassant == null ? new int[Chess.SIDE] : whitePassant.clone();
        this.blackPassant = blackPassant == null ? new int[Chess.SIDE] : blackPassant.clone();
    }

    int[][] filterLegalMoves(int oldRow, int oldCol, int[][] moves) {
        if (moves == null) return new int[Chess.SIDE][Chess.SIDE];

        int movingPiece = game[oldRow][oldCol];
        int side = movingPiece > 6 ? 1 : 2;

        for (int row = 0; row < Chess.SIDE; row++) {
            for (int col = 0; col < Chess.SIDE; col++) {
                if (moves[row][col] == 2 || moves[row][col] == 3) {
                    if (!isLegalMove(oldRow, oldCol, row, col)) moves[row][col] = 0;
                }

                if (moves[row][col] == 4 || moves[row][col] == 5) {
                    if (!isLegalCastle(oldRow, oldCol, row, col)) moves[row][col] = 0;
                }

                if (moves[row][col] == 7) {
                    boolean rightExists =
                            (side == 1 && whitePassant[col] == 1) ||
                            (side == 2 && blackPassant[col] == 1);

                    if (!rightExists || !isLegalEnPassant(oldRow, oldCol, row, col)) {
                        moves[row][col] = 0;
                    }
                }
            }
        }
        return moves;
    }

    boolean isLegalMove(int oldRow, int oldCol, int newRow, int newCol) {
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

    private boolean isLegalCastle(int oldRow, int oldCol, int newRow, int newCol) {
        int movingPiece = game[oldRow][oldCol];
        int side = movingPiece > 6 ? 1 : 2;

        if (movingPiece != 6 && movingPiece != 12) return false;
        if (isKingInCheck(side)) return false;

        int step = newCol > oldCol ? 1 : -1;
        int throughCol = oldCol + step;

        // The king may not pass through an attacked square.
        if (!isLegalMove(oldRow, oldCol, oldRow, throughCol)) return false;

        // The king may not finish castling on an attacked square.
        return isLegalMove(oldRow, oldCol, newRow, newCol);
    }

    private boolean isLegalEnPassant(int oldRow, int oldCol, int newRow, int newCol) {
        int movingPiece = game[oldRow][oldCol];
        int side = movingPiece > 6 ? 1 : 2;
        int expectedCapturedPawn = side == 1 ? 1 : 7;

        if (game[newRow][newCol] != 0) return false;
        if (game[oldRow][newCol] != expectedCapturedPawn) return false;

        int capturedPawn = game[oldRow][newCol];

        game[newRow][newCol] = movingPiece;
        game[oldRow][oldCol] = 0;
        game[oldRow][newCol] = 0;

        boolean legal = !isKingInCheck(side);

        game[oldRow][oldCol] = movingPiece;
        game[oldRow][newCol] = capturedPawn;
        game[newRow][newCol] = 0;

        return legal;
    }

    boolean isKingInCheck(int side) {
        int king = side == 1 ? 12 : 6;
        int kingRow = -1;
        int kingCol = -1;

        for (int row = 0; row < Chess.SIDE; row++) {
            for (int col = 0; col < Chess.SIDE; col++) {
                if (game[row][col] == king) {
                    kingRow = row;
                    kingCol = col;
                    break;
                }
            }
            if (kingRow != -1) break;
        }

        if (kingRow == -1) return true;

        for (int row = 0; row < Chess.SIDE; row++) {
            for (int col = 0; col < Chess.SIDE; col++) {
                int p = game[row][col];
                if (p == 0) continue;

                if (side == 1 && p >= 1 && p <= 6) {
                    Piece enemy = new Piece(row, col, p, game);
                    int[][] moves = enemy.possibleMoves();
                    if (moves != null && moves[kingRow][kingCol] == 3) return true;
                }

                if (side == 2 && p >= 7 && p <= 12) {
                    Piece enemy = new Piece(row, col, p, game);
                    int[][] moves = enemy.possibleMoves();
                    if (moves != null && moves[kingRow][kingCol] == 3) return true;
                }
            }
        }
        return false;
    }

    int[] getKingPosition(int side) {
        int king = side == 1 ? 12 : 6;
        for (int row = 0; row < Chess.SIDE; row++)
            for (int col = 0; col < Chess.SIDE; col++)
                if (game[row][col] == king) return new int[]{row, col};
        return new int[]{-1, -1};
    }

    boolean hasAnyLegalMove(int side) {
        for (int row = 0; row < Chess.SIDE; row++) {
            for (int col = 0; col < Chess.SIDE; col++) {
                int p = game[row][col];
                if (p == 0) continue;
                if (side == 1 && (p < 7 || p > 12)) continue;
                if (side == 2 && (p < 1 || p > 6)) continue;

                Piece testPiece = new Piece(row, col, p, game,
                        blackCastleRight, blackCastleLeft,
                        whiteCastleRight, whiteCastleLeft);

                int[][] moves = filterLegalMoves(row, col, testPiece.possibleMoves());

                for (int r = 0; r < Chess.SIDE; r++)
                    for (int c = 0; c < Chess.SIDE; c++)
                        if (moves[r][c] == 2 || moves[r][c] == 3 ||
                                moves[r][c] == 4 || moves[r][c] == 5 ||
                                moves[r][c] == 7) return true;
            }
        }
        return false;
    }

    boolean isCheckmate(int side) {
        return isKingInCheck(side) && !hasAnyLegalMove(side);
    }

    boolean isStalemate(int side) {
        return !isKingInCheck(side) && !hasAnyLegalMove(side);
    }
}
