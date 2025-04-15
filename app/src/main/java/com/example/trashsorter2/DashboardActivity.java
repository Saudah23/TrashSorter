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

        icMenu.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.END));

        icNotifications.setOnClickListener(view -> openNotificationPage());

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_notifications) {
                openNotificationPage();

            } else if (id == R.id.nav_control) {
                startActivity(new Intent(DashboardActivity.this, ControlActivity.class));

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
        });
    }

    private void openNotificationPage() {
        Intent intent = new Intent(DashboardActivity.this, NotificationActivity.class);
        startActivity(intent);
    }
}
