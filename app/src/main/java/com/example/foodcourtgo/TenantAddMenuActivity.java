package com.example.foodcourtgo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.UUID;

public class TenantAddMenuActivity extends AppCompatActivity {
    EditText etName, etPrice;
    Button btnSimpan;
    String tenantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_add_menu);

        tenantId = getSharedPreferences("FoodCourtGoPrefs", MODE_PRIVATE).getString("tenantId", "");

        etName = findViewById(R.id.et_menu_name);
        etPrice = findViewById(R.id.et_menu_price);
        btnSimpan = findViewById(R.id.btn_save_menu);

        btnSimpan.setOnClickListener(v -> simpanMenu());
        findViewById(R.id.btn_back_add_menu).setOnClickListener(v -> finish());
    }

    private void simpanMenu() {
        String nama = etName.getText().toString().trim();
        String hargaStr = etPrice.getText().toString().trim();

        if (nama.isEmpty() || hargaStr.isEmpty()) {
            Toast.makeText(this, "Nama dan harga harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        long harga;
        try {
            harga = Long.parseLong(hargaStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Harga tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        String menuId = tenantId + "_M" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        MenuModel menu = new MenuModel();
        menu.setMenuId(menuId);
        menu.setNama(nama);
        menu.setDeskripsi(""); // bisa ditambahkan EditText deskripsi nanti
        menu.setHarga(harga);
        menu.setTenantId(tenantId);
        menu.setTambahan(new ArrayList<>());

        FirebaseDatabase.getInstance().getReference("menu").child(menuId)
                .setValue(menu)
                .addOnSuccessListener(u -> {
                    Toast.makeText(this, "Menu berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal menyimpan", Toast.LENGTH_SHORT).show());
    }
}