package com.example.chessgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Chess chessGame;
    private Button[][] buttons;
    private int[][] game;
    private int[][] currentGreen;

    private int oldRow = -1;
    private int oldCol = -1;

    private TextView status;
    private TextView topPlayerName;
    private TextView bottomPlayerName;
    private TextView topTimer;
    private TextView bottomTimer;
    private TextView scoreText;

    private int whiteWins;
    private int blackWins;

    private int[] whitePassant;
    private int[] blackPassant;

    private int boardSquareSize;


    // 10-minute chess clock for each side.
    private static final long START_TIME_MS = 10 * 60 * 1000L;

    private long whiteTimeMs = START_TIME_MS;
    private long blackTimeMs = START_TIME_MS;

    private long undoWhiteTimeMs;
    private long undoBlackTimeMs;

    private int[] undoWhitePassant;
    private int[] undoBlackPassant;

    private final Handler timerHandler =
            new Handler(Looper.getMainLooper());

    private boolean timerRunning = true;

    private final Runnable timerRunnable =
            new Runnable() {
                @Override
                public void run() {

                    if (!timerRunning) {
                        return;
                    }

                    if (chessGame != null &&
                            chessGame.whoWon() == 0 &&
                            !chessGame.isDrawByStalemate() &&
                            !chessGame.isFivefoldRepetition()) {

                        if (chessGame.getTurn() == 1) {
                            whiteTimeMs -= 1000;

                            if (whiteTimeMs <= 0) {
                                whiteTimeMs = 0;
                                onTimeOut(false);
                                return;
                            }

                        } else {
                            blackTimeMs -= 1000;

                            if (blackTimeMs <= 0) {
                                blackTimeMs = 0;
                                onTimeOut(true);
                                return;
                            }
                        }

                        updateTimerViews();
                    }

                    timerHandler.postDelayed(this, 1000);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chessGame = new Chess();
        game = chessGame.getGame();

        whitePassant = new int[Chess.SIDE];
        blackPassant = new int[Chess.SIDE];

        for (int i = 0; i < Chess.SIDE; i++) {
            whitePassant[i] = 0;
            blackPassant[i] = 0;
        }

        whiteWins = 0;
        blackWins = 0;

        buildGuiByCode();
    }

    // =========================================================
    // UI
    // =========================================================

    public void buildGuiByCode() {

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        int screenWidth = size.x;
        int horizontalMargin = dp(16);

        boardSquareSize =
                (screenWidth - horizontalMargin * 2) / Chess.SIDE;

        currentGreen =
                new int[Chess.SIDE][Chess.SIDE];

        ScrollView scrollView =
                new ScrollView(this);

        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(
                Color.parseColor("#1F2025"));

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL);

        root.setGravity(
                Gravity.CENTER_HORIZONTAL);

        root.setPadding(
                horizontalMargin,
                dp(18),
                horizontalMargin,
                dp(24));

        scrollView.addView(
                root,
                new ScrollView.LayoutParams(
                        ScrollView.LayoutParams.MATCH_PARENT,
                        ScrollView.LayoutParams.WRAP_CONTENT));

        // -----------------------------
        // HEADER
        // -----------------------------

        TextView title =
                new TextView(this);

        title.setText("CHESS GAME");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24);
        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD);

        title.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

        titleParams.bottomMargin = dp(18);

        root.addView(
                title,
                titleParams);

        // -----------------------------
        // TOP INFO CARD - BLACK
        // -----------------------------

        LinearLayout topCard =
                createPlayerCard();

        topPlayerName =
                createPlayerName(
                        "BLACK PLAYER",
                        "Opponent");

        topTimer =
                createTimer("10:00");

        topCard.addView(
                topPlayerName,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f));

        topCard.addView(topTimer);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

        cardParams.bottomMargin = dp(14);

        root.addView(
                topCard,
                cardParams);

        // -----------------------------
        // SCORE
        // -----------------------------

        scoreText =
                new TextView(this);

        scoreText.setTextColor(
                Color.parseColor("#C7C8CC"));

        scoreText.setTextSize(14);
        scoreText.setGravity(Gravity.CENTER);
        scoreText.setPadding(
                0,
                0,
                0,
                dp(10));

        root.addView(scoreText);

        // -----------------------------
        // BOARD
        // -----------------------------

        GridLayout board =
                new GridLayout(this);

        board.setColumnCount(Chess.SIDE);
        board.setRowCount(Chess.SIDE);

        buttons =
                new Button[Chess.SIDE][Chess.SIDE];

        ButtonHandler bh =
                new ButtonHandler();

        for (int row = 0;
             row < Chess.SIDE;
             row++) {

            for (int col = 0;
                 col < Chess.SIDE;
                 col++) {

                buttons[row][col] =
                        new Button(this);

                buttons[row][col]
                        .setOnClickListener(bh);

                buttons[row][col]
                        .setPadding(0, 0, 0, 0);

                buttons[row][col]
                        .setMinWidth(0);

                buttons[row][col]
                        .setMinHeight(0);

                buttons[row][col]
                        .setAllCaps(false);

                board.addView(
                        buttons[row][col],
                        boardSquareSize,
                        boardSquareSize);

                movePieces(row, col);
            }
        }

        LinearLayout.LayoutParams boardParams =
                new LinearLayout.LayoutParams(
                        Chess.SIDE * boardSquareSize,
                        Chess.SIDE * boardSquareSize);

        boardParams.gravity =
                Gravity.CENTER_HORIZONTAL;

        root.addView(
                board,
                boardParams);

        // -----------------------------
        // TURN STATUS
        // -----------------------------

        status =
                new TextView(this);

        status.setTextColor(Color.WHITE);
        status.setTextSize(23);
        status.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD);

        status.setGravity(Gravity.CENTER);

        status.setPadding(
                dp(8),
                dp(16),
                dp(8),
                dp(16));

        LinearLayout.LayoutParams statusParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

        root.addView(
                status,
                statusParams);

        // -----------------------------
        // BOTTOM INFO CARD - WHITE
        // -----------------------------

        LinearLayout bottomCard =
                createPlayerCard();

        bottomPlayerName =
                createPlayerName(
                        "WHITE PLAYER",
                        "You");

        bottomTimer =
                createTimer("10:00");

        bottomCard.addView(
                bottomPlayerName,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f));

        bottomCard.addView(bottomTimer);

        LinearLayout.LayoutParams bottomParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

        bottomParams.bottomMargin = dp(14);

        root.addView(
                bottomCard,
                bottomParams);

        // -----------------------------
        // ACTION BUTTONS
        // -----------------------------

        LinearLayout actions =
                new LinearLayout(this);

        actions.setOrientation(
                LinearLayout.HORIZONTAL);

        actions.setGravity(Gravity.CENTER);

        Button newGame =
                createActionButton(
                        "NEW GAME");

        Button undoButton =
                createActionButton(
                        "UNDO");

        LinearLayout.LayoutParams actionButtonParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(48),
                        1f);

        actionButtonParams.setMargins(
                dp(4),
                0,
                dp(4),
                0);

        actions.addView(
                newGame,
                actionButtonParams);

        actions.addView(
                undoButton,
                actionButtonParams);

        root.addView(
                actions,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

        newGame.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        showRestartDialog();
                    }
                });

        undoButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        undoMove();
                    }
                });

        resetBackgrounds();
        highlightCheckedKing();
        updateStatusUi();

        setContentView(scrollView);

        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private LinearLayout createPlayerCard() {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.HORIZONTAL);

        card.setGravity(
                Gravity.CENTER_VERTICAL);

        card.setPadding(
                dp(16),
                dp(13),
                dp(16),
                dp(13));

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.parseColor("#292B31"));

        background.setCornerRadius(
                dp(14));

        card.setBackground(background);

        return card;
    }

    private TextView createPlayerName(
            String name,
            String subtitle) {

        TextView view =
                new TextView(this);

        view.setText(
                name + "\n" + subtitle);

        view.setTextColor(Color.WHITE);
        view.setTextSize(16);
        view.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD);

        view.setLineSpacing(
                dp(2),
                1f);

        return view;
    }

    private TextView createTimer(
            String time) {

        TextView timer =
                new TextView(this);

        timer.setText(time);
        timer.setTextColor(Color.WHITE);
        timer.setTextSize(27);

        timer.setTypeface(
                Typeface.MONOSPACE,
                Typeface.BOLD);

        timer.setGravity(
                Gravity.CENTER);

        return timer;
    }

    private Button createActionButton(
            String text) {

        Button button =
                new Button(this);

        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(13);

        GradientDrawable bg =
                new GradientDrawable();

        bg.setColor(
                Color.parseColor("#34363D"));

        bg.setCornerRadius(
                dp(12));

        button.setBackground(bg);

        return button;
    }

    private int dp(int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int)
                (value * density + 0.5f);
    }

    // =========================================================
    // GAME INPUT
    // =========================================================

    public void update(
            int row,
            int col) {

        int[][] possibleMoves =
                chessGame.play(
                        row,
                        col,
                        game[row][col]);

        // En passant lifetime - keep original project behavior.
        for (int i = 0;
             i < Chess.SIDE;
             i++) {

            if (whitePassant[i] == 1 &&
                    chessGame.getTurn() == 2) {

                whitePassant[i] = 2;
            }

            if (blackPassant[i] == 1 &&
                    chessGame.getTurn() == 1) {

                blackPassant[i] = 2;
            }
        }

        boolean moveCompleted = false;
        boolean promotionRequired = false;

        // Select a piece.
        if (game[row][col] > 0 &&
                currentGreen[row][col] != 3 &&
                currentGreen[row][col] != 4 &&
                currentGreen[row][col] != 5) {

            resetBackgrounds();

            if (oldRow == row &&
                    oldCol == col) {

                oldRow = -1;
                oldCol = -1;

            } else {

                oldRow = row;
                oldCol = col;

                for (int r = 0;
                     r < Chess.SIDE;
                     r++) {

                    for (int c = 0;
                         c < Chess.SIDE;
                         c++) {

                        if (possibleMoves[r][c] == 2 ||
                                possibleMoves[r][c] == 4 ||
                                possibleMoves[r][c] == 5) {

                            buttons[r][c]
                                    .setBackground(
                                            getDrawable(
                                                    R.drawable.green_box));
                        }

                        if (possibleMoves[r][c] == 3 ||
                                (possibleMoves[r][c] == 7 &&
                                        whitePassant[c] == 1 &&
                                        chessGame.getTurn() == 1) ||
                                (possibleMoves[r][c] == 7 &&
                                        blackPassant[c] == 1 &&
                                        chessGame.getTurn() == 2)) {

                            buttons[r][c]
                                    .setBackground(
                                            getDrawable(
                                                    R.drawable.red_box));
                        }
                    }
                }
            }

            currentGreen =
                    possibleMoves;

            highlightCheckedKing();
            updateStatusUi();
            return;
        }

        if (currentGreen != null &&
                oldRow >= 0 &&
                oldCol >= 0) {

            // ---------------------------------
            // NORMAL MOVE
            // ---------------------------------

            if (currentGreen[row][col] == 2) {

                saveUndoState();
                resetBackgrounds();

                if (oldRow == 1 &&
                        row == 3 &&
                        game[oldRow][oldCol] == 1 &&
                        whitePassant[col] == 0) {

                    whitePassant[col] = 1;
                }

                if (oldRow == 6 &&
                        row == 4 &&
                        game[oldRow][oldCol] == 7 &&
                        blackPassant[col] == 0) {

                    blackPassant[col] = 1;
                }

                game[row][col] =
                        game[oldRow][oldCol];

                game[oldRow][oldCol] = 0;

                movePieces(row, col);
                movePieces(oldRow, oldCol);

                chessGame.updateTurn();

                currentGreen =
                        possibleMoves;

                moveCompleted = true;
            }

            // ---------------------------------
            // CAPTURE
            // ---------------------------------

            else if (currentGreen[row][col] == 3) {

                saveUndoState();
                resetBackgrounds();

                game[row][col] =
                        game[oldRow][oldCol];

                game[oldRow][oldCol] = 0;

                movePieces(row, col);
                movePieces(oldRow, oldCol);

                currentGreen =
                        possibleMoves;

                chessGame.updateTurn();

                moveCompleted = true;
            }

            // ---------------------------------
            // KING-SIDE CASTLING - ORIGINAL 4
            // ---------------------------------

            else if (currentGreen[row][col] == 4) {

                saveUndoState();
                resetBackgrounds();

                game[row][6] =
                        game[row][4];

                game[row][5] =
                        game[row][4] == 6
                                ? 2
                                : 8;

                game[row][4] = 0;
                game[row][Chess.SIDE - 1] = 0;

                movePieces(row, 6);
                movePieces(row, 5);
                movePieces(row, 4);
                movePieces(
                        row,
                        Chess.SIDE - 1);

                currentGreen =
                        possibleMoves;

                chessGame.updateTurn();

                moveCompleted = true;
            }

            // ---------------------------------
            // QUEEN-SIDE CASTLING - ORIGINAL 5
            // ---------------------------------

            else if (currentGreen[row][col] == 5) {

                saveUndoState();
                resetBackgrounds();

                game[row][2] =
                        game[row][4];

                game[row][3] =
                        game[row][4] == 6
                                ? 2
                                : 8;

                game[row][4] = 0;
                game[row][0] = 0;

                movePieces(row, 2);
                movePieces(row, 3);
                movePieces(row, 4);
                movePieces(row, 0);

                currentGreen =
                        possibleMoves;

                chessGame.updateTurn();

                moveCompleted = true;
            }

            // ---------------------------------
            // EN PASSANT - KEEP ORIGINAL
            // ---------------------------------

            else if (currentGreen[row][col] == 7 &&
                    ((chessGame.getTurn() == 1 &&
                            whitePassant[col] == 1) ||
                            (chessGame.getTurn() == 2 &&
                                    blackPassant[col] == 1))) {

                saveUndoState();
                resetBackgrounds();

                game[row][col] =
                        game[oldRow][oldCol];

                game[oldRow][oldCol] = 0;
                game[oldRow][col] = 0;

                movePieces(row, col);
                movePieces(oldRow, oldCol);
                movePieces(oldRow, col);

                currentGreen =
                        possibleMoves;

                if (chessGame.getTurn() == 1) {

                    whitePassant[col] = 2;

                } else {

                    blackPassant[col] = 2;
                }

                chessGame.updateTurn();

                moveCompleted = true;
            }
        }

        if (!moveCompleted) {

            resetBackgrounds();
            highlightCheckedKing();
            updateStatusUi();
            return;
        }

        oldRow = -1;
        oldCol = -1;

        // ---------------------------------
        // PROMOTION - 4 OPTIONS
        // ---------------------------------

        if (game[row][col] == 1 &&
                row == 7) {

            promotionRequired = true;
            showPromotionDialog(
                    row,
                    col,
                    false);
        }

        else if (game[row][col] == 7 &&
                row == 0) {

            promotionRequired = true;
            showPromotionDialog(
                    row,
                    col,
                    true);
        }

        if (promotionRequired) {
            return;
        }

        chessGame.recordCurrentPosition();

        resetBackgrounds();
        highlightCheckedKing();

        checkGameEnd();
        updateStatusUi();
    }

    // =========================================================
    // UNDO + TIMER
    // =========================================================

    private void saveUndoState() {

        chessGame.saveStateForUndo();

        undoWhiteTimeMs = whiteTimeMs;
        undoBlackTimeMs = blackTimeMs;

        undoWhitePassant =
                whitePassant.clone();

        undoBlackPassant =
                blackPassant.clone();
    }

    private void undoMove() {

        if (!chessGame.undoLastMove()) {
            return;
        }

        game = chessGame.getGame();

        whiteTimeMs = undoWhiteTimeMs;
        blackTimeMs = undoBlackTimeMs;

        if (undoWhitePassant != null) {
            whitePassant =
                    undoWhitePassant.clone();
        }

        if (undoBlackPassant != null) {
            blackPassant =
                    undoBlackPassant.clone();
        }

        oldRow = -1;
        oldCol = -1;

        currentGreen =
                new int[Chess.SIDE][Chess.SIDE];

        timerRunning = true;

        enableButtons(true);
        resetButtons();
        resetBackgrounds();
        highlightCheckedKing();
        updateTimerViews();
        updateStatusUi();
    }

    private void updateTimerViews() {

        if (topTimer != null) {
            topTimer.setText(
                    formatTime(blackTimeMs));
        }

        if (bottomTimer != null) {
            bottomTimer.setText(
                    formatTime(whiteTimeMs));
        }
    }

    private String formatTime(long timeMs) {

        long totalSeconds =
                Math.max(0, timeMs) / 1000;

        long minutes =
                totalSeconds / 60;

        long seconds =
                totalSeconds % 60;

        return String.format(
                "%02d:%02d",
                minutes,
                seconds);
    }

    private void onTimeOut(
            boolean blackLost) {

        timerRunning = false;
        enableButtons(false);

        String winner =
                blackLost
                        ? "WHITE WINS"
                        : "BLACK WINS";

        status.setText(
                winner + " - TIME");

        AlertDialog.Builder alert =
                new AlertDialog.Builder(this);

        alert.setTitle(winner);

        alert.setMessage(
                "Time out\nPlay again?");

        PlayDialog playAgain =
                new PlayDialog();

        alert.setPositiveButton(
                "YES",
                playAgain);

        alert.setNegativeButton(
                "NO",
                playAgain);

        alert.show();
    }

    @Override
    protected void onDestroy() {

        timerHandler.removeCallbacks(
                timerRunnable);

        super.onDestroy();
    }

    // =========================================================
    // PROMOTION
    // =========================================================

    public void showPromotionDialog(
            final int row,
            final int col,
            final boolean white) {

        final String[] pieces = {
                "Queen",
                "Rook",
                "Bishop",
                "Knight"
        };

        AlertDialog.Builder alert =
                new AlertDialog.Builder(this);

        alert.setTitle(
                "Choose promotion");

        alert.setCancelable(false);

        alert.setItems(
                pieces,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(
                            DialogInterface dialog,
                            int which) {

                        int piece;

                        if (white) {

                            if (which == 0) {
                                piece = 11;
                            } else if (which == 1) {
                                piece = 8;
                            } else if (which == 2) {
                                piece = 10;
                            } else {
                                piece = 9;
                            }

                            game =
                                    chessGame.upgradeWhitePawn(
                                            row,
                                            col,
                                            piece);

                        } else {

                            if (which == 0) {
                                piece = 5;
                            } else if (which == 1) {
                                piece = 2;
                            } else if (which == 2) {
                                piece = 4;
                            } else {
                                piece = 3;
                            }

                            game =
                                    chessGame.upgradeBlackPawn(
                                            row,
                                            col,
                                            piece);
                        }

                        movePieces(row, col);

                        chessGame.recordCurrentPosition();

                        resetBackgrounds();
                        highlightCheckedKing();

                        checkGameEnd();
                        updateStatusUi();
                    }
                });

        alert.show();
    }

    // =========================================================
    // CHECK / CHECKMATE / DRAW
    // =========================================================

    public void highlightCheckedKing() {

        if (chessGame.isKingInCheck(1)) {

            int[] kingPos =
                    chessGame.getKingPosition(1);

            if (kingPos[0] >= 0 &&
                    kingPos[1] >= 0) {

                buttons[kingPos[0]][kingPos[1]]
                        .setBackground(
                                getDrawable(
                                        R.drawable.red_box));
            }
        }

        if (chessGame.isKingInCheck(2)) {

            int[] kingPos =
                    chessGame.getKingPosition(2);

            if (kingPos[0] >= 0 &&
                    kingPos[1] >= 0) {

                buttons[kingPos[0]][kingPos[1]]
                        .setBackground(
                                getDrawable(
                                        R.drawable.red_box));
            }
        }
    }

    public void checkGameEnd() {

        if (chessGame.whoWon() != 0) {

            int winner =
                    chessGame.whoWon();

            enableButtons(false);
            timerRunning = false;

            if (winner == 1) {
                blackWins++;
            }

            if (winner == 2) {
                whiteWins++;
            }

            updateStatusUi();
            showNewGameDialog();
            return;
        }

        if (chessGame.isDrawByStalemate()) {

            enableButtons(false);
            timerRunning = false;

            updateStatusUi();

            showDrawDialog(
                    "Stalemate");
            return;
        }

        if (chessGame.isFivefoldRepetition()) {

            enableButtons(false);
            timerRunning = false;

            updateStatusUi();

            showDrawDialog(
                    "Draw by repetition");
        }
    }

    public void showNewGameDialog() {

        AlertDialog.Builder alert =
                new AlertDialog.Builder(this);

        alert.setTitle(
                chessGame.result());

        alert.setMessage(
                "Play again?");

        PlayDialog playAgain =
                new PlayDialog();

        alert.setPositiveButton(
                "YES",
                playAgain);

        alert.setNegativeButton(
                "NO",
                playAgain);

        alert.show();
    }

    public void showDrawDialog(
            String reason) {

        AlertDialog.Builder alert =
                new AlertDialog.Builder(this);

        alert.setTitle("Draw");

        alert.setMessage(
                reason + "\nPlay again?");

        PlayDialog playAgain =
                new PlayDialog();

        alert.setPositiveButton(
                "YES",
                playAgain);

        alert.setNegativeButton(
                "NO",
                playAgain);

        alert.show();
    }

    private void showRestartDialog() {

        AlertDialog.Builder alert =
                new AlertDialog.Builder(this);

        alert.setTitle("New game");

        alert.setMessage(
                "Start a new game?");

        alert.setPositiveButton(
                "YES",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(
                            DialogInterface dialog,
                            int which) {

                        resetWholeGame();
                    }
                });

        alert.setNegativeButton(
                "NO",
                null);

        alert.show();
    }

    // =========================================================
    // BOARD DRAWING
    // =========================================================

    public void resetButtons() {

        for (int row = 0;
             row < Chess.SIDE;
             row++) {

            for (int col = 0;
                 col < Chess.SIDE;
                 col++) {

                movePieces(row, col);
            }
        }
    }

    public void enableButtons(
            boolean enabled) {

        for (int row = 0;
             row < Chess.SIDE;
             row++) {

            for (int col = 0;
                 col < Chess.SIDE;
                 col++) {

                buttons[row][col]
                        .setEnabled(enabled);
            }
        }
    }

    public void resetBackgrounds() {

        int i = 0;
        int p = 1;

        for (int r = 0;
             r < Chess.SIDE - 1;
             r += 2) {

            for (int c = 0;
                 c < Chess.SIDE;
                 c++) {

                buttons[r + i][c]
                        .setBackground(
                                getDrawable(
                                        R.drawable.white_box));

                buttons[r + p][c]
                        .setBackground(
                                getDrawable(
                                        R.drawable.black_box));

                i = i == 0 ? 1 : 0;
                p = p == 0 ? 1 : 0;
            }
        }
    }

    public void movePieces(
            int row,
            int col) {

        if (game[row][col] == 1) {
            buttons[row][col]
                    .setForeground(
                            getDrawable(
                                    R.drawable.black_pawn));
        }

        if (game[row][col] == 2) {
            buttons[row][col]
                    .setForeground(
                            getDrawable(
                                    R.drawable.black_castle));
        }

        if (game[row][col] == 3) {
            buttons[row][col]
                    .setForeground(
                            getDrawable(
                                    R.drawable.black_horse));
        }

        if (game[row][col] == 4) {
            buttons[row][col]
                    .setForeground(
                            getDrawable(
                                    R.drawable.black_bishop));
        }

        if (game[row][col] == 5) {
            buttons[row][col]
                    .setForeground(
                            getDrawable(
                                    R.drawable.black_queen));
        }

        if (game[row][col] == 6) {
            buttons[row][col]
                    .setForeground(
                            getDrawable(
                                    R.drawable.black_king));
        }

        if (game[row][col] == 7) {
            buttons[row][col]
                    .setForeground(
                            getDrawable(
                                    R.drawable.white_pawn));
        }

        if (game[row][col] == 8) {
            buttons[row][col]
                    .setForeground(
                            getDrawable(
                                    R.drawable.white_castle));
        }

        if (game[row][col] == 9) {
            buttons[row][col]
                    .setForeground(
                            getDrawable(
                                    R.drawable.white_horse));
        }

        if (game[row][col] == 10) {
            buttons[row][col]
                    .setForeground(
                            getDrawable(
                                    R.drawable.white_bishop));
        }

        if (game[row][col] == 11) {
            buttons[row][col]
                    .setForeground(
                            getDrawable(
                                    R.drawable.white_queen));
        }

        if (game[row][col] == 12) {
            buttons[row][col]
                    .setForeground(
                            getDrawable(
                                    R.drawable.white_king));
        }

        if (game[row][col] == 0) {
            buttons[row][col]
                    .setForeground(null);
        }
    }

    // =========================================================
    // STATUS UI
    // =========================================================

    private void updateStatusUi() {

        String mainStatus;

        if (chessGame.whoWon() == 1) {

            mainStatus = "BLACK WINS";

        } else if (chessGame.whoWon() == 2) {

            mainStatus = "WHITE WINS";

        } else if (chessGame.isDrawByStalemate()) {

            mainStatus = "DRAW - STALEMATE";

        } else if (chessGame.isFivefoldRepetition()) {

            mainStatus = "DRAW - REPETITION";

        } else if (chessGame.isKingInCheck(1)) {

            mainStatus = "WHITE IN CHECK";

        } else if (chessGame.isKingInCheck(2)) {

            mainStatus = "BLACK IN CHECK";

        } else if (chessGame.getTurn() == 1) {

            mainStatus = "WHITE'S TURN";

        } else {

            mainStatus = "BLACK'S TURN";
        }

        status.setText(mainStatus);

        scoreText.setText(
                "Black Wins: "
                        + blackWins
                        + "     •     White Wins: "
                        + whiteWins);

        // Static timer labels for UI only.
        // Timer logic can be added later without touching chess rules.
        updateTimerViews();
        
    }

    private void resetWholeGame() {

        chessGame.resetGame();
        game = chessGame.getGame();

        for (int i = 0;
             i < Chess.SIDE;
             i++) {

            whitePassant[i] = 0;
            blackPassant[i] = 0;
        }

        oldRow = -1;
        oldCol = -1;

        whiteTimeMs = START_TIME_MS;
        blackTimeMs = START_TIME_MS;
        timerRunning = true;

        currentGreen =
                new int[Chess.SIDE][Chess.SIDE];

        enableButtons(true);
        resetButtons();
        resetBackgrounds();
        highlightCheckedKing();
        updateStatusUi();
    }

    // =========================================================
    // CLICK HANDLERS
    // =========================================================

    private class ButtonHandler
            implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            for (int row = 0;
                 row < Chess.SIDE;
                 row++) {

                for (int col = 0;
                     col < Chess.SIDE;
                     col++) {

                    if (v == buttons[row][col]) {

                        update(row, col);
                        return;
                    }
                }
            }
        }
    }

    private class PlayDialog
            implements DialogInterface.OnClickListener {

        @Override
        public void onClick(
                DialogInterface dialog,
                int id) {

            if (id == -1) {

                resetWholeGame();

            } else if (id == -2) {

                MainActivity.this.finish();
            }
        }
    }
}
