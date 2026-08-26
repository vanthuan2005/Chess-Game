package com.example.chessgame;

public final class FenConverter {

    private FenConverter() {
    }

    public static String toFen(
            Chess chess,
            int[][] board,
            int[] whitePassant,
            int[] blackPassant) {

        StringBuilder fen =
                new StringBuilder();

        // 1) Piece placement: row 0 = rank 8, row 7 = rank 1.
        for (int row = 0;
             row < Chess.SIDE;
             row++) {

            int empty = 0;

            for (int col = 0;
                 col < Chess.SIDE;
                 col++) {

                int piece = board[row][col];

                if (piece == 0) {
                    empty++;
                    continue;
                }

                if (empty > 0) {
                    fen.append(empty);
                    empty = 0;
                }

                fen.append(
                        pieceToFen(piece));
            }

            if (empty > 0) {
                fen.append(empty);
            }

            if (row < Chess.SIDE - 1) {
                fen.append('/');
            }
        }

        // 2) Side to move.
        fen.append(' ');
        fen.append(
                chess.getTurn() == 1
                        ? 'w'
                        : 'b');

        // 3) Castling rights.
        // Only report a right if king + corresponding rook are still
        // physically on their original squares. This avoids strict
        // FEN validators rejecting inconsistent castling flags.
        fen.append(' ');
        fen.append(
                buildSafeCastlingRights(
                        chess,
                        board));

        // 4) En-passant target.
        // Some API validators are stricter than standard FEN parsers:
        // they reject an EP target if no pawn can actually capture it.
        // Therefore only include the square when a real legal EP
        // capture is geometrically possible; otherwise use "-".
        fen.append(' ');
        fen.append(
                buildSafeEnPassant(
                        chess.getTurn(),
                        board,
                        whitePassant,
                        blackPassant));

        // 5) Halfmove clock + fullmove number.
        // The project does not track them, but 0 1 is valid FEN.
        fen.append(" 0 1");

        return fen.toString();
    }

    private static char pieceToFen(
            int piece) {

        switch (piece) {

            // Black pieces.
            case 1:
                return 'p';
            case 2:
                return 'r';
            case 3:
                return 'n';
            case 4:
                return 'b';
            case 5:
                return 'q';
            case 6:
                return 'k';

            // White pieces.
            case 7:
                return 'P';
            case 8:
                return 'R';
            case 9:
                return 'N';
            case 10:
                return 'B';
            case 11:
                return 'Q';
            case 12:
                return 'K';

            default:
                throw new IllegalArgumentException(
                        "Unknown chess piece: "
                                + piece);
        }
    }

    private static String buildSafeCastlingRights(
            Chess chess,
            int[][] board) {

        StringBuilder rights =
                new StringBuilder();

        // White: king e1 = board[7][4],
        // rooks a1/h1 = board[7][0]/board[7][7].
        boolean whiteKingHome =
                board[7][4] == 12;

        if (whiteKingHome &&
                board[7][7] == 8 &&
                chess.canWhiteCastleRight()) {

            rights.append('K');
        }

        if (whiteKingHome &&
                board[7][0] == 8 &&
                chess.canWhiteCastleLeft()) {

            rights.append('Q');
        }

        // Black: king e8 = board[0][4],
        // rooks a8/h8 = board[0][0]/board[0][7].
        boolean blackKingHome =
                board[0][4] == 6;

        if (blackKingHome &&
                board[0][7] == 2 &&
                chess.canBlackCastleRight()) {

            rights.append('k');
        }

        if (blackKingHome &&
                board[0][0] == 2 &&
                chess.canBlackCastleLeft()) {

            rights.append('q');
        }

        return rights.length() == 0
                ? "-"
                : rights.toString();
    }

    private static String buildSafeEnPassant(
            int turn,
            int[][] board,
            int[] whitePassant,
            int[] blackPassant) {

        /*
         * IMPORTANT:
         * In the current MainActivity naming:
         *
         * blackPassant[col] is set when a WHITE pawn
         * moves two squares from row 6 to row 4.
         *
         * whitePassant[col] is set when a BLACK pawn
         * moves two squares from row 1 to row 3.
         *
         * The names are historical, so do not swap them here.
         */

        // Black to move after White pushed two squares.
        // Target square is file + rank 3, for example e3.
        if (turn == 2 &&
                blackPassant != null) {

            for (int col = 0;
                 col < Chess.SIDE;
                 col++) {

                if (blackPassant[col] != 1) {
                    continue;
                }

                // White pawn that just moved must be on rank 4:
                // board row 4.
                if (board[4][col] != 7) {
                    continue;
                }

                // A black pawn must actually stand on d4/f4
                // (same row, adjacent file) to capture en passant.
                boolean blackCanCapture =
                        (col > 0 &&
                                board[4][col - 1] == 1)
                                ||
                        (col < Chess.SIDE - 1 &&
                                board[4][col + 1] == 1);

                if (blackCanCapture) {

                    return ""
                            + (char) ('a' + col)
                            + "3";
                }
            }

            return "-";
        }

        // White to move after Black pushed two squares.
        // Target square is file + rank 6, for example e6.
        if (turn == 1 &&
                whitePassant != null) {

            for (int col = 0;
                 col < Chess.SIDE;
                 col++) {

                if (whitePassant[col] != 1) {
                    continue;
                }

                // Black pawn that just moved must be on rank 5:
                // board row 3.
                if (board[3][col] != 1) {
                    continue;
                }

                // A white pawn must actually stand on d5/f5
                // (same row, adjacent file) to capture en passant.
                boolean whiteCanCapture =
                        (col > 0 &&
                                board[3][col - 1] == 7)
                                ||
                        (col < Chess.SIDE - 1 &&
                                board[3][col + 1] == 7);

                if (whiteCanCapture) {

                    return ""
                            + (char) ('a' + col)
                            + "6";
                }
            }
        }

        return "-";
    }
}
