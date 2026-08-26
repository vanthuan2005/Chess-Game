package com.example.chessgame;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL);

        root.setGravity(
                Gravity.CENTER_HORIZONTAL);

        root.setBackgroundColor(
                Color.parseColor("#1F2025"));

        root.setPadding(
                dp(28),
                dp(36),
                dp(28),
                dp(36));

        // =========================
        // LOGO BLOCK - TOP CENTER
        // =========================

        LinearLayout logoBlock =
                new LinearLayout(this);

        logoBlock.setOrientation(
                LinearLayout.VERTICAL);

        logoBlock.setGravity(
                Gravity.CENTER_HORIZONTAL);

        TextView crown =
                new TextView(this);

        crown.setText("♛");
        crown.setTextSize(64);
        crown.setTextColor(
                Color.parseColor("#D6A35B"));
        crown.setGravity(Gravity.CENTER);

        TextView title =
                new TextView(this);

        title.setText("CHESS");
        title.setTextColor(Color.WHITE);
        title.setTextSize(34);
        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        TextView sub =
                new TextView(this);

        sub.setText("CHOOSE GAME MODE");
        sub.setTextColor(
                Color.parseColor("#9D9FA6"));
        sub.setTextSize(14);
        sub.setGravity(Gravity.CENTER);

        logoBlock.addView(crown);
        logoBlock.addView(title);
        logoBlock.addView(sub);

        root.addView(
                logoBlock,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

        // Khoang trong giua logo va cum button.
        // Hai Space co cung weight de tam cum button
        // nam dung giua phan man hinh con lai.
        Space topSpace =
                new Space(this);

        root.addView(
                topSpace,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f));

        // =========================
        // BUTTON BLOCK - CENTER
        // =========================

        LinearLayout buttonBlock =
                new LinearLayout(this);

        buttonBlock.setOrientation(
                LinearLayout.VERTICAL);

        buttonBlock.setGravity(
                Gravity.CENTER);

        Button human =
                createButton(
                        "♟  PLAY WITH FRIEND");

        Button bot =
                createButton(
                        "♞  PLAY WITH BOT");

        buttonBlock.addView(
                human,
                buttonParams());

        buttonBlock.addView(
                bot,
                buttonParams());

        root.addView(
                buttonBlock,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

        Space bottomSpace =
                new Space(this);

        root.addView(
                bottomSpace,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f));

        human.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startActivity(
                                new Intent(
                                        MenuActivity.this,
                                        MainActivity.class));
                    }
                });

        bot.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(
                                MenuActivity.this,
                                "Play with bot: Coming soon",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

        setContentView(root);
    }

    private Button createButton(
            String text) {

        Button button =
                new Button(this);

        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(16);
        button.setAllCaps(false);

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.parseColor("#2A2C32"));

        background.setCornerRadius(
                dp(14));

        background.setStroke(
                dp(1),
                Color.parseColor("#404249"));

        button.setBackground(background);

        return button;
    }

    private LinearLayout.LayoutParams
    buttonParams() {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(58));

        params.bottomMargin = dp(16);

        return params;
    }

    private int dp(int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int)
                (value * density + 0.5f);
    }
}
