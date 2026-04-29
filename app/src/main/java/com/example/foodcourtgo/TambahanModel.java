package com.example.foodcourtgo;

public class TambahanModel {
    private String nama;
    private long harga;   // 0 jika gratis

    public TambahanModel() {}

    public TambahanModel(String nama, long harga) {
        this.nama = nama;
        this.harga = harga;
    }

    public String getNama() { return nama; }
    public long getHarga() { return harga; }

    public void setNama(String nama) { this.nama = nama; }
    public void setHarga(long harga) { this.harga = harga; }
}