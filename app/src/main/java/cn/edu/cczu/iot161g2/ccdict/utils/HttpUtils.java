package cn.edu.cczu.iot161g2.ccdict.utils;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 提供 HTTP 请求相关方法.
 */
public class HttpUtils {
    private static final int DEFAULT_CONNECTION_TIME_OUT = 8000;
    private static final int DEFAULT_READ_TIME_OUT = 8000;

    @NonNull
    public static String get(String urlString) {
        String response = "";

        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(DEFAULT_CONNECTION_TIME_OUT);
            connection.setReadTimeout(DEFAULT_READ_TIME_OUT);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line)
                        .append("\n");
            }
            response = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;
    }
}
