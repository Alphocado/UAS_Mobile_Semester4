package com.example.foodcourtgo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class QrScannerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);
        Button btnSelesai = findViewById(R.id.btnSelesai);

        // Terima data tenant
        Intent source = getIntent();
        String tenantId = source.getStringExtra("tenantId");
        String tenantNama = source.getStringExtra("tenantNama");
        String tenantGambar = source.getStringExtra("tenantGambar");
        String tenantKategori = source.getStringExtra("tenantKategori");
        String tenantDeskripsi = source.getStringExtra("tenantDeskripsi");

        btnSelesai.setOnClickListener(v -> {
            Intent detailIntent = new Intent(QrScannerActivity.this, DetailTenantActivity.class);
            detailIntent.putExtra("tenantId", tenantId);
            detailIntent.putExtra("tenantNama", tenantNama);
            detailIntent.putExtra("tenantGambar", tenantGambar);
            detailIntent.putExtra("tenantKategori", tenantKategori);
            detailIntent.putExtra("tenantDeskripsi", tenantDeskripsi);
            startActivity(detailIntent);
            finish();   // agar tidak kembali ke QrScanner
        });
    }
}