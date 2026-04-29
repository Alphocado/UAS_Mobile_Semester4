package com.example.foodcourtgo;

import java.util.List;

public class MenuModel {
    private String menuId;     // ID dari node (misal T0001_M01)
    private String nama;
    private String deskripsi;
    private long harga;        // Firebase simpan angka → long
    private String gambar;
    private String tenantId;
    private List<TambahanModel> tambahan;


    // Constructor kosong wajib
    public MenuModel() {}

    // Getters
    public String getMenuId() { return menuId; }
    public String getNama() { return nama; }
    public String getDeskripsi() { return deskripsi; }
    public long getHarga() { return harga; }
    public String getGambar() { return gambar; }
    public String getTenantId() { return tenantId; }
    public List<TambahanModel> getTambahan() { return tambahan; }


    // Setters
    public void setTambahan(List<TambahanModel> tambahan) { this.tambahan = tambahan; }

    public void setMenuId(String menuId) { this.menuId = menuId; }
    public void setNama(String nama) { this.nama = nama; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public void setHarga(long harga) { this.harga = harga; }
    public void setGambar(String gambar) { this.gambar = gambar; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    // Format harga ke Rupiah
    public String getHargaFormatted() {
        return "Rp" + String.format("%,d", harga).replace(',', '.');
    }
}