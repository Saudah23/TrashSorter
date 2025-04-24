package com.example.trashsorter2.model;

public class NotificationItem {
    private String waktu;
    private String status;
    private String volume;

    // Constructor kosong diperlukan oleh Firebase
    public NotificationItem() {
    }

    public NotificationItem(String waktu, String status, String volume) {
        this.waktu = waktu;
        this.status = status;
        this.volume = volume;
    }

    public String getWaktu() {
        return waktu;
    }

    public String getStatus() {
        return status;
    }

    public String getVolume() {
        return volume;
    }
}
