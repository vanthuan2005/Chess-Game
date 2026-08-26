package com.example.chessgame;

class KingMoveGenerator {
    private final int row;
    private final int col;
    private final int[][] game;
    private final boolean blackCastleRight;
    private final boolean blackCastleLeft;
    private final boolean whiteCastleRight;
    private final boolean whiteCastleLeft;

    KingMoveGenerator(int row, int col, int[][] game, boolean blackCastleRight, boolean blackCastleLeft, boolean whiteCastleRight, boolean whiteCastleLeft) {
        this.row = row;
        this.col = col;
        this.game = game;
        this.blackCastleRight = blackCastleRight;
        this.blackCastleLeft = blackCastleLeft;
        this.whiteCastleRight = whiteCastleRight;
        this.whiteCastleLeft = whiteCastleLeft;

    }

    int[][] blackKing(int[][] m) {
        int[][] moves = m;
        if(row-1>=0 &&col-1>=0&&game[row-1][col-1]==0)
            moves[row-1][col-1]=2;
        if(row-1>=0&&game[row-1][col]==0)
            moves[row-1][col]=2;
        if(row-1>=0&&col+1<Chess.SIDE&&game[row-1][col+1]==0)
            moves[row-1][col+1]=2;
        if(col-1>=0&&game[row][col-1]==0)
            moves[row][col-1]=2;
        if(col+1<Chess.SIDE&&game[row][col+1]==0)
            moves[row][col+1]=2;
        if(row+1<Chess.SIDE&&col-1>=0&&game[row+1][col-1]==0)
            moves[row+1][col-1]=2;
        if(row+1<Chess.SIDE&&game[row+1][col]==0)
            moves[row+1][col]=2;
        if(row+1<Chess.SIDE&&col+1<Chess.SIDE&&game[row+1][col+1]==0)
            moves[row+1][col+1]=2;

        if(row-1>=0 &&col-1>=0&&game[row-1][col-1]>6)
            moves[row-1][col-1]=3;
        if(row-1>=0&&game[row-1][col]>6)
            moves[row-1][col]=3;
        if(row-1>=0&&col+1<Chess.SIDE&&game[row-1][col+1]>6)
            moves[row-1][col+1]=3;
        if(col-1>=0&&game[row][col-1]>6)
            moves[row][col-1]=3;
        if(col+1<Chess.SIDE&&game[row][col+1]>6)
            moves[row][col+1]=3;
        if(row+1<Chess.SIDE&&col-1>=0&&game[row+1][col-1]>6)
            moves[row+1][col-1]=3;
        if(row+1<Chess.SIDE&&game[row+1][col]>6)
            moves[row+1][col]=3;
        if(row+1<Chess.SIDE&&col+1<Chess.SIDE&&game[row+1][col+1]>6)
            moves[row+1][col+1]=3;
        if(blackCastleRight){
            boolean canCastle=true;
            for(int c=col+1;c<Chess.SIDE-1;c++)
                if(game[row][c]!=0) {
                    canCastle=false;
                    break;
                }
            if(canCastle && col==4 && game[row][Chess.SIDE-1]==2){
                moves[row][col+2]=4;
            }
        }
        if(blackCastleLeft){
            boolean canCastle=true;
            for(int c=col-1;c>0;c--)
                if(game[row][c]!=0) {
                    canCastle=false;
                    break;
                }
            if(canCastle && col==4 && game[row][0]==2){
                moves[row][col-2]=5;
            }
        }
        moves[row][col]=1;
        return moves;
    }

    int[][] whiteKing(int[][] m) {
        int[][] moves = m;
        if(row-1>=0 &&col-1>=0&&game[row-1][col-1]==0)
            moves[row-1][col-1]=2;
        if(row-1>=0&&game[row-1][col]==0)
            moves[row-1][col]=2;
        if(row-1>=0&&col+1<Chess.SIDE&&game[row-1][col+1]==0)
            moves[row-1][col+1]=2;
        if(col-1>=0&&game[row][col-1]==0)
            moves[row][col-1]=2;
        if(col+1<Chess.SIDE&&game[row][col+1]==0)
            moves[row][col+1]=2;
        if(row+1<Chess.SIDE&&col-1>=0&&game[row+1][col-1]==0)
            moves[row+1][col-1]=2;
        if(row+1<Chess.SIDE&&game[row+1][col]==0)
            moves[row+1][col]=2;
        if(row+1<Chess.SIDE&&col+1<Chess.SIDE&&game[row+1][col+1]==0)
            moves[row+1][col+1]=2;

        if(row-1>=0 &&col-1>=0&&game[row-1][col-1]>0&&game[row-1][col-1]<7)
            moves[row-1][col-1]=3;
        if(row-1>=0&&game[row-1][col]>0&&game[row-1][col]<7)
            moves[row-1][col]=3;
        if(row-1>=0&&col+1<Chess.SIDE&&game[row-1][col+1]>0&&game[row-1][col+1]<7)
            moves[row-1][col+1]=3;
        if(col-1>=0&&game[row][col-1]>0&&game[row][col-1]<7)
            moves[row][col-1]=3;
        if(col+1<Chess.SIDE&&game[row][col+1]>0&&game[row][col+1]<7)
            moves[row][col+1]=3;
        if(row+1<Chess.SIDE&&col-1>=0&&game[row+1][col-1]>0&&game[row+1][col-1]<7)
            moves[row+1][col-1]=3;
        if(row+1<Chess.SIDE&&game[row+1][col]>0&&game[row+1][col]<7)
            moves[row+1][col]=3;
        if(row+1<Chess.SIDE&&col+1<Chess.SIDE&&game[row+1][col+1]>0&&game[row+1][col+1]<7)
            moves[row+1][col+1]=3;
        if(whiteCastleRight){
            boolean canCastle=true;
            for(int c=col+1;c<Chess.SIDE-1;c++)
                if(game[row][c]!=0) {
                    canCastle=false;
                    break;
                }
            if(canCastle && col==4 && game[row][Chess.SIDE-1]==8){
                moves[row][col+2]=4;
            }
        }
        if(whiteCastleLeft){
            boolean canCastle=true;
            for(int c=col-1;c>0;c--)
                if(game[row][c]!=0) {
                    canCastle=false;
                    break;
                }
            if(canCastle && col==4 && game[row][0]==8){
                moves[row][col-2]=5;
            }
        }
        moves[row][col]=1;
        return moves;
    }
}
