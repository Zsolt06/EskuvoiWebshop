package com.example.webshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegistrationActivity.class.getName();
    private static final String PREFS_KEY = MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 66;

    EditText userNameET;
    EditText userEmailET;
    EditText passwordET;
    EditText passwordConfirmET;
    EditText phoneET;
    EditText addressET;
    RadioGroup accountTypeRG;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

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
        phoneET = findViewById(R.id.editTextPhone);
        addressET = findViewById(R.id.editTextAddress);
        accountTypeRG = findViewById(R.id.radioGroupAccountType);
        accountTypeRG.check(R.id.buyerRadioButton);

        preferences = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        String email = preferences.getString("email", "");
        userEmailET.setText(email);

        mAuth = FirebaseAuth.getInstance();

    }

    public void register(View view) {
        String username = userNameET.getText().toString();
        String email = userEmailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordConfirm = passwordConfirmET.getText().toString();
        String phone = phoneET.getText().toString();
        String address = addressET.getText().toString();

        int checkedID = accountTypeRG.getCheckedRadioButtonId();
        RadioButton radioButton = accountTypeRG.findViewById(checkedID);
        String accountType = radioButton.getText().toString();

        if (!password.equals(passwordConfirm)) {
            Log.e(LOG_TAG, "A két jelszó nem egyezik meg!");
            return;
        }

        Log.i(LOG_TAG, "Regisztrált: " + username + " Email: " + email + " Jelszó: " + password + " Telefon: " + phone + " Cím: " + address + " AccountType: " + accountType);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "Sikeres regisztráció!");
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    if (currentUser != null) {
                        saveUserDataToFirestore(currentUser.getUid(), username, email, phone, address, accountType);
                    }

                    startShopping();
                } else {
                    Log.e(LOG_TAG, "Sikertelen regisztráció!");
                    Toast.makeText(RegistrationActivity.this, "Sikertelen regisztráció: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Create
    private void saveUserDataToFirestore(String userId, String username, String email, String phone, String address, String accountType) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(userId);

        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("phone", phone);
        userData.put("address", address);
        userData.put("accountType", accountType);

        userRef.set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(LOG_TAG, "Felhasználói adatok hozzáadva a Firestore-hoz!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(LOG_TAG, "Hiba a felhasználói adatok hozzáadásával a Firestore-hoz: " + e.getMessage());
                    }
                });
    }

    private void startShopping() {
        Intent intent = new Intent(this, WebshopListActivity.class);
        startActivity(intent);
    }
}