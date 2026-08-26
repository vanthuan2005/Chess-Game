package com.example.chessgame;

public class Piece {
    private int row;
    private int col;
    private int piece;
    private boolean blackCastleRight;
    private boolean blackCastleLeft;
    private boolean whiteCastleRight;
    private boolean whiteCastleLeft;
    private int[][] game;
    public Piece(int r, int c, int p, int[][] g) {
        this(r, c, p, g, false, false, false, false);
    }

    public Piece(int r, int c, int p, int[][] g,
                 boolean blackCastleRight,
                 boolean blackCastleLeft,
                 boolean whiteCastleRight,
                 boolean whiteCastleLeft) {
        row = r;
        col = c;
        piece = p;
        game = g;
        this.blackCastleRight = blackCastleRight;
        this.blackCastleLeft = blackCastleLeft;
        this.whiteCastleRight = whiteCastleRight;
        this.whiteCastleLeft = whiteCastleLeft;
    }

    public int[][] possibleMoves() {
        int[][] moves = new int[Chess.SIDE][Chess.SIDE];
        for(int r = 0;r<moves.length;r++)
            for(int c =0;c<moves.length;c++)
                moves[r][c]=0;

        if (piece ==1)
            return blackPawn(moves);
        if (piece == 2)
            return blackCastle(moves);
        if (piece == 3)
            return blackKnight(moves);
        if (piece == 4)
            return blackBishop(moves);
        if (piece == 5)
            return blackQueen(moves);
        if (piece == 6)
            return blackKing(moves);
        if (piece == 7)
            return whitePawn(moves);
        if (piece == 8)
            return whiteCastle(moves);
        if (piece == 9)
            return whiteKnight(moves);
        if (piece == 10)
            return whiteBishop(moves);
        if (piece == 11)
            return whiteQueen(moves);
        if (piece == 12)
            return whiteKing(moves);
        return null;
    }

    public int[][] blackPawn(int[][] m) {
        int[][] moves = m;
        if(row==7){
            moves[row][col]=6;
            return moves;
        }
        else if(row==1) {
            for (int i = row + 1; i < row + 3; i++) {
                if (game[i][col] == 0)
                    moves[i][col] = 2;
                else if(game[i][col]<=6)
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
    public int[][] blackCastle(int[][] m) {
        int[][] moves= blackHorizontals(m);
        moves[row][col] =1;
        return moves;
    }
    public int[][] blackKnight(int[][] m) {
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
    public int[][] blackBishop(int[][] m) {
        int[][] moves= blackDiags(m);
        moves[row][col] = 1;
        return moves;
    }
    public int[][] blackQueen(int[][] m) {
        int[][] moves= blackDiags(m);
        moves = blackHorizontals(moves);
        moves[row][col] = 1;
        return moves;
    }
    public int[][] blackKing(int[][] m) {
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
    public int[][] whitePawn(int[][] m) {
        int[][] moves = m;
        if(row==0){
            moves[row][col]=6;
            return moves;
        }
        else if(row==6) {
            for (int i = row - 1; i > row-3; i--) {
                if (game[i][col] == 0)
                    moves[i][col] = 2;
                else if(game[i][col]>6)
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
    public int[][] whiteCastle(int[][] m) {
        int[][] moves= whiteHorizontals(m);
        moves[row][col] =1;
        return moves;
    }
    public int[][] whiteKnight(int[][] m) {
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
    public int[][] whiteBishop(int[][] m) {
        int[][] moves= whiteDiags(m);
        moves[row][col] = 1;
        return moves;
    }
    public int[][] whiteQueen(int[][] m) {
        int[][] moves= whiteDiags(m);
        moves = whiteHorizontals(moves);
        moves[row][col] = 1;
        return moves;
    }
    public int[][] whiteKing(int[][] m) {
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

    public int[][] blackHorizontals(int[][] m) {
        int[][] moves = m;
        for(int r = row+1;r<moves.length;r++) {
            if(game[r][col]==0)
                moves[r][col] = 2;
            else if(game[r][col]>6) {
                moves[r][col] = 3;
                break;
            }
            else if(game[r][col]<=6)
                break;
        }
        for(int r = row-1;r>=0;r--) {
            if(game[r][col]==0)
                moves[r][col] = 2;
            else if(game[r][col]>6) {
                moves[r][col] = 3;
                break;
            }
            else if(game[r][col]<=6)
                break;
        }
        for(int c = col+1;c<moves.length;c++) {
            if(game[row][c]==0)
                moves[row][c] = 2;
            else if(game[row][c]>6){
                moves[row][c]=3;
                break;
            }

            else if(game[row][c]<=6)
                break;
        }
        for(int c = col-1;c>=0;c--) {
            if(game[row][c]==0)
                moves[row][c] = 2;
            else if(game[row][c]>6){
                moves[row][c]=3;
                break;
            }

            else if(game[row][c]<=6)
                break;
        }
        return moves;
    }
    public int[][] whiteHorizontals(int[][] m) {
        int[][] moves = m;
        for(int r = row+1;r<moves.length;r++) {
            if(game[r][col]==0)
                moves[r][col] = 2;
            else if(game[r][col]<=6) {
                moves[r][col] = 3;
                break;
            }
            else if(game[r][col]>6)
                break;
        }
        for(int r = row-1;r>=0;r--) {
            if(game[r][col]==0)
                moves[r][col] = 2;
            else if(game[r][col]<=6) {
                moves[r][col] = 3;
                break;
            }
            else if(game[r][col]>6)
                break;
        }
        for(int c = col+1;c<moves.length;c++) {
            if(game[row][c]==0)
                moves[row][c] = 2;
            else if(game[row][c]<=6){
                moves[row][c]=3;
                break;
            }

            else if(game[row][c]>6)
                break;
        }
        for(int c = col-1;c>=0;c--) {
            if(game[row][c]==0)
                moves[row][c] = 2;
            else if(game[row][c]<=6){
                moves[row][c]=3;
                break;
            }

            else if(game[row][c]>6)
                break;
        }
        return moves;
    }
    public int[][] blackDiags(int[][] m) {
        int[][] moves =m;
        int c=col+1;
        for (int r=row+1;r<Chess.SIDE&&c<Chess.SIDE;r++){
            if(game[r][c]==0)
                moves[r][c] = 2;
            else if(game[r][c]>6) {
                moves[r][c] = 3;
                break;
            }
            else if(game[r][c]<=6)
                break;
            c++;
        }
        c=col-1;
        for (int r=row+1;r<Chess.SIDE&&c>=0;r++){
                if(game[r][c]==0)
                moves[r][c] = 2;
            else if(game[r][c]>6) {
                moves[r][c] = 3;
                break;
            }
            else if(game[r][c]<=6)
                break;
            c--;
        }
        c=col+1;
        for (int r=row-1;r>=0&&c<Chess.SIDE;r--){
            if(game[r][c]==0)
                moves[r][c] = 2;
            else if(game[r][c]>6) {
                moves[r][c] = 3;
                break;
            }
            else if(game[r][c]<=6)
                break;
            c++;
        }
        c=col-1;
        for (int r=row-1;r>=0&&c>=0;r--){
            if(game[r][c]==0)
                moves[r][c] = 2;
            else if(game[r][c]>6) {
                moves[r][c] = 3;
                break;
            }
            else if(game[r][c]<=6)
                break;
            c--;
        }
        return moves;
    }
    public int[][] whiteDiags(int[][] m) {
        int[][] moves =m;
        int c=col+1;
        for (int r=row+1;r<Chess.SIDE&&c<Chess.SIDE;r++){
            if(game[r][c]==0)
                moves[r][c] = 2;
            else if(game[r][c]<=6) {
                moves[r][c] = 3;
                break;
            }
            else if(game[r][c]>6)
                break;
            c++;
        }
        c=col-1;
        for (int r=row+1;r<Chess.SIDE&&c>=0;r++){
            if(game[r][c]==0)
                moves[r][c] = 2;
            else if(game[r][c]<=6) {
                moves[r][c] = 3;
                break;
            }
            else if(game[r][c]>6)
                break;
            c--;
        }
        c=col+1;
        for (int r=row-1;r>=0&&c<Chess.SIDE;r--){
            if(game[r][c]==0)
                moves[r][c] = 2;
            else if(game[r][c]<=6) {
                moves[r][c] = 3;
                break;
            }
            else if(game[r][c]>6)
                break;
            c++;
        }
        c=col-1;
        for (int r=row-1;r>=0&&c>=0;r--){
            if(game[r][c]==0)
                moves[r][c] = 2;
            else if(game[r][c]<=6) {
                moves[r][c] = 3;
                break;
            }
            else if(game[r][c]>6)
                break;
            c--;
        }
        return moves;
    }
    public void updateBlackCastleRight(boolean castle){
        blackCastleRight = castle;
    }
    public void updateBlackCastleLeft(boolean castle){
        blackCastleLeft = castle;
    }
    public void updateWhiteCastleRight(boolean castle){
        whiteCastleRight = castle;
    }
    public void updateWhiteCastleLeft(boolean castle){
        whiteCastleLeft = castle;
    }

}
