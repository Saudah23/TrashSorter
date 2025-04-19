package com.example.trashsorter2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

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

        // Menampilkan Chart
        LineChart lineChart = findViewById(R.id.lineChart);
        ArrayList<Entry> organik = new ArrayList<>();
        organik.add(new Entry(0, 20));
        organik.add(new Entry(1, 40));
        organik.add(new Entry(2, 60));
        organik.add(new Entry(3, 30));

        ArrayList<Entry> nonOrganik = new ArrayList<>();
        nonOrganik.add(new Entry(0, 15));
        nonOrganik.add(new Entry(1, 25));
        nonOrganik.add(new Entry(2, 50));
        nonOrganik.add(new Entry(3, 40));

        LineDataSet set1 = new LineDataSet(organik, "Organik");
        set1.setColor(Color.GREEN);
        set1.setCircleColor(Color.GREEN);

        LineDataSet set2 = new LineDataSet(nonOrganik, "Non-Organik");
        set2.setColor(Color.BLUE);
        set2.setCircleColor(Color.BLUE);

        LineData data = new LineData(set1, set2);
        lineChart.setData(data);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
        lineChart.getLegend().setForm(Legend.LegendForm.LINE);
        lineChart.animateX(1000);
        lineChart.invalidate();

        // Klik menu icon
        icMenu.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.END));

        // Klik notifikasi icon (header)
        icNotifications.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, NotificationActivity.class);
            startActivity(intent);
        });


        // Navigasi menu
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.btnProfile) {
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_notifications) {
                Intent intent = new Intent(DashboardActivity.this, NotificationActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_control) {
                Intent intent = new Intent(DashboardActivity.this, ControlActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });

        // Menambahkan listener untuk Edit Profile button di header
        View headerView = navigationView.getHeaderView(0);
        Button btnProfile = headerView.findViewById(R.id.btnProfile);
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }
    }
}
