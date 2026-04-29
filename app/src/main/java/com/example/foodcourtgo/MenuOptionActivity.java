package com.example.foodcourtgo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuOptionActivity extends AppCompatActivity {

    private ImageView ivBack, ivGambar;
    private TextView tvNama, tvDeskripsi, tvHarga;
    private LinearLayout llOpsiContainer;
    private Button btnPilih;

    private String menuId;
    private String menuNama, menuDeskripsi, menuGambar;
    private long menuHarga;
    private List<TambahanModel> listTambahan;
    private List<CheckBox> checkBoxList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_option);

        ivBack = findViewById(R.id.ivBack);
        ivGambar = findViewById(R.id.ivMenuImage);
        tvNama = findViewById(R.id.tvMenuNama);
        tvDeskripsi = findViewById(R.id.tvMenuDeskripsi);
        tvHarga = findViewById(R.id.tvMenuHarga);
        llOpsiContainer = findViewById(R.id.llOpsiContainer);
        btnPilih = findViewById(R.id.btnPilih);

        menuId = getIntent().getStringExtra("menuId");

        // Ambil data menu dari Firebase
        DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference("menu").child(menuId);
        menuRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    menuNama = snapshot.child("nama").getValue(String.class);
                    menuDeskripsi = snapshot.child("deskripsi").getValue(String.class);
                    menuGambar = snapshot.child("gambar").getValue(String.class);
                    menuHarga = snapshot.child("harga").getValue(Long.class);

                    tvNama.setText(menuNama);
                    tvDeskripsi.setText(menuDeskripsi);
                    tvHarga.setText("Rp" + String.format("%,d", menuHarga).replace(',', '.'));
                    Glide.with(MenuOptionActivity.this).load(menuGambar).into(ivGambar);

                    // Ambil list tambahan
                    listTambahan = new ArrayList<>();
                    for (DataSnapshot child : snapshot.child("tambahan").getChildren()) {
                        TambahanModel t = child.getValue(TambahanModel.class);
                        if (t != null) listTambahan.add(t);
                    }
                    buatCheckBoxTambahan();
                } else {
                    Toast.makeText(MenuOptionActivity.this, "Menu tidak ditemukan", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuOptionActivity.this, "Gagal memuat menu", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        ivBack.setOnClickListener(v -> finish());

        btnPilih.setOnClickListener(v -> {
            StringBuilder opsi = new StringBuilder();
            long totalTambahan = 0;
            for (CheckBox cb : checkBoxList) {
                if (cb.isChecked()) {
                    TambahanModel t = (TambahanModel) cb.getTag();
                    if (opsi.length() > 0) opsi.append(", ");
                    opsi.append(t.getNama());
                    totalTambahan += t.getHarga();
                }
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("menuId", menuId);
            resultIntent.putExtra("menuNama", menuNama);
            resultIntent.putExtra("menuHarga", menuHarga);
            resultIntent.putExtra("opsi", opsi.toString());
            resultIntent.putExtra("hargaTambahan", totalTambahan);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void buatCheckBoxTambahan() {
        llOpsiContainer.removeAllViews();
        checkBoxList.clear();
        if (listTambahan != null) {
            for (TambahanModel t : listTambahan) {
                CheckBox cb = new CheckBox(this);
                String label = t.getNama();
                if (t.getHarga() > 0) {
                    label += " (+Rp" + String.format("%,d", t.getHarga()).replace(',', '.') + ")";
                } else {
                    label += " (Free)";
                }
                cb.setText(label);
                cb.setTag(t);
                checkBoxList.add(cb);
                llOpsiContainer.addView(cb);
            }
        }
    }
}