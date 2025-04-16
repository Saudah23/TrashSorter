package com.example.trashsorter2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private CheckBox cbRemember;
    private FirebaseAuth mAuth;

    private static final String PREFS_NAME = "secure_login_prefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";

    private SharedPreferences securePrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        cbRemember = findViewById(R.id.cbRemember);

        mAuth = FirebaseAuth.getInstance();

        // Inisialisasi EncryptedSharedPreferences
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            securePrefs = EncryptedSharedPreferences.create(
                    PREFS_NAME,
                    masterKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal memuat keamanan preferensi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Load saved credentials if "remember me" is checked
        boolean isRemembered = securePrefs.getBoolean(KEY_REMEMBER, false);
        if (isRemembered) {
            etEmail.setText(securePrefs.getString(KEY_EMAIL, ""));
            etPassword.setText(securePrefs.getString(KEY_PASSWORD, ""));
            cbRemember.setChecked(true);
        }

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email tidak boleh kosong");
                etEmail.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password tidak boleh kosong");
                etPassword.requestFocus();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (cbRemember.isChecked()) {
                                SharedPreferences.Editor editor = securePrefs.edit();
                                editor.putString(KEY_EMAIL, email);
                                editor.putString(KEY_PASSWORD, password);
                                editor.putBoolean(KEY_REMEMBER, true);
                                editor.apply();
                            } else {
                                securePrefs.edit().clear().apply();
                            }

                            Toast.makeText(LoginActivity.this, "Login berhasil", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            Exception e = task.getException();
                            if (e instanceof FirebaseAuthInvalidUserException) {
                                etEmail.setError("Email tidak terdaftar");
                                etEmail.requestFocus();
                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                etPassword.setError("Password salah");
                                etPassword.requestFocus();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login gagal: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });
    }
}
