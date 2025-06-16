package com.kelompok4.trashsorter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kelompok4.trashsorter.model.NotificationItem;

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

        holder.textWaktu.setText(item.getWaktu());
        holder.textJenisSampah.setText("Jenis Sampah: " + item.getStatus());
        holder.textVolume.setText("Volume: " + item.getVolume());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView textWaktu, textJenisSampah, textVolume;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            textWaktu = itemView.findViewById(R.id.textWaktu);
            textJenisSampah = itemView.findViewById(R.id.textJenisSampah);
            textVolume = itemView.findViewById(R.id.textVolume);
        }
    }
}
