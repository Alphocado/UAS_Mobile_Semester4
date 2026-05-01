package com.example.foodcourtgo;

public class TenantModel {
    private String id;
    private String nama;
    private String deskripsi;
    private String kategori;
    private String gambar;
    private String status;   // "active" / "inactive"

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Constructor kosong wajib ada untuk Firebase
    public TenantModel() {}

    public TenantModel(String id, String nama, String deskripsi, String kategori, String gambar) {
        this.id = id;
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.gambar = gambar;
    }

    // Getters
    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getDeskripsi() { return deskripsi; }
    public String getKategori() { return kategori; }
    public String getGambar() { return gambar; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setGambar(String gambar) { this.gambar = gambar; }
}