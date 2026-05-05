package com.example.foodcourtgo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class TenantAddMenuActivity extends AppCompatActivity {
    EditText etName, etDeskripsi, etPrice, etGambar;
    Button btnSimpan;
    String tenantId;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_add_menu);
        tenantId = getSharedPreferences("FoodCourtGoPrefs", MODE_PRIVATE).getString("tenantId", "");

        etName = findViewById(R.id.et_menu_name);
        etDeskripsi = findViewById(R.id.et_menu_price); // perhatikan: di layout et_menu_price?
        // Di layout, EditText harga ada di dalam row_menu_price dengan id et_menu_price
        etPrice = findViewById(R.id.et_menu_price);
        etGambar = findViewById(R.id.et_menu_deskripsi); // sebenarnya tidak ada gambar? di layout ada upload area, tapi kita skip dulu
        btnSimpan = findViewById(R.id.btn_save_menu);

        btnSimpan.setOnClickListener(v -> simpanMenu());
        findViewById(R.id.btn_back_add_menu).setOnClickListener(v -> finish());
    }

    private void simpanMenu() {
        String nama = etName.getText().toString().trim();
        String deskripsi = ""; // dari mana?
        String hargaStr = etPrice.getText().toString().trim();
        if (nama.isEmpty() || hargaStr.isEmpty()) {
            Toast.makeText(this, "Nama dan harga wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }
        long harga = Long.parseLong(hargaStr);
        String menuId = tenantId + "_M" + System.currentTimeMillis() % 10000; // sederhana
        MenuModel menu = new MenuModel();
        menu.setMenuId(menuId);
        menu.setNama(nama);
        menu.setDeskripsi(deskripsi);
        menu.setHarga(harga);
        menu.setTenantId(tenantId);
        menu.setTambahan(new ArrayList<>());

        FirebaseDatabase.getInstance().getReference("menu").child(menuId)
                .setValue(menu)
                .addOnSuccessListener(u -> {
                    Toast.makeText(this, "Menu berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}