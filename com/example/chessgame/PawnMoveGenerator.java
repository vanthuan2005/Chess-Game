package com.example.chessgame;

class PawnMoveGenerator {
    private final int row;
    private final int col;
    private final int[][] game;

    PawnMoveGenerator(int row, int col, int[][] game) {
        this.row = row;
        this.col = col;
        this.game = game;

    }

    int[][] blackPawn(int[][] m) {
        int[][] moves = m;
        if(row==7){
            moves[row][col]=6;
            return moves;
        }
        else if(row==1) {
            for (int i = row + 1; i < row + 3; i++) {
                if (game[i][col] == 0)
                    moves[i][col] = 2;
                else
                    break;
            }
        }
        else {
            if (game[row + 1][col] == 0)
                moves[row + 1][col] = 2;
        }
        for (int c = col - 1; c < col + 2; c += 2)
            if (c >= 0 && c < Chess.SIDE && row + 1 < Chess.SIDE)
                if (game[row + 1][c] > 6)
                    moves[row + 1][c] = 3;
        if(row==4)
            for(int c=col-1;c<col+2;c+=2)
                if(c>=0&&c<Chess.SIDE)
                    if(game[row][c]==7&&game[row+1][c]==0)
                        moves[row+1][c]=7;
        moves[row][col] =1;
        return moves;
    }

    int[][] whitePawn(int[][] m) {
        int[][] moves = m;
        if(row==0){
            moves[row][col]=6;
            return moves;
        }
        else if(row==6) {
            for (int i = row - 1; i > row-3; i--) {
                if (game[i][col] == 0)
                    moves[i][col] = 2;
                else
                    break;
            }
        }
        else {
            if (game[row - 1][col] == 0)
                moves[row - 1][col] = 2;
        }
        for(int c=col-1;c<col+2;c+=2)
            if(c>=0&&c<Chess.SIDE&&row-1>=0)
                if(game[row-1][c]>0 &&game[row-1][c] <7)
                    moves[row-1][c]=3;
        if(row==3)
            for(int c=col-1;c<col+2;c+=2)
                if(c>=0&&c<Chess.SIDE&&row-1>=0)
                    if(game[row][c]==1&&game[row-1][c]==0)
                        moves[row-1][c]=7;
        moves[row][col] =1;
        return moves;
    }
}
