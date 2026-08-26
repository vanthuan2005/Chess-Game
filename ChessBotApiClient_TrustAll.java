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

                            // BO QUA KIEM TRA CERTIFICATE.
                            connection.setSSLSocketFactory(
                                    sslContext.getSocketFactory());

                            // BO QUA KIEM TRA HOSTNAME.
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
                            connection.setReadTimeout(25000);
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

                            request.put(
                                    "variants",
                                    1);

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

    private SSLContext createUnsafeSslContext()
            throws Exception {

        TrustManager[] trustAllCertificates =
                new TrustManager[]{
                        new X509TrustManager() {

                            @Override
                            public void checkClientTrusted(
                                    X509Certificate[] chain,
                                    String authType) {
                                // Trust all.
                            }

                            @Override
                            public void checkServerTrusted(
                                    X509Certificate[] chain,
                                    String authType) {
                                // Trust all.
                            }

                            @Override
                            public X509Certificate[]
                            getAcceptedIssuers() {

                                return new X509Certificate[0];
                            }
                        }
                };

        // TLS works on Android Pie.
        SSLContext sslContext =
                SSLContext.getInstance("TLS");

        sslContext.init(
                null,
                trustAllCertificates,
                new SecureRandom());

        return sslContext;
    }

    private boolean isValidUci(
            String move) {

        return move != null &&
                move.matches(
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

    public void shutdown() {
        executor.shutdownNow();
    }
}
