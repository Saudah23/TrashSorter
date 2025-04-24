package com.example.trashsorter2;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trashsorter2.model.NotificationItem;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private final List<NotificationItem> notificationList = new ArrayList<>();
    private boolean isActivityActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.notificationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationAdapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(notificationAdapter);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        loadNotificationsFromFirebase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityActive = false;
    }

    private void loadNotificationsFromFirebase() {
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://trashsorter-1f379-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Sensor");

        ref.addValueEventListener(new ValueEventListener() { // gunakan real-time listener
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isActivityActive) return;

                notificationList.clear();
                String waktuSekarang = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

                Float volumeMetal = parseVolume(snapshot.child("Metal").child("volumePersenMetal").getValue());
                Float volumeNonMetal = parseVolume(snapshot.child("NonMetal").child("volumePersenNonmetal").getValue());

                if (volumeMetal != null && volumeMetal >= 50f) {
                    String pesan = (volumeMetal >= 100f) ? "Tempat Sampah METAL Penuh" : "Tempat Sampah METAL Hampir Penuh";
                    notificationList.add(new NotificationItem(waktuSekarang, pesan, String.format("%.0f%%", volumeMetal)));
                }

                if (volumeNonMetal != null && volumeNonMetal >= 50f) {
                    String pesan = (volumeNonMetal >= 100f) ? "Tempat Sampah NON-METAL Penuh" : "Tempat Sampah NON-METAL Hampir Penuh";
                    notificationList.add(new NotificationItem(waktuSekarang, pesan, String.format("%.0f%%", volumeNonMetal)));
                }

                if (notificationList.isEmpty()) {
                    Toast.makeText(NotificationActivity.this, "Tidak ada notifikasi saat ini", Toast.LENGTH_SHORT).show();
                    Log.d("Notification", "Tidak ada tempat sampah yang hampir penuh atau penuh.");
                }

                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isActivityActive) return;
                Log.e("FirebaseError", "Database error: " + error.getMessage());
                Toast.makeText(NotificationActivity.this, "Gagal mengambil data notifikasi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Float parseVolume(Object value) {
        try {
            if (value == null) return 0.0f;
            if (value instanceof Number) return ((Number) value).floatValue();
            return Float.parseFloat(value.toString());
        } catch (Exception e) {
            Log.e("ParseError", "Gagal parsing volume: " + e.getMessage());
            return 0.0f;
        }
    }
}
