package com.example.foodcourtgo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private ImageView ivBack;
    private LinearLayout llPesananList;
    private TextView tvTotalBayar;
    private Button btnBayar;

    private List<PesananItem> pesananList;
    private long totalHarga;
    private String tenantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ivBack = findViewById(R.id.ivBack);
        llPesananList = findViewById(R.id.llPesananList);
        tvTotalBayar = findViewById(R.id.tvTotalBayar);
        btnBayar = findViewById(R.id.btnBayar);
        tenantId = getIntent().getStringExtra("tenantId");

        // Terima data pesanan (disimpan sebagai static sementara, atau dikirim via Intent)
        pesananList = PesananHolder.getPesananList();
        if (pesananList == null || pesananList.isEmpty()) {
            Toast.makeText(this, "Tidak ada pesanan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        totalHarga = 0;
        for (PesananItem item : pesananList) {
            totalHarga += item.getTotalHarga();

            // Buat view ringkasan per item
            View itemView = getLayoutInflater().inflate(R.layout.item_pesanan, llPesananList, false);
            TextView tvNama = itemView.findViewById(R.id.tvPesananNama);
            TextView tvOpsi = itemView.findViewById(R.id.tvPesananOpsi);
            TextView tvHarga = itemView.findViewById(R.id.tvPesananHarga);

            tvNama.setText(item.getNama());
            if (item.getOpsi() != null && !item.getOpsi().isEmpty()) {
                tvOpsi.setText(item.getOpsi());
            } else {
                tvOpsi.setVisibility(View.GONE);
            }
            tvHarga.setText("Rp" + String.format("%,d", item.getTotalHarga()).replace(',', '.'));
            llPesananList.addView(itemView);
        }

        tvTotalBayar.setText("Rp" + String.format("%,d", totalHarga).replace(',', '.'));

        ivBack.setOnClickListener(v -> finish());

        btnBayar.setOnClickListener(v -> {
            new AlertDialog.Builder(PaymentActivity.this)
                    .setTitle("Konfirmasi Pembayaran")
                    .setMessage("Rp" + String.format("%,d", totalHarga).replace(',', '.') + "\n\nScan QR di atas dan klik Bayar untuk simulasi berhasil.")
                    .setPositiveButton("Bayar", (dialog, which) -> {
                        simpanPesananDanNotifikasi();  // <-- panggil method baru
                        Toast.makeText(PaymentActivity.this, "Pembayaran Berhasil!", Toast.LENGTH_SHORT).show();
                        PesananHolder.clear();
                        Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }
    private void simpanPesananDanNotifikasi() {
        if (pesananList == null || pesananList.isEmpty() || tenantId == null) return;

        // Generate ID pesanan
        String pesananId = "P" + System.currentTimeMillis();
        String customerId = getSharedPreferences("FoodCourtGoPrefs", MODE_PRIVATE)
                .getString("userId", "");
        String meja = "A-01";  // bisa diganti input

        // Buat objek pesanan
        PesananAdminModel pesanan = new PesananAdminModel();
        pesanan.setId(pesananId);
        pesanan.setTenantId(tenantId);
        pesanan.setCustomerId(customerId);
        pesanan.setMeja(meja);
        pesanan.setWaktu(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT).format(new java.util.Date()));
        pesanan.setStatus("pending");

        List<ItemPesananModel> items = new ArrayList<>();
        for (PesananItem item : pesananList) {
            ItemPesananModel i = new ItemPesananModel();
            i.setMenuId(item.getMenuId());
            i.setNama(item.getNama());
            i.setQty(1);
            i.setHarga(item.getHarga());
            i.setOpsi(item.getOpsi());
            i.setHargaTambahan(item.getHargaTambahan());
            items.add(i);
        }
        pesanan.setItems(items);
        pesanan.setTotalHarga(totalHarga);

        // Simpan ke Firebase - pesanan
        FirebaseDatabase.getInstance().getReference("pesanan")
                .child(pesananId).setValue(pesanan);

        // Buat notifikasi
        String notifId = tenantId + "_" + System.currentTimeMillis();
        NotificationModel notif = new NotificationModel();
        notif.setId(notifId);
        notif.setTenantId(tenantId);
        notif.setText("Pesanan baru " + pesananId + " dari Meja " + meja);
        notif.setWaktu(new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date()));
        notif.setStatus("unread");

        FirebaseDatabase.getInstance().getReference("notifications")
                .child(notifId).setValue(notif);
    }
}
