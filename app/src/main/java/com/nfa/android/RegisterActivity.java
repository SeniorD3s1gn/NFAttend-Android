package com.nfa.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nfa.android.utils.ConnectionManager;
import com.nfa.android.utils.StringValidator;

public class RegisterActivity extends AppCompatActivity {

    private static final String api = "https://nfattend.firebaseapp.com/api/users/";

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

        RegisterFname = findViewById(R.id.RegisterFname);
        RegisterLname = findViewById(R.id.RegisterLname);
        RegisterID = findViewById(R.id.RegisterID);
        RegisterEmail = findViewById(R.id.RegisterEmail);
        RegisterPassword = findViewById(R.id.RegisterPassword);
        Register = findViewById(R.id.Register);
        Register.setEnabled(false);
        RegisterFname.addTextChangedListener(new StringValidator(RegisterFname) {
            @Override
            public void validate(TextView view, String s) {
                Register.setEnabled(validateAll());
            }
        });
        RegisterLname.addTextChangedListener(new StringValidator(RegisterLname) {
            @Override
            public void validate(TextView view, String s) {
                Register.setEnabled(validateAll());
            }
        });
        RegisterID.addTextChangedListener(new StringValidator(RegisterID) {
            @Override
            public void validate(TextView view, String s) {
                if (view.getText().length() != 6) {
                    view.setError("Student id must be 6 digits");
                }
                Register.setEnabled(validateAll());
            }
        });
        RegisterEmail.addTextChangedListener(new StringValidator(RegisterEmail) {
            @Override
            public void validate(TextView view, String s) {
                if (Patterns.EMAIL_ADDRESS.matcher(view.getText()).matches()) {
                    view.setError("Email address format is invalid");
                }
                Register.setEnabled(validateAll());
            }
        });
        RegisterPassword.addTextChangedListener(new StringValidator(RegisterPassword) {
            @Override
            public void validate(TextView view, String s) {
                if (view.getText().length() < 7) {
                    RegisterPassword.setError("Password must be longer than 6 characters");
                }
                Register.setEnabled(validateAll());
            }
        });
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAll()) {
                    ConnectionManager manager = new ConnectionManager(RegisterActivity.this, api);
                    manager.register(RegisterFname.getText().toString(),
                            RegisterLname.getText().toString(), RegisterEmail.getText().toString(),
                            RegisterID.getText().toString(), RegisterPassword.getText().toString());
                }
            }
        });
    }

    private boolean validateAll() {
        return !TextUtils.isEmpty(RegisterFname.getText()) &&
                !TextUtils.isEmpty(RegisterLname.getText()) &&
                RegisterID.getText().length() == 6 &&
                !Patterns.EMAIL_ADDRESS.matcher(RegisterEmail.getText()).matches() &&
                !TextUtils.isEmpty(RegisterPassword.getText());
    }
}