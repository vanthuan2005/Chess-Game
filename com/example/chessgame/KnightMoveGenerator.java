package com.example.chessgame;

class KnightMoveGenerator {
    private final int row;
    private final int col;
    private final int[][] game;

    KnightMoveGenerator(int row, int col, int[][] game) {
        this.row = row;
        this.col = col;
        this.game = game;

    }

    int[][] blackKnight(int[][] m) {
        int[][] moves = m;
        for(int r=row-1;r<row+2;r+=2) {
            for(int c= col-2;c< col+3 ;c+=4){
                if(r>=0 && c>=0 &&r<Chess.SIDE&&c<Chess.SIDE) {
                    if (game[r][c] == 0 )
                        moves[r][c] = 2;
                    else if(game[r][c]>6)
                        moves[r][c]=3;
                }
            }
        }
        for(int r=row-2;r<row+3;r+=4) {
            for(int c= col-1;c< col+2 ;c+=2){
                if(r>=0 && c>=0 &&r<Chess.SIDE&&c<Chess.SIDE) {
                    if (game[r][c] == 0 )
                        moves[r][c] = 2;
                    else if(game[r][c]>6)
                        moves[r][c]=3;
                }
            }
        }
        moves[row][col] = 1;
        return moves;
    }

    int[][] whiteKnight(int[][] m) {
        int[][] moves = m;
        for(int r=row-1;r<row+2;r+=2) {
            for(int c= col-2;c< col+3 ;c+=4){
                if(r>=0 && c>=0 &&r<Chess.SIDE&&c<Chess.SIDE) {
                    if (game[r][c] == 0 )
                        moves[r][c] = 2;
                    else if(game[r][c]<=6)
                        moves[r][c]=3;
                }
            }
        }
        for(int r=row-2;r<row+3;r+=4) {
            for(int c= col-1;c< col+2 ;c+=2){
                if(r>=0 && c>=0 &&r<Chess.SIDE&&c<Chess.SIDE) {
                    if (game[r][c] == 0 )
                        moves[r][c] = 2;
                    else if(game[r][c]<=6)
                        moves[r][c]=3;
                }
            }
        }
        moves[row][col] = 1;
        return moves;
    }
}
