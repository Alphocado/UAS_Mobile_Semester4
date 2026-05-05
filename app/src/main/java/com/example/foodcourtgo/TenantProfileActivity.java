package com.example.foodcourtgo;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

public class TenantProfileActivity extends AppCompatActivity {
    EditText etName, etEmail, etPhone, etLocation;
    Button btnSave;
    String tenantId;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_profile);
        tenantId = getSharedPreferences("FoodCourtGoPrefs", MODE_PRIVATE).getString("tenantId", "");
        etName = findViewById(R.id.et_profile_stand_name);
        etEmail = findViewById(R.id.et_profile_email);
        etPhone = findViewById(R.id.et_profile_phone);
        etLocation = findViewById(R.id.et_profile_location);
        btnSave = findViewById(R.id.btn_save_profile);

        loadProfile();
        btnSave.setOnClickListener(v -> saveProfile());
        findViewById(R.id.btn_back_profile).setOnClickListener(v -> finish());
    }

    private void loadProfile() {
        DatabaseReference tenantRef = FirebaseDatabase.getInstance().getReference("tenant").child(tenantId);
        tenantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                TenantModel t = snap.getValue(TenantModel.class);
                if (t != null) {
                    etName.setText(t.getNama());
                    etEmail.setText(t.getEmail());
                    etPhone.setText(t.getTelepon());
                    etLocation.setText(t.getLokasi());
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void saveProfile() {
        String nama = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String lokasi = etLocation.getText().toString().trim();
        DatabaseReference tenantRef = FirebaseDatabase.getInstance().getReference("tenant").child(tenantId);
        tenantRef.child("nama").setValue(nama);
        tenantRef.child("email").setValue(email);
        tenantRef.child("telepon").setValue(phone);
        tenantRef.child("lokasi").setValue(lokasi);
        Toast.makeText(this, "Profil disimpan", Toast.LENGTH_SHORT).show();
    }
}