package com.kelompok4.trashsorter.model;

public class NotificationItem {
    private String waktu;
    private String status;
    private String volume;

    public NotificationItem() {
        // Diperlukan untuk Firebase
    }

    public NotificationItem(String waktu, String status, String volume) {
        this.waktu = waktu;
        this.status = status;
        this.volume = volume;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}
