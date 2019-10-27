package com.nfa.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {
    private TextView RegisterFname;
    private TextView RegisterLname;
    private TextView RegisterID;
    private TextView RegisterEmail;
    private TextView RegisterPassword;
    private Button Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        RegisterFname = findViewById(R.id.MainEmail);
        RegisterLname = findViewById(R.id.MainPassword);
        RegisterID = findViewById(R.id.MainForgot);
        RegisterEmail = findViewById(R.id.MainCreate);
        RegisterPassword = findViewById(R.id.MainLogin);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    Log.d("RegAct", "valid login");
                } else {
                    Log.d("RegAct", "Invalid Login");
                }
            }
        });
    }

    private boolean validate() {
        return !(RegisterEmail.getText().toString().matches("")) && !(RegisterPassword.getText().toString().matches(""));
    }
}