package com.example.chessgame;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.util.HashSet;
import java.util.Set;

/** Keeps every short chess sound outside MainActivity. */
class ChessSoundManager {
    static final int MOVE_NONE = 0;
    static final int MOVE_NORMAL = 1;
    static final int MOVE_CAPTURE = 2;
    static final int MOVE_CASTLE = 3;
    static final int MOVE_PROMOTE = 4;

    private final SoundPool pool;
    private final Set<Integer> loadedSounds = new HashSet<>();
    private final Set<Integer> pendingSounds = new HashSet<>();

    private final int moveSelf, moveOpponent, capture, check, castle, promote;
    private final int illegal, gameStart, gameEnd, gameDraw, gameLose, tenSeconds, notify;

    private boolean whiteTenSecondsPlayed;
    private boolean blackTenSecondsPlayed;
    private boolean released;

    ChessSoundManager(Context context) {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        pool = new SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(attributes)
                .build();

        pool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            if (released || status != 0) return;

            loadedSounds.add(sampleId);

            // If a sound was requested before SoundPool finished loading it,
            // play it once as soon as it becomes ready. This fixes game_start
            // occasionally being silent when MainActivity opens quickly.
            if (pendingSounds.remove(sampleId)) {
                playLoaded(sampleId);
            }
        });

        moveSelf = pool.load(context, R.raw.move_self, 1);
        moveOpponent = pool.load(context, R.raw.move_opponent, 1);
        capture = pool.load(context, R.raw.capture, 1);
        check = pool.load(context, R.raw.move_check, 1);
        castle = pool.load(context, R.raw.castle, 1);
        promote = pool.load(context, R.raw.promote, 1);
        illegal = pool.load(context, R.raw.illegal, 1);
        gameStart = pool.load(context, R.raw.game_start, 1);
        gameEnd = pool.load(context, R.raw.game_end, 1);
        gameDraw = pool.load(context, R.raw.game_draw, 1);
        gameLose = pool.load(context, R.raw.game_lose, 1);
        tenSeconds = pool.load(context, R.raw.tenseconds, 1);
        notify = pool.load(context, R.raw.notify, 1);
    }

    private void play(int soundId) {
        if (released || soundId == 0) return;

        if (loadedSounds.contains(soundId)) {
            playLoaded(soundId);
        } else {
            pendingSounds.add(soundId);
        }
    }

    private void playLoaded(int soundId) {
        if (!released) {
            pool.play(soundId, 1f, 1f, 1, 0, 1f);
        }
    }

    void playMove(int moveType, int movingSide, Chess chess) {
        if (chess.whoWon() != 0 || chess.isDrawByStalemate() || chess.isFivefoldRepetition()) {
            return;
        }

        int checkedSide = movingSide == 1 ? 2 : 1;
        if (chess.isKingInCheck(checkedSide)) {
            play(check);
            return;
        }

        if (moveType == MOVE_CAPTURE) play(capture);
        else if (moveType == MOVE_CASTLE) play(castle);
        else if (moveType == MOVE_PROMOTE) play(promote);
        else if (moveType == MOVE_NORMAL) play(movingSide == 1 ? moveSelf : moveOpponent);
    }

    void playIllegal() { play(illegal); }
    void playGameStart() { play(gameStart); }
    void playDraw() { play(gameDraw); }
    void playNotify() { play(notify); }

    // White is the bottom player labelled "You" in the current UI.
    void playGameResult(int winner) {
        play(winner == 2 ? gameEnd : gameLose);
    }

    void checkTenSeconds(long whiteTimeMs, long blackTimeMs) {
        if (whiteTimeMs > 10_000) whiteTenSecondsPlayed = false;
        if (blackTimeMs > 10_000) blackTenSecondsPlayed = false;

        if (whiteTimeMs > 0 && whiteTimeMs <= 10_000 && !whiteTenSecondsPlayed) {
            whiteTenSecondsPlayed = true;
            play(tenSeconds);
        }

        if (blackTimeMs > 0 && blackTimeMs <= 10_000 && !blackTenSecondsPlayed) {
            blackTenSecondsPlayed = true;
            play(tenSeconds);
        }
    }

    void syncClock(long whiteTimeMs, long blackTimeMs) {
        whiteTenSecondsPlayed = whiteTimeMs <= 10_000;
        blackTenSecondsPlayed = blackTimeMs <= 10_000;
    }

    void resetClockWarnings() {
        whiteTenSecondsPlayed = false;
        blackTenSecondsPlayed = false;
    }

    void release() {
        released = true;
        pendingSounds.clear();
        loadedSounds.clear();
        pool.release();
    }
}
