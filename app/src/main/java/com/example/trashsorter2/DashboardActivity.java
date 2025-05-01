package com.example.trashsorter2;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.content.Intent;
import android.view.View;
import android.app.Activity;

public class DashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView icMenu, icNotifications;
    private ProgressBar progressMetal, progressNonMetal;
    private TextView tvMetalPercent, tvNonMetalPercent;
    private LineChart lineChart;

    private final int maxVolume = 100;
    private final ArrayList<Entry> entriesMetal = new ArrayList<>();
    private final ArrayList<Entry> entriesNonMetal = new ArrayList<>();
    private long lastSavedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        drawerLayout = findViewById(R.id.drawerLayout);
        icMenu = findViewById(R.id.icMenu);
        icNotifications = findViewById(R.id.icNotifications);
        NavigationView navigationView = findViewById(R.id.navigationView);

        lineChart = findViewById(R.id.lineChart);
        progressMetal = findViewById(R.id.progressMetal);
        progressNonMetal = findViewById(R.id.progressNonMetal);
        tvMetalPercent = findViewById(R.id.tvMetalPercent);
        tvNonMetalPercent = findViewById(R.id.tvNonMetalPercent);

        setupFirebaseData();
        setupNavigation(navigationView);

        icMenu.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.END));
        icNotifications.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        View headerView = navigationView.getHeaderView(0);
        Button btnProfile = headerView.findViewById(R.id.btnProfile);
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupNavigation(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.btnProfile) {
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
            } else if (id == R.id.nav_notifications) {
                startActivity(new Intent(DashboardActivity.this, NotificationActivity.class));
            } else if (id == R.id.nav_logout) {
                Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    private void setupLineChart() {
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
    }

    private void setupFirebaseData() {
        DatabaseReference sensorRef = FirebaseDatabase.getInstance("https://trashsorter-1f379-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Sensor");

        sensorRef.child("Metal").child("volumePersenMetal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Float metalVolume = parseVolume(snapshot.getValue());
                    if (metalVolume != null) {
                        int percentMetal = (int) ((metalVolume / (float) maxVolume) * 100);
                        progressMetal.setProgress(percentMetal);
                        tvMetalPercent.setText(String.format(Locale.getDefault(), "%.0f %%", metalVolume));

                        sensorRef.child("NonMetal").child("volumePersenNonmetal").get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Float nonMetalVolume = parseVolume(task.getResult().getValue());

                                long currentTime = System.currentTimeMillis();
                                if (currentTime - lastSavedTime > 60 * 1000) {
                                    saveToRiwayat(metalVolume, nonMetalVolume);
                                    lastSavedTime = currentTime;
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });

        sensorRef.child("NonMetal").child("volumePersenNonmetal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Float nonMetalVolume = parseVolume(snapshot.getValue());
                    if (nonMetalVolume != null) {
                        int percentNonMetal = (int) ((nonMetalVolume / (float) maxVolume) * 100);
                        progressNonMetal.setProgress(percentNonMetal);
                        tvNonMetalPercent.setText(String.format(Locale.getDefault(), "%.0f %%", nonMetalVolume));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });

        sensorRef.child("Riwayat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                entriesMetal.clear();
                entriesNonMetal.clear();
                int index = 0;

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Float metalVolume = parseVolume(snap.child("Metal").getValue());
                    Float nonMetalVolume = parseVolume(snap.child("NonMetal").getValue());

                    if (metalVolume != null && nonMetalVolume != null) {
                        entriesMetal.add(new Entry(index, metalVolume));
                        entriesNonMetal.add(new Entry(index, nonMetalVolume));
                        index++;
                    }
                }

                LineDataSet dataSetMetal = new LineDataSet(entriesMetal, "Metal");
                dataSetMetal.setColor(Color.BLUE);
                dataSetMetal.setLineWidth(2f);

                LineDataSet dataSetNonMetal = new LineDataSet(entriesNonMetal, "Non-Metal");
                dataSetNonMetal.setColor(Color.GREEN);
                dataSetNonMetal.setLineWidth(2f);

                LineData lineData = new LineData(dataSetMetal, dataSetNonMetal);
                lineChart.setData(lineData);
                lineChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DashboardActivity", "LineChart load error: " + error.getMessage());
            }
        });
    }

    private void saveToRiwayat(float metalVolume, float nonMetalVolume) {
        DatabaseReference riwayatRef = FirebaseDatabase.getInstance()
                .getReference("Sensor").child("Riwayat");

        String timeKey = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        riwayatRef.child(timeKey).child("Metal").setValue(metalVolume);
        riwayatRef.child(timeKey).child("NonMetal").setValue(nonMetalVolume);
    }

    private Float parseVolume(Object value) {
        try {
            if (value == null) return 0f;
            if (value instanceof Number) return ((Number) value).floatValue();
            return Float.parseFloat(value.toString());
        } catch (Exception e) {
            Log.e("ParseError", "Gagal parsing volume: " + e.getMessage());
            return 0f;
        }
    }
}
