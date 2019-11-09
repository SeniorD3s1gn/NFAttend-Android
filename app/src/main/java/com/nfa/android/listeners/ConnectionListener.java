package com.nfa.android.listeners;

import org.json.JSONObject;

public interface ConnectionListener {

    void onConnectionFinish(String eventType, JSONObject object);

}
