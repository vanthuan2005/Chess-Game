package com.example.chessgame;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ChessBotApiClient {

    private static final String API_URL =
            "https://chess-api.com/v1";

    private static final MediaType JSON =
            MediaType.parse(
                    "application/json; charset=utf-8");

    private final OkHttpClient client =
            new OkHttpClient.Builder()
                    .connectTimeout(
                            10,
                            TimeUnit.SECONDS)
                    .readTimeout(
                            20,
                            TimeUnit.SECONDS)
                    .writeTimeout(
                            10,
                            TimeUnit.SECONDS)
                    .build();

    public interface CallbackResult {
        void onSuccess(String uciMove);
        void onError(String message);
    }

    /*
     * Keep compatibility with the MainActivity version already sent:
     * MainActivity calls ChessBotApiClient.Callback.
     */
    public interface Callback {
        void onSuccess(String uciMove);
        void onError(String message);
    }

    public void getBestMove(
            String fen,
            int depth,
            final Callback callback) {

        if (callback == null) {
            return;
        }

        try {

            JSONObject json =
                    new JSONObject();

            json.put(
                    "fen",
                    fen);

            json.put(
                    "depth",
                    Math.max(
                            1,
                            Math.min(depth, 18)));

            json.put(
                    "variants",
                    1);

            RequestBody body =
                    RequestBody.create(
                            json.toString(),
                            JSON);

            Request request =
                    new Request.Builder()
                            .url(API_URL)
                            .post(body)
                            .header(
                                    "Accept",
                                    "application/json")
                            .build();

            client.newCall(request)
                    .enqueue(
                            new okhttp3.Callback() {

                                @Override
                                public void onFailure(
                                        Call call,
                                        IOException e) {

                                    callback.onError(
                                            safeMessage(e));
                                }

                                @Override
                                public void onResponse(
                                        Call call,
                                        Response response)
                                        throws IOException {

                                    try {

                                        ResponseBody responseBody =
                                                response.body();

                                        String text =
                                                responseBody == null
                                                        ? ""
                                                        : responseBody.string();

                                        if (!response.isSuccessful()) {

                                            callback.onError(
                                                    "HTTP "
                                                            + response.code()
                                                            + " - "
                                                            + text);

                                            return;
                                        }

                                        JSONObject result =
                                                new JSONObject(text);

                                        String move =
                                                result.optString(
                                                        "move",
                                                        "")
                                                        .trim()
                                                        .toLowerCase();

                                        if (!isValidUci(move)) {

                                            callback.onError(
                                                    "API returned invalid move: "
                                                            + move);

                                            return;
                                        }

                                        callback.onSuccess(move);

                                    } catch (Exception e) {

                                        callback.onError(
                                                safeMessage(e));

                                    } finally {

                                        response.close();
                                    }
                                }
                            });

        } catch (Exception e) {

            callback.onError(
                    safeMessage(e));
        }
    }

    private boolean isValidUci(
            String move) {

        return move != null &&
                move.matches(
                        "^[a-h][1-8][a-h][1-8][qrbn]?$");
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

        client.dispatcher()
                .cancelAll();

        client.connectionPool()
                .evictAll();
    }
}
