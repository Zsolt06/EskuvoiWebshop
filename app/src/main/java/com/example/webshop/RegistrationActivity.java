package com.example.webshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class RegistrationActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegistrationActivity.class.getName();
    private static final String PREFS_KEY = MainActivity.class.getPackage().toString();

    EditText userNameET;
    EditText userEmailET;
    EditText passwordET;
    EditText passwordConfirmET;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != 66) {
            finish();
        }

        userNameET = findViewById(R.id.editTextUserName);
        userEmailET = findViewById(R.id.editTextEmail);
        passwordET = findViewById(R.id.editTextPassword);
        passwordConfirmET = findViewById(R.id.editTextPasswordAgain);

        preferences = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        String userName = preferences.getString("userName", "");

        userNameET.setText(userName);
    }

    public void register(View view) {
        String username = userNameET.getText().toString();
        String email = userEmailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordConfirm = passwordConfirmET.getText().toString();

        if (!password.equals(passwordConfirm)) {
            Log.e(LOG_TAG, "A két jelszó nem egyezik meg!");
            return;
        }

        Log.i(LOG_TAG, "Regisztrált: " + username + " Email: " + email + " Jelszó: " + password);
    }
}