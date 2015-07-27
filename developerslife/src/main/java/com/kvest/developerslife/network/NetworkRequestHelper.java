package com.kvest.developerslife.network;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 23.12.13
 * Time: 23:48
 * To change this template use File | Settings | File Templates.
 */
public abstract class NetworkRequestHelper {
    private static final int CONNECT_TIMEOUT = 30000;
    private static final int READ_TIMEOUT = 120000;

    public static boolean loadFile(String urlString, File file) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type","*/*");
            conn.setRequestProperty("Accept", "*/*");

            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);

                try {
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                } finally {
                    try {
                        is.close();
                    } catch (IOException ioException) {}
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException ioException) {}
                }

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e("KVEST_TAG", "getFile error " + e + "(" + urlString + ")");
            return false;
        }
    }
}
