package com.example.trashsorter2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

import com.github.mikephil.charting.components.MarkerView;
import com.example.trashsorter2.marker.MyMarkerView;

public class DashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView icMenu, icNotifications;
    private LineChart lineChart;
    private LineDataSet setMetal, setNonMetal;
    private LineData lineData;

    private final List<Entry> dataMetal = new ArrayList<>();
    private final List<Entry> dataNonMetal = new ArrayList<>();
    private final List<String> timeLabels = new ArrayList<>();
    private final Set<String> timeSet = new HashSet<>();

    private final SimpleDateFormat firebaseDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private final SimpleDateFormat displayTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private View barMetal, barNonMetal;
    private TextView labelMetal, labelNonMetal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        drawerLayout = findViewById(R.id.drawerLayout);
        icMenu = findViewById(R.id.icMenu);
        icNotifications = findViewById(R.id.icNotifications);
        lineChart = findViewById(R.id.lineChart);
        barMetal = findViewById(R.id.barMetal);
        barNonMetal = findViewById(R.id.barNonMetal);
        labelMetal = findViewById(R.id.labelMetal);
        labelNonMetal = findViewById(R.id.labelNonMetal);

        setupLineChart();

        MyMarkerView marker = new MyMarkerView(this, R.layout.custom_marker_view);
        marker.setChartView(lineChart);
        lineChart.setMarker(marker);


// Garis highlight saat titik disentuh
        lineChart.setDrawMarkers(true);

        setMetal.setDrawHighlightIndicators(true);
        setMetal.setHighLightColor(Color.GRAY);
        setMetal.setHighlightLineWidth(1f);

        setNonMetal.setDrawHighlightIndicators(true);
        setNonMetal.setHighLightColor(Color.GRAY);
        setNonMetal.setHighlightLineWidth(1f);



        listenToRealtimeData();

        icMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        icNotifications.setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.btnProfile) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.nav_notifications) {
                startActivity(new Intent(this, NotificationActivity.class));
            } else if (id == R.id.nav_logout) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    private void setupLineChart() {
        setMetal = new LineDataSet(dataMetal, "Metal");
        setMetal.setColor(Color.RED);
        setMetal.setCircleColor(Color.RED);
        setMetal.setLineWidth(2f);
        setMetal.setCircleRadius(4f);
        setMetal.setDrawCircles(true);
        setMetal.setDrawValues(true); // Tampilkan nilai volume di setiap titik
        setMetal.setValueTextSize(10f);
        setMetal.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Garis lebih halus

        setNonMetal = new LineDataSet(dataNonMetal, "Non-Metal");
        setNonMetal.setColor(Color.BLUE);
        setNonMetal.setCircleColor(Color.BLUE);
        setNonMetal.setLineWidth(2f);
        setNonMetal.setCircleRadius(4f);
        setNonMetal.setDrawCircles(true);
        setNonMetal.setDrawValues(true); // Tampilkan nilai volume di setiap titik
        setNonMetal.setValueTextSize(10f);
        setNonMetal.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        lineData = new LineData(setMetal, setNonMetal);
        lineChart.setData(lineData);

        lineChart.setHighlightPerTapEnabled(true);
        lineChart.setHighlightPerDragEnabled(true);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-30);
        xAxis.setTextSize(11f);
        xAxis.setYOffset(10f);
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return (index >= 0 && index < timeLabels.size()) ? timeLabels.get(index) : "";
            }
        });

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setLabelCount(6, true);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setTextSize(11f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.0f%%", value);
            }
        });

        lineChart.getAxisRight().setEnabled(false);

        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(13f);
        legend.setXEntrySpace(20f);
        legend.setFormSize(13f);

        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setNoDataText("Belum ada data...");
        lineChart.setMinOffset(20f);
        lineChart.animateX(500);
        lineChart.invalidate();
    }

    private void listenToRealtimeData() {
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://trashsorter-1f379-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Sensor");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String waktuString = snapshot.child("Metal").child("waktu").getValue(String.class);
                    Object metalVolumeObj = snapshot.child("Metal").child("volumePersenMetal").getValue();
                    Object nonMetalVolumeObj = snapshot.child("NonMetal").child("volumePersenNonmetal").getValue();

                    if (waktuString == null || metalVolumeObj == null || nonMetalVolumeObj == null) return;

                    Float metalVolume = parseVolume(metalVolumeObj);
                    Float nonMetalVolume = parseVolume(nonMetalVolumeObj);
                    if (metalVolume == null || nonMetalVolume == null) return;

                    Date waktu = firebaseDateFormat.parse(waktuString);
                    if (waktu == null) return;

                    String labelTime = displayTimeFormat.format(waktu);

                    runOnUiThread(() -> {
                        if (!timeSet.contains(waktuString)) {
                            int index = timeLabels.size();
                            timeLabels.add(labelTime);
                            timeSet.add(waktuString);

                            dataMetal.add(new Entry(index, metalVolume));
                            dataNonMetal.add(new Entry(index, nonMetalVolume));

                            if (dataMetal.size() > 10) {
                                dataMetal.remove(0);
                                dataNonMetal.remove(0);
                                timeLabels.remove(0);
                            }
                        } else {
                            int lastIndex = timeLabels.size() - 1;
                            if (lastIndex >= 0) {
                                dataMetal.set(lastIndex, new Entry(lastIndex, metalVolume));
                                dataNonMetal.set(lastIndex, new Entry(lastIndex, nonMetalVolume));
                            }
                        }

                        setMetal.notifyDataSetChanged();
                        setNonMetal.notifyDataSetChanged();
                        lineChart.setData(new LineData(setMetal, setNonMetal)); // refresh data
                        lineChart.notifyDataSetChanged();
                        lineChart.invalidate();

                        updateBarChart(metalVolume, nonMetalVolume);
                    });

                } catch (Exception e) {
                    Log.e("RealtimeUpdate", "Error", e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Realtime listener cancelled", error.toException());
            }
        });
    }

    private void updateBarChart(float metal, float nonMetal) {
        float total = metal + nonMetal;
        if (total == 0) total = 1;

        float metalWeight = metal / total;
        float nonMetalWeight = nonMetal / total;

        barMetal.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, metalWeight));
        barNonMetal.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, nonMetalWeight));

        labelMetal.setText(String.format(Locale.getDefault(), "%.0f %%", metal));
        labelNonMetal.setText(String.format(Locale.getDefault(), "%.0f %%", nonMetal));
    }

    private Float parseVolume(Object value) {
        try {
            if (value instanceof Number) return ((Number) value).floatValue();
            if (value instanceof String) return Float.parseFloat((String) value);
        } catch (Exception e) {
            Log.e("ParseVolume", "Failed to parse volume", e);
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
}
