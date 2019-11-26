package com.nfa.android.fragments;

import android.content.Context;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nfa.android.HomeActivity;
import com.nfa.android.R;
import com.nfa.android.listeners.NFCListener;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * A simple {@link Fragment} subclass.
 */
public class NFCReadFragment extends DialogFragment {

    public static final String TAG = NFCReadFragment.class.getSimpleName();
    private TextView read_message;
    private NFCListener listener;

    public static NFCReadFragment newInstance() {
        return new NFCReadFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read, container, false);

        read_message = view.findViewById(R.id.read_message);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (HomeActivity) context;
        listener.onDialogDisplayed();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener.onDialogDismissed();
    }

    public void onNfcDetected(MifareUltralight tag) {
        readFromNFC(tag);
    }

    private void readFromNFC(MifareUltralight tag) {
        try {
            StringBuilder sb = new StringBuilder();
            tag.connect();
            int i = 4;
            while (i < 21) {
                byte[] data = tag.readPages(i);
                String v = new String(data, Charset.forName("UTF-8"));
                sb.append(v);
                i += 4;
            }
            read_message.setText(sb.toString());
            Log.d(TAG, "data: " + sb.toString());
            tag.close();
        } catch (IOException ex) {
            Log.d(TAG, ex.getMessage());
        }
    }
}
