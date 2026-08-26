package com.example.chessgame;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChessBotApiClient {

    // API 1: chess-api.com
    private static final String PRIMARY_API =
            "https://chess-api.com/v1";

    // API 2: fallback Stockfish API.
    private static final String FALLBACK_API =
            "https://stockfish.online/api/s/v2.php";

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    public interface Callback {
        void onSuccess(String uciMove);
        void onError(String message);
    }

    public void getBestMove(
            final String fen,
            final int depth,
            final Callback callback) {

        executor.execute(
                new Runnable() {
                    @Override
                    public void run() {

                        try {
                            String move =
                                    requestChessApi(
                                            fen,
                                            depth);

                            if (isValidUci(move)) {
                                callback.onSuccess(move);
                                return;
                            }

                        } catch (Exception ignored) {
                            // Neu chess-api.com bi loi SSL/certificate,
                            // thu API Stockfish thu hai.
                        }

                        try {
                            String move =
                                    requestStockfishOnline(
                                            fen,
                                            depth);

                            if (isValidUci(move)) {
                                callback.onSuccess(move);
                                return;
                            }

                            callback.onError(
                                    "Stockfish API did not return a valid move");

                        } catch (Exception e) {

                            callback.onError(
                                    "Cannot connect to bot API. "
                                            + safeMessage(e));
                        }
                    }
                });
    }

    // =========================================================
    // API 1 - chess-api.com
    // =========================================================

    private String requestChessApi(
            String fen,
            int depth)
            throws Exception {

        HttpURLConnection connection = null;

        try {
            URL url =
                    new URL(PRIMARY_API);

            connection =
                    (HttpURLConnection)
                            url.openConnection();

            connection.setRequestMethod("POST");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(15000);
            connection.setDoOutput(true);

            connection.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=UTF-8");

            JSONObject request =
                    new JSONObject();

            request.put("fen", fen);
            request.put(
                    "depth",
                    Math.max(
                            1,
                            Math.min(depth, 18)));

            request.put("variants", 1);

            byte[] body =
                    request.toString()
                            .getBytes(
                                    StandardCharsets.UTF_8);

            OutputStream outputStream =
                    connection.getOutputStream();

            outputStream.write(body);
            outputStream.flush();
            outputStream.close();

            int responseCode =
                    connection.getResponseCode();

            String response =
                    readAll(
                            responseCode >= 200 &&
                                    responseCode < 300
                                    ? connection.getInputStream()
                                    : connection.getErrorStream());

            if (responseCode < 200 ||
                    responseCode >= 300) {

                throw new Exception(
                        "chess-api HTTP "
                                + responseCode);
            }

            JSONObject json =
                    new JSONObject(response);

            return json.optString(
                    "move",
                    "");

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // =========================================================
    // API 2 - stockfish.online
    // =========================================================

    private String requestStockfishOnline(
            String fen,
            int depth)
            throws Exception {

        int safeDepth =
                Math.max(
                        1,
                        Math.min(depth, 15));

        String urlString =
                FALLBACK_API
                        + "?fen="
                        + URLEncoder.encode(
                                fen,
                                "UTF-8")
                        + "&depth="
                        + safeDepth;

        HttpURLConnection connection = null;

        try {
            URL url =
                    new URL(urlString);

            connection =
                    (HttpURLConnection)
                            url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(20000);

            int responseCode =
                    connection.getResponseCode();

            String response =
                    readAll(
                            responseCode >= 200 &&
                                    responseCode < 300
                                    ? connection.getInputStream()
                                    : connection.getErrorStream());

            if (responseCode < 200 ||
                    responseCode >= 300) {

                throw new Exception(
                        "stockfish.online HTTP "
                                + responseCode);
            }

            JSONObject json =
                    new JSONObject(response);

            if (!json.optBoolean(
                    "success",
                    true)) {

                throw new Exception(
                        json.optString(
                                "data",
                                "Stockfish API error"));
            }

            String bestMove =
                    json.optString(
                            "bestmove",
                            "");

            // Vi du:
            // "bestmove e7e5 ponder g1f3"
            String[] parts =
                    bestMove.trim()
                            .split("\\s+");

            if (parts.length >= 2 &&
                    "bestmove"
                            .equalsIgnoreCase(
                                    parts[0])) {

                return parts[1];
            }

            return bestMove;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private boolean isValidUci(
            String move) {

        if (move == null) {
            return false;
        }

        move = move.trim()
                .toLowerCase();

        return move.matches(
                "^[a-h][1-8][a-h][1-8][qrbn]?$");
    }

    private String readAll(
            InputStream inputStream)
            throws Exception {

        if (inputStream == null) {
            return "";
        }

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                inputStream,
                                StandardCharsets.UTF_8));

        StringBuilder builder =
                new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        reader.close();

        return builder.toString();
    }

    private String safeMessage(
            Exception e) {

        if (e == null) {
            return "Unknown network error";
        }

        String message =
                e.getMessage();

        if (message == null ||
                message.trim().isEmpty()) {

            return e.getClass()
                    .getSimpleName();
        }

        return message;
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
