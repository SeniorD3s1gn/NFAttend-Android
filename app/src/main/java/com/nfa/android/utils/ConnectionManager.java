package com.nfa.android.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConnectionManager {

    private static final String TAG = ConnectionManager.class.getSimpleName();
    private Context context;
    private String url;

    public ConnectionManager(Context context, String url) {
        this.url = url;
        this.context = context;
    }

    public void login(final String email, String password) {
        JSONObject json;
        try {
            json = new JSONObject();
            json.put("email", email);
            json.put("password", password);
        } catch (JSONException ex) {
            Log.d(TAG, ex.getMessage());
            return;
        }
        createJsonRequest(json);
    }

    public void register(String firstName, String lastName, String email, String id, String password) {
        JSONObject json;
        try {
            json = new JSONObject();
            json.put("first_name", firstName);
            json.put("last_name", lastName);
            json.put("email", email);
            json.put("id", id);
            json.put("password", password);
            json.put("device", UUID.randomUUID());
        } catch (JSONException ex) {
            Log.d(TAG, ex.getMessage());
            return;
        }
        createJsonRequest(json);
    }

    private void createJsonRequest(JSONObject json) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    Log.d(TAG, jsonError);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                final Map<String, String> headers = new HashMap<>();
                headers.put("secret", "/VeF/$9xJcv(![bZn[~%!zQy9U_jMoJIqC[?tZb_w&C-63O^X`+]sT4p;o+TTLI");
                return headers;
            }
        };
        queue.add(loginRequest);
    }

}
