package com.nfa.android.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcA;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;

public class NFCManager {

    private static final String TAG = NFCManager.class.getSimpleName();

    private Activity activity;
    private NfcAdapter adapter;

    public NFCManager(Activity activity) {
        this.activity = activity;
    }

    private boolean verify() {
        adapter = NfcAdapter.getDefaultAdapter(activity);
        return adapter != null && adapter.isEnabled();
    }

    public boolean foregroundDispatch() {
        if (!verify()) {
            return false;
        }
        Intent i = new Intent(activity, getClass());
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pending = PendingIntent.getActivity(activity, 0, i, 0);
        IntentFilter[] filters = new IntentFilter[] {
                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        };
        String[][] tech = new String[][] {
                { android.nfc.tech.Ndef.class.getName() },
                { android.nfc.tech.NdefFormatable.class.getName() },
                { android.nfc.tech.MifareUltralight.class.getName() }
        };

        adapter.enableForegroundDispatch(activity, pending, filters, tech);
        return true;
    }

    public void disableForegroundDispatch() {
        adapter.disableForegroundDispatch(activity);
    }

    public boolean handleIntent(Intent intent) {
        Log.d(TAG, "New Intent");
        if (intent.getAction() == null) {
            return false;
        }
        if (intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED) ||
                intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Tag currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            MifareUltralight ultralight = MifareUltralight.get(currentTag);
            try {
                StringBuilder sb = new StringBuilder();
                ultralight.connect();
                int i = 4;
                while (i < 21) {
                    byte[] data = ultralight.readPages(i);
                    String v = new String(data, Charset.forName("UTF-8"));
                    sb.append(v);
                    i += 4;
                }
                ultralight.close();
                Log.d(TAG, "data: " + sb.toString());
                return true;
            } catch (IOException ex) {
                Log.d(TAG, ex.getMessage());
            }
        }
        return false;
    }

}
