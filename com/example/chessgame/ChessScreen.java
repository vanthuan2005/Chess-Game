package com.example.chessgame;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** Builds Android views only. Game rules stay outside this class. */
class ChessScreen {
    interface Listener {
        void onSquareClicked(int row, int col);
        void onNewGameClicked();
        void onUndoClicked();
    }

    static class Views {
        final ScrollView root;
        final Button[][] buttons;
        final TextView status, topPlayerName, bottomPlayerName, topTimer, bottomTimer, scoreText;
        Views(ScrollView root, Button[][] buttons, TextView status,
              TextView topPlayerName, TextView bottomPlayerName,
              TextView topTimer, TextView bottomTimer, TextView scoreText) {
            this.root = root; this.buttons = buttons; this.status = status;
            this.topPlayerName = topPlayerName; this.bottomPlayerName = bottomPlayerName;
            this.topTimer = topTimer; this.bottomTimer = bottomTimer; this.scoreText = scoreText;
        }
    }

    private final Activity activity;
    private final Listener listener;

    ChessScreen(Activity activity, Listener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    Views build() {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        int horizontalMargin = dp(16);
        int boardSquareSize = (size.x - horizontalMargin * 2) / Chess.SIDE;

        ScrollView scrollView = new ScrollView(activity);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(Color.parseColor("#1F2025"));

        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(horizontalMargin, dp(18), horizontalMargin, dp(24));
        scrollView.addView(root, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));

        TextView title = new TextView(activity);
        title.setText("CHESS GAME");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = dp(18);
        root.addView(title, titleParams);

        LinearLayout topCard = createPlayerCard();
        TextView topPlayerName = createPlayerName("BLACK PLAYER", "Opponent");
        TextView topTimer = createTimer("10:00");
        topCard.addView(topPlayerName, new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        topCard.addView(topTimer);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dp(14);
        root.addView(topCard, cardParams);

        TextView scoreText = new TextView(activity);
        scoreText.setTextColor(Color.parseColor("#C7C8CC"));
        scoreText.setTextSize(14);
        scoreText.setGravity(Gravity.CENTER);
        scoreText.setPadding(0, 0, 0, dp(10));
        root.addView(scoreText);

        GridLayout board = new GridLayout(activity);
        board.setColumnCount(Chess.SIDE);
        board.setRowCount(Chess.SIDE);
        Button[][] buttons = new Button[Chess.SIDE][Chess.SIDE];
        for (int row = 0; row < Chess.SIDE; row++) {
            for (int col = 0; col < Chess.SIDE; col++) {
                final int r = row;
                final int c = col;
                Button button = new Button(activity);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) { listener.onSquareClicked(r, c); }
                });
                button.setPadding(0, 0, 0, 0);
                button.setMinWidth(0);
                button.setMinHeight(0);
                button.setAllCaps(false);
                buttons[row][col] = button;
                board.addView(button, boardSquareSize, boardSquareSize);
            }
        }
        LinearLayout.LayoutParams boardParams = new LinearLayout.LayoutParams(
                Chess.SIDE * boardSquareSize, Chess.SIDE * boardSquareSize);
        boardParams.gravity = Gravity.CENTER_HORIZONTAL;
        root.addView(board, boardParams);

        TextView status = new TextView(activity);
        status.setTextColor(Color.WHITE);
        status.setTextSize(23);
        status.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        status.setGravity(Gravity.CENTER);
        status.setPadding(dp(8), dp(16), dp(8), dp(16));
        root.addView(status, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout bottomCard = createPlayerCard();
        TextView bottomPlayerName = createPlayerName("WHITE PLAYER", "You");
        TextView bottomTimer = createTimer("10:00");
        bottomCard.addView(bottomPlayerName, new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        bottomCard.addView(bottomTimer);
        LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bottomParams.bottomMargin = dp(14);
        root.addView(bottomCard, bottomParams);

        LinearLayout actions = new LinearLayout(activity);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.CENTER);
        Button newGame = createActionButton("NEW GAME");
        Button undoButton = createActionButton("UNDO");
        LinearLayout.LayoutParams actionButtonParams = new LinearLayout.LayoutParams(0, dp(48), 1f);
        actionButtonParams.setMargins(dp(4), 0, dp(4), 0);
        actions.addView(newGame, actionButtonParams);
        actions.addView(undoButton, actionButtonParams);
        root.addView(actions, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        newGame.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { listener.onNewGameClicked(); }
        });
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { listener.onUndoClicked(); }
        });

        return new Views(scrollView, buttons, status, topPlayerName, bottomPlayerName,
                topTimer, bottomTimer, scoreText);
    }

    private LinearLayout createPlayerCard() {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(16), dp(13), dp(16), dp(13));
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.parseColor("#292B31"));
        background.setCornerRadius(dp(14));
        card.setBackground(background);
        return card;
    }

    private TextView createPlayerName(String name, String subtitle) {
        TextView view = new TextView(activity);
        view.setText(name + "\n" + subtitle);
        view.setTextColor(Color.WHITE);
        view.setTextSize(16);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setLineSpacing(dp(2), 1f);
        return view;
    }

    private TextView createTimer(String time) {
        TextView timer = new TextView(activity);
        timer.setText(time);
        timer.setTextColor(Color.WHITE);
        timer.setTextSize(27);
        timer.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        timer.setGravity(Gravity.CENTER);
        return timer;
    }

    private Button createActionButton(String text) {
        Button button = new Button(activity);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(13);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#34363D"));
        bg.setCornerRadius(dp(12));
        button.setBackground(bg);
        return button;
    }

    private int dp(int value) {
        return (int) (value * activity.getResources().getDisplayMetrics().density + 0.5f);
    }
}
