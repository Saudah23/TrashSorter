package com.example.trashsorter2;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Pastikan layout ini ada dan benar

        ImageView backArrow = findViewById(R.id.btnBack);
        if (backArrow != null) {
            backArrow.setOnClickListener(view -> finish());
        }
    }
}
