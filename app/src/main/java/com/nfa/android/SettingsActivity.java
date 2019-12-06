package com.nfa.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends Fragment {



    List<String> comboValues = new ArrayList<String>();
    HomeActivity home;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);
        Switch notifSwitch = view.findViewById(R.id.notifSwitch);
        Spinner alertBox = view.findViewById(R.id.alertCombo);
        home = (HomeActivity) getActivity();
        int position = home.getAlertValue();

        notifSwitch.setChecked(home.getNSwitch());
        comboValues.add("None");
        comboValues.add("15 minutes");
        comboValues.add("30 minutes");
        comboValues.add("1 hour");

        ArrayAdapter<String>  adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, comboValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alertBox.setAdapter(adapter);
        alertBox.setSelection(position);

        alertBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("TAG", "itemclick");
               //String item = adapterView.getItemAtPosition(i).toString();
               home.savePref(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        notifSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                home.saveNSwitch(b);

            }
        });
        return view;
    }
}
