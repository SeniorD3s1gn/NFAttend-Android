package com.nfa.android.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.nfa.android.BuildConfig;
import com.nfa.android.listeners.ConnectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConnectionManager {

    private static final String TAG = ConnectionManager.class.getSimpleName();

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String APIKEY = BuildConfig.API_KEY.trim();

    private URL url;
    private ConnectionListener listener;
    private HandlerThread thread;

    public ConnectionManager(String url, ConnectionListener listener) {
        try {
            this.url = new URL(url);
            this.listener = listener;
            thread = new HandlerThread("NetworkThread");
            thread.start();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

    public void login(final String email, final String password) {
        Handler handler = new Handler(thread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> values = new HashMap<>();
                values.put("email", email);
                values.put("password", password);
                try {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("secret", APIKEY);

                    String encoded = getDataString(values);

                    try(OutputStream os = conn.getOutputStream()) {
                        byte[] input = encoded.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    try(BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        Log.d(TAG, response.toString());
                        listener.onConnectionFinish("Login", new JSONObject(response.toString()));
                    }

                } catch (IOException | JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void register(final String firstName, final String lastName, final String email,
                         final String id, final String password) {
        Handler handler = new Handler(thread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> values = new HashMap<>();
                values.put("first_name", firstName);
                values.put("last_name", lastName);
                values.put("email", email);
                values.put("password", password);
                values.put("id", id);
                values.put("device", UUID.randomUUID().toString());
                try {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("secret", APIKEY);

                    String encoded = getDataString(values);

                    try(OutputStream os = conn.getOutputStream()) {
                        byte[] input = encoded.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    try(BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        Log.d(TAG, response.toString());
                        listener.onConnectionFinish("Register", new JSONObject(response.toString()));
                    }

                } catch (IOException | JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void retrieveStudent(final String id) {
        Handler handler = new Handler(thread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    URL urlX = new URL(url + "" + id);
                    HttpURLConnection conn = (HttpURLConnection) urlX.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", USER_AGENT);
                    conn.setDoOutput(false);
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("secret", APIKEY);

                    int status = conn.getResponseCode();

                    Log.d(TAG, "status code: " + status);

                    try(BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        Log.d(TAG, response.toString());
                        listener.onConnectionFinish("Student", new JSONObject(response.toString()));
                    }

                } catch (IOException | JSONException ex) {
                    Log.d(TAG, ex.toString());
                }
            }
        });
    }

    private String getDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

}
