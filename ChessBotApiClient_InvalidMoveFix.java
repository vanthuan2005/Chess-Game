package com.example.chessgame;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ChessBotApiClient {

    private static final String API_URL =
            "https://chess-api.com/v1";

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

                        HttpsURLConnection connection = null;

                        try {

                            SSLContext sslContext =
                                    createUnsafeSslContext();

                            URL url =
                                    new URL(API_URL);

                            connection =
                                    (HttpsURLConnection)
                                            url.openConnection();

                            connection.setSSLSocketFactory(
                                    sslContext.getSocketFactory());

                            connection.setHostnameVerifier(
                                    new HostnameVerifier() {
                                        @Override
                                        public boolean verify(
                                                String hostname,
                                                SSLSession session) {
                                            return true;
                                        }
                                    });

                            connection.setRequestMethod("POST");
                            connection.setConnectTimeout(10000);
                            connection.setReadTimeout(30000);
                            connection.setDoOutput(true);

                            connection.setRequestProperty(
                                    "Content-Type",
                                    "application/json; charset=UTF-8");

                            connection.setRequestProperty(
                                    "Accept",
                                    "application/json");

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

                            InputStream inputStream =
                                    responseCode >= 200 &&
                                            responseCode < 300
                                            ? connection.getInputStream()
                                            : connection.getErrorStream();

                            String response =
                                    readAll(inputStream);

                            if (responseCode < 200 ||
                                    responseCode >= 300) {

                                callback.onError(
                                        "HTTP "
                                                + responseCode
                                                + " - "
                                                + response);

                                return;
                            }

                            JSONObject result =
                                    new JSONObject(response);

                            String move =
                                    extractUciMove(result);

                            if (!isValidUci(move)) {

                                callback.onError(
                                        "API response has no valid UCI move. RAW: "
                                                + response);

                                return;
                            }

                            callback.onSuccess(move);

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

    /*
     * chess-api.com normally returns:
     *   move: "e7e5"
     *
     * But for compatibility this method also accepts:
     *   lan: "e7e5"
     * or:
     *   from: "e7", to: "e5", promotion: "q"
     */
    private String extractUciMove(
            JSONObject result) {

        String move =
                normalizeMove(
                        result.optString(
                                "move",
                                ""));

        if (isValidUci(move)) {
            return move;
        }

        String lan =
                normalizeMove(
                        result.optString(
                                "lan",
                                ""));

        if (isValidUci(lan)) {
            return lan;
        }

        String from =
                result.optString(
                        "from",
                        "")
                        .trim()
                        .toLowerCase(Locale.US);

        String to =
                result.optString(
                        "to",
                        "")
                        .trim()
                        .toLowerCase(Locale.US);

        String promotion =
                result.optString(
                        "promotion",
                        "")
                        .trim()
                        .toLowerCase(Locale.US);

        if (from.matches("^[a-h][1-8]$") &&
                to.matches("^[a-h][1-8]$")) {

            String combined =
                    from + to;

            if (promotion.matches(
                    "^[qrbn]$")) {

                combined += promotion;
            }

            return combined;
        }

        return move;
    }

    private String normalizeMove(
            String move) {

        if (move == null) {
            return "";
        }

        move =
                move.trim()
                        .toLowerCase(Locale.US);

        // Remove common display separators if an API/proxy ever returns them.
        move =
                move.replace("→", "")
                        .replace("->", "")
                        .replace("-", "")
                        .replace(" ", "");

        // Convert e7e8=q to standard UCI e7e8q.
        move =
                move.replace("=", "");

        return move;
    }

    private boolean isValidUci(
            String move) {

        return move != null &&
                move.matches(
                        "^[a-h][1-8][a-h][1-8][qrbn]?$");
    }

    private SSLContext createUnsafeSslContext()
            throws Exception {

        TrustManager[] trustAllCertificates =
                new TrustManager[]{
                        new X509TrustManager() {

                            @Override
                            public void checkClientTrusted(
                                    X509Certificate[] chain,
                                    String authType) {
                            }

                            @Override
                            public void checkServerTrusted(
                                    X509Certificate[] chain,
                                    String authType) {
                            }

                            @Override
                            public X509Certificate[]
                            getAcceptedIssuers() {

                                return new X509Certificate[0];
                            }
                        }
                };

        SSLContext sslContext =
                SSLContext.getInstance("TLS");

        sslContext.init(
                null,
                trustAllCertificates,
                new SecureRandom());

        return sslContext;
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
