package com.example.chessgame;

class SlidingMoveGenerator {
    private final int row;
    private final int col;
    private final int[][] game;

    SlidingMoveGenerator(int row, int col, int[][] game) {
        this.row = row;
        this.col = col;
        this.game = game;

    }

    int[][] blackCastle(int[][] m) {
        int[][] moves= blackHorizontals(m);
        moves[row][col] =1;
        return moves;
    }

    int[][] whiteCastle(int[][] m) {
        int[][] moves= whiteHorizontals(m);
        moves[row][col] =1;
        return moves;
    }

    int[][] blackBishop(int[][] m) {
        int[][] moves= blackDiags(m);
        moves[row][col] = 1;
        return moves;
    }

    int[][] whiteBishop(int[][] m) {
        int[][] moves= whiteDiags(m);
        moves[row][col] = 1;
        return moves;
    }

    int[][] blackQueen(int[][] m) {
        int[][] moves= blackDiags(m);
        moves = blackHorizontals(moves);
        moves[row][col] = 1;
        return moves;
    }

    int[][] whiteQueen(int[][] m) {
        int[][] moves= whiteDiags(m);
        moves = whiteHorizontals(moves);
        moves[row][col] = 1;
        return moves;
    }

    int[][] blackHorizontals(int[][] m) {
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

    int[][] whiteHorizontals(int[][] m) {
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

    int[][] blackDiags(int[][] m) {
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

    int[][] whiteDiags(int[][] m) {
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
}
