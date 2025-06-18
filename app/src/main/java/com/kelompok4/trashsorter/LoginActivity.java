package com.kelompok4.trashsorter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private CheckBox rememberMeCheckBox;
    private Button loginButton, googleLoginButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.etEmail);
        passwordEditText = findViewById(R.id.etPassword);
        rememberMeCheckBox = findViewById(R.id.cbRemember);
        loginButton = findViewById(R.id.btnLogin);
        googleLoginButton = findViewById(R.id.btnGoogleLogin);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(view -> loginUser());
        googleLoginButton.setOnClickListener(view -> signInWithGoogle());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();
                    user.getIdToken(true).addOnCompleteListener(tokenTask -> {
                        if (tokenTask.isSuccessful()) {
                            String idToken = tokenTask.getResult().getToken();
                            sendTokenToESP32(uid, idToken);

                            // (Opsional) Simpan token ke Firestore untuk debugging
                            FirebaseFirestore.getInstance().collection("users")
                                    .document(uid)
                                    .update("idToken", idToken);
                        }
                        startActivity(new Intent(this, DashboardActivity.class));
                        finish();
                    });
                }
            } else {
                Toast.makeText(this, "Login gagal: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void signInWithGoogle() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            user.getIdToken(true).addOnCompleteListener(tokenTask -> {
                if (tokenTask.isSuccessful()) {
                    String idToken = tokenTask.getResult().getToken();
                    sendTokenToESP32(uid, idToken);

                    // (Opsional) Simpan token ke Firestore untuk debugging
                    FirebaseFirestore.getInstance().collection("users")
                            .document(uid)
                            .update("idToken", idToken);
                }
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
            });
        }
    }

    private void sendTokenToESP32(String uid, String token) {
        AsyncTask.execute(() -> {
            try {
                URL url = new URL("http://192.168.1.100/token"); // Ganti IP sesuai ESP32 kamu
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonBody = "{\"uid\":\"" + uid + "\", \"idToken\":\"" + token + "\"}";

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonBody.getBytes());
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                Log.d("TokenSend", "Response code from ESP32: " + responseCode);
            } catch (Exception e) {
                Log.e("TokenSend", "Error sending token to ESP32", e);
            }
        });
    }
}
