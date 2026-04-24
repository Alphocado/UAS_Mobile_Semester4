package com.example.foodcourtgo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddTenantActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etCode;
    private Button btnAddFoodCourt;

    private String userId; // ID user yang sedang login, contoh: "A0001"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tenant);

        ivBack       = findViewById(R.id.ivBacks);
        etCode       = findViewById(R.id.etCode);
        btnAddFoodCourt = findViewById(R.id.btnAddFoodCourt);

        // Ambil userId dari SharedPreferences (disimpan saat login)
        SharedPreferences pref = getSharedPreferences("FoodCourtGoPrefs", MODE_PRIVATE);
        userId = pref.getString("userId", "");

        // Kalau userId kosong berarti belum login, paksa balik ke LoginActivity
        if (userId.isEmpty()) {
            Toast.makeText(this, "Sesi habis, silakan login ulang", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ivBack.setOnClickListener(v -> finish());

        btnAddFoodCourt.setOnClickListener(v -> {
            String kode = etCode.getText().toString().trim().toUpperCase(); // "t0001" → "T0001"

            if (kode.isEmpty()) {
                Toast.makeText(this, "Masukkan kode tenant terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            btnAddFoodCourt.setEnabled(false); // Cegah double klik
            cekDanSimpanTenant(kode);
        });
    }

    /**
     * Alur:
     * 1. Cek apakah kode ada di node "tenant/" di Firebase
     * 2. Kalau ada → cek apakah user sudah pernah simpan tenant ini
     * 3. Kalau belum → simpan ke "akun/{userId}/savedTenants/{kode}" = true
     * 4. Kalau sudah → beri tahu user sudah ditambahkan
     */
    private void cekDanSimpanTenant(String kode) {
        DatabaseReference tenantRef = FirebaseDatabase.getInstance()
                .getReference("tenant")
                .child(kode);

        tenantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // ❌ Kode tidak ditemukan di database tenant
                if (!snapshot.exists()) {
                    Toast.makeText(AddTenantActivity.this,
                            "Kode tenant tidak ditemukan", Toast.LENGTH_SHORT).show();
                    btnAddFoodCourt.setEnabled(true);
                    return;
                }

                // ✅ Kode valid — sekarang cek apakah user sudah simpan ini
                DatabaseReference savedRef = FirebaseDatabase.getInstance()
                        .getReference("akun")
                        .child(userId)
                        .child("savedTenants")
                        .child(kode);

                savedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot savedSnapshot) {

                        if (savedSnapshot.exists()) {
                            // User sudah pernah tambahkan tenant ini
                            Toast.makeText(AddTenantActivity.this,
                                    "Tenant ini sudah ada di daftar kamu", Toast.LENGTH_SHORT).show();
                            btnAddFoodCourt.setEnabled(true);
                        } else {
                            // Belum ada → simpan sekarang
                            // Struktur: akun/A0001/savedTenants/T0001 = true
                            savedRef.setValue(true)
                                    .addOnSuccessListener(unused -> {
                                        // Ambil nama tenant untuk ditampilkan di toast
                                        String namaTenant = "";
                                        if (snapshot.child("nama").getValue() != null) {
                                            namaTenant = snapshot.child("nama").getValue().toString();
                                        }

                                        Toast.makeText(AddTenantActivity.this,
                                                "\"" + namaTenant + "\" berhasil ditambahkan!",
                                                Toast.LENGTH_SHORT).show();

                                        finish(); // Kembali ke Home
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AddTenantActivity.this,
                                                "Gagal menyimpan: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                        btnAddFoodCourt.setEnabled(true);
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddTenantActivity.this,
                                "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        btnAddFoodCourt.setEnabled(true);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddTenantActivity.this,
                        "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                btnAddFoodCourt.setEnabled(true);
            }
        });
    }
}