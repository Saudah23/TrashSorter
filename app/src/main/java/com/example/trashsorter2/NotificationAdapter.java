package com.example.trashsorter2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trashsorter2.model.NotificationItem;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final List<NotificationItem> notificationList;

    public NotificationAdapter(List<NotificationItem> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem item = notificationList.get(position);

        // Tampilkan waktu
        holder.textWaktu.setText(item.getWaktu());

        // Menampilkan volume tanpa "liter" dan tanpa unit apapun
        String volume = item.getVolume(); // Ambil volume tanpa menambahkan satuan
        String message = "Status: " + item.getStatus() + "\nVolume: " + volume; // Hanya volume yang ditampilkan
        holder.textPesan.setText(message);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView textWaktu, textPesan;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            textWaktu = itemView.findViewById(R.id.textWaktu);
            textPesan = itemView.findViewById(R.id.textPesan);
        }
    }
}
