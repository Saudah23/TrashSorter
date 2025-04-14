package com.example.trashsorter2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class DashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView icMenu, icNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        drawerLayout = findViewById(R.id.drawerLayout);
        icMenu = findViewById(R.id.icMenu);
        icNotifications = findViewById(R.id.icNotifications);
        NavigationView navigationView = findViewById(R.id.navigationView);

        // Buka navigation drawer saat ikon menu diklik
        icMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.END); // Posisi kanan
            }
        });

        // Klik notifikasi
        icNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashboardActivity.this, "Notifikasi diklik", Toast.LENGTH_SHORT).show();
                // Tambahkan intent jika ingin ke halaman Notifikasi
            }
        });

        // Klik item navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_notifications) {
                    Toast.makeText(DashboardActivity.this, "Notifikasi diklik", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_control) {
                    Toast.makeText(DashboardActivity.this, "Kontrol diklik", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_language) {
                    Toast.makeText(DashboardActivity.this, "Bahasa diklik", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_logout) {
                    Toast.makeText(DashboardActivity.this, "Logout berhasil", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            }
        });
    }
}
