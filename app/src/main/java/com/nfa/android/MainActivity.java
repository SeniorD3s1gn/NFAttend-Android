package com.nfa.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView Email;
    private TextView Password;
    private TextView Forgot;
    private TextView Create;
    private Button Login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Email = findViewById(R.id.MainEmail);
        Password = findViewById(R.id.MainPassword);
        Forgot = findViewById(R.id.MainForgot);
        Create = findViewById(R.id.MainCreate);
        Login= findViewById(R.id.MainLogin);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( validate()){
                    Log.d("MainAct" ,"valid login");
                }
                else{
                    Log.d("MainAct", "Invalid Login");
                }
            }
        });
    }

    private boolean validate() {
        return!(Email.getText().toString().matches("")) && !(Password.getText().toString().matches(""));
    }

}
