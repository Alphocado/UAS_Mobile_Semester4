package com.example.foodcourtgo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardAdminActivity extends AppCompatActivity {

    private TextView tvTotalOrders, tvTotalRevenue, tvActiveTenant, tvMenuCount;
    private DatabaseReference pesananRef, tenantRef, menuRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_admin);

        tvTotalOrders = findViewById(R.id.tv_total_orders);
        tvTotalRevenue = findViewById(R.id.tv_total_revenue);
        tvActiveTenant = findViewById(R.id.tv_active_tenant);
        tvMenuCount = findViewById(R.id.tv_menu_count);

        pesananRef = FirebaseDatabase.getInstance().getReference("pesanan");
        tenantRef = FirebaseDatabase.getInstance().getReference("tenant");
        menuRef = FirebaseDatabase.getInstance().getReference("menu");

        loadDashboardData();

        // Bottom navigation
        findViewById(R.id.nav_dashboard).setOnClickListener(v -> {});
        findViewById(R.id.nav_tenant).setOnClickListener(v ->
                startActivity(new Intent(this, TenantManagementActivity.class)));
        findViewById(R.id.nav_menu).setOnClickListener(v ->
                startActivity(new Intent(this, MenuManagementActivity.class)));
        findViewById(R.id.nav_pesanan).setOnClickListener(v ->
                startActivity(new Intent(this, PesananActivity.class)));
        findViewById(R.id.nav_profil).setOnClickListener(v ->
                startActivity(new Intent(this, ProfilAdminActivity.class)));

        findViewById(R.id.btn_quick_tenant).setOnClickListener(v ->
                startActivity(new Intent(this, TenantManagementActivity.class)));
        findViewById(R.id.btn_quick_menu).setOnClickListener(v ->
                startActivity(new Intent(this, MenuManagementActivity.class)));
        findViewById(R.id.btn_quick_report).setOnClickListener(v ->
                startActivity(new Intent(this, LaporanActivity.class)));
        findViewById(R.id.btn_profile_shortcut).setOnClickListener(v ->
                startActivity(new Intent(this, ProfilAdminActivity.class)));
    }

    private void loadDashboardData() {
        // Total pesanan & pendapatan dari node pesanan
        pesananRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalOrders = (int) snapshot.getChildrenCount();
                long totalRevenue = 0;
                for (DataSnapshot pesananSnap : snapshot.getChildren()) {
                    Object totalObj = pesananSnap.child("totalHarga").getValue();
                    if (totalObj != null) totalRevenue += (long) totalObj;
                }
                tvTotalOrders.setText(String.valueOf(totalOrders));
                tvTotalRevenue.setText("Rp " + String.format("%,d", totalRevenue).replace(',', '.'));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Tenant aktif (status = "active")
        tenantRef.orderByChild("status").equalTo("active")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int activeCount = (int) snapshot.getChildrenCount();
                        tvActiveTenant.setText(String.valueOf(activeCount));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        // Total menu
        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int menuCount = (int) snapshot.getChildrenCount();
                tvMenuCount.setText(String.valueOf(menuCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}