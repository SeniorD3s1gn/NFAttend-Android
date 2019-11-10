package com.nfa.android.listeners;

import org.json.JSONArray;
import org.json.JSONObject;

public interface ConnectionListener {

    void onConnectionFinish(String eventType, JSONObject object);
    void onConnectionFinish(String eventType, JSONArray array);

}
