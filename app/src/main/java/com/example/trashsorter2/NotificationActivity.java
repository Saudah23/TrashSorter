package com.example.trashsorter2;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trashsorter2.model.NotificationItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<NotificationItem> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.notificationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(notificationAdapter);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        loadNotificationsFromFirebase();
    }

    private void loadNotificationsFromFirebase() {
        // Menambahkan URL Firebase yang benar
        DatabaseReference ref = FirebaseDatabase.getInstance("https://trashsorter-1f379-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Sensor/SampahOrganik");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("FirebaseDebug", "Snapshot: " + snapshot.toString());

                notificationList.clear(); // Kosongkan list sebelumnya

                if (snapshot.exists()) {
                    // Iterasi setiap child untuk logging key dan value
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Log.d("FirebaseDebug", "Key: " + child.getKey() + ", Value: " + child.getValue());
                    }

                    // Ambil nilai dari snapshot
                    String status = snapshot.child("status").getValue(String.class);
                    String waktu = snapshot.child("waktu").getValue(String.class);
                    String volume = snapshot.child("volume_sampah").getValue(String.class);

                    Log.d("FirebaseDebug", "Status: " + status + ", Waktu: " + waktu + ", Volume: " + volume);

                    // Cek apakah semua field ada dan tidak null
                    if (status != null && waktu != null && volume != null) {
                        // Tambahkan item notifikasi jika data valid
                        notificationList.add(new NotificationItem(waktu, status, volume));
                    } else {
                        Log.d("FirebaseDebug", "Ada field yang null");
                    }
                } else {
                    Log.d("FirebaseDebug", "Snapshot TIDAK ADA");
                }

                // Memberitahu adapter bahwa data sudah berubah
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Database error: " + error.getMessage());
            }
        });
    }

}
