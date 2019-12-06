package com.nfa.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nfa.android.listeners.ConnectionListener;
import com.nfa.android.utils.ConnectionManager;
import com.nfa.android.utils.StringValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements ConnectionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String api = "https://nfattend.firebaseapp.com/api/auth/";
    private static final boolean DEBUG = true;

    private EditText Email;
    private EditText Password;
    private TextView Forgot;
    private TextView Create;
    private Button Login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (DEBUG) {
            login("030587");
        }

        Email = findViewById(R.id.MainEmail);
        Password = findViewById(R.id.MainPassword);
        Forgot = findViewById(R.id.MainForgot);
        Create = findViewById(R.id.MainCreate);
        Login = findViewById(R.id.MainLogin);
        Login.setEnabled(false);
        Email.addTextChangedListener(new StringValidator(Email) {
            @Override
            public void validate(TextView view, String s) {
                if (!Patterns.EMAIL_ADDRESS.matcher(view.getText()).matches()) {
                    Email.setError("Email address format is invalid");
                }
                Login.setEnabled(validateAll());
            }
        });
        Password.addTextChangedListener(new StringValidator(Password) {
            @Override
            public void validate(TextView view, String s) {
                if (view.getText().length() < 6) {
                    Password.setError("Password must be longer than 6 characters");
                }
                Login.setEnabled(validateAll());
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAll()) {
                     ConnectionManager manager = new ConnectionManager(api, MainActivity.this);
                     manager.login(Email.getText().toString(), Password.getText().toString());
                }
            }
        });
        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    private boolean validateAll() {
        return !TextUtils.isEmpty(Email.getText()) &&
                !TextUtils.isEmpty(Password.getText()) &&
                Patterns.EMAIL_ADDRESS.matcher(Email.getText()).matches() &&
                !(Password.getText().length() < 6);
    }

    private void login(String id) {
        Log.d(TAG, "Called login to home");
        Intent i = new Intent(this, HomeActivity.class);
        i.putExtra("STUDENT_ID", id);
        startActivity(i);
    }

    @Override
    public void onConnectionFinish(String eventType, JSONArray array) {

    }

    @Override
    public void onConnectionFinish(String eventType, JSONObject object) {
        if (eventType.equals("Login")) {
            if (object.has("id")) {
                try {
                    String id = object.getString("id");
                    login(id);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


}
