package com.example.chessgame;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChessBotApiClient {

    private static final String API_URL =
            "https://lichess.org/api/cloud-eval";

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

                        HttpURLConnection connection = null;

                        try {

                            String urlString =
                                    API_URL
                                            + "?fen="
                                            + URLEncoder.encode(
                                                    fen,
                                                    "UTF-8")
                                            + "&multiPv=1";

                            URL url =
                                    new URL(urlString);

                            connection =
                                    (HttpURLConnection)
                                            url.openConnection();

                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(10000);
                            connection.setReadTimeout(15000);
                            connection.setRequestProperty(
                                    "Accept",
                                    "application/json");

                            int responseCode =
                                    connection.getResponseCode();

                            InputStream inputStream =
                                    responseCode >= 200 &&
                                            responseCode < 300
                                            ? connection.getInputStream()
                                            : connection.getErrorStream();

                            String response =
                                    readAll(inputStream);

                            if (responseCode == 404) {
                                callback.onError(
                                        "Lichess has no cloud evaluation for this position");
                                return;
                            }

                            if (responseCode == 429) {
                                callback.onError(
                                        "Lichess rate limit. Try again later.");
                                return;
                            }

                            if (responseCode < 200 ||
                                    responseCode >= 300) {
                                callback.onError(
                                        "Lichess HTTP "
                                                + responseCode
                                                + " - "
                                                + response);
                                return;
                            }

                            JSONObject json =
                                    new JSONObject(response);

                            JSONArray pvs =
                                    json.optJSONArray("pvs");

                            if (pvs == null ||
                                    pvs.length() == 0) {
                                callback.onError(
                                        "Lichess returned no principal variation");
                                return;
                            }

                            JSONObject firstPv =
                                    pvs.getJSONObject(0);

                            String moves =
                                    firstPv.optString(
                                            "moves",
                                            "")
                                            .trim();

                            if (moves.isEmpty()) {
                                callback.onError(
                                        "Lichess returned no move");
                                return;
                            }

                            String firstMove =
                                    moves.split("\\s+")[0]
                                            .toLowerCase();

                            if (!firstMove.matches(
                                    "^[a-h][1-8][a-h][1-8][qrbn]?$")) {
                                callback.onError(
                                        "Invalid Lichess move: "
                                                + firstMove);
                                return;
                            }

                            callback.onSuccess(firstMove);

                        } catch (Exception e) {

                            callback.onError(
                                    e.getMessage() == null
                                            ? e.getClass()
                                                .getSimpleName()
                                            : e.getMessage());

                        } finally {

                            if (connection != null) {
                                connection.disconnect();
                            }
                        }
                    }
                });
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

    public void shutdown() {
        executor.shutdownNow();
    }
}
