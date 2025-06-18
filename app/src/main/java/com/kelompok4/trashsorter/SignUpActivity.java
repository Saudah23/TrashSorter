package com.kelompok4.trashsorter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class SignUpActivity extends AppCompatActivity {

    private EditText etNama, etAlamat, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private TextView tvLoginHere;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        etNama = findViewById(R.id.etNama);
        etAlamat = findViewById(R.id.etAlamat);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLoginHere = findViewById(R.id.tvLoginHere);

        btnSignUp.setOnClickListener(v -> registerUser());
        tvLoginHere.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String nama = etNama.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (nama.isEmpty() || alamat.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            // Simpan data ke Firestore
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("uid", user.getUid());
                            userMap.put("nama", nama);
                            userMap.put("alamat", alamat);
                            userMap.put("email", email);

                            firestore.collection("users")
                                    .document(user.getUid())
                                    .set(userMap)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();

                                        // Kirim token ke ESP32
                                        user.getIdToken(true).addOnCompleteListener(tokenTask -> {
                                            if (tokenTask.isSuccessful()) {
                                                String idToken = tokenTask.getResult().getToken();
                                                sendTokenToESP32(idToken);
                                            }
                                            // Langsung ke dashboard
                                            startActivity(new Intent(this, DashboardActivity.class));
                                            finish();
                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Gagal menyimpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Registrasi gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendTokenToESP32(String token) {
        AsyncTask.execute(() -> {
            try {
                URL url = new URL("http://192.168.1.100/token"); // Ganti IP ESP32 kamu
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonBody = "{\"idToken\":\"" + token + "\"}";

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
