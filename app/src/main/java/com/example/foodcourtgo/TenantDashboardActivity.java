package com.example.foodcourtgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;
import java.util.Locale;

public class TenantDashboardActivity extends AppCompatActivity {

    TextView tvWelcome, tvTodayOrders, tvProcessOrders, tvDoneOrders, tvTotalSales;
    String tenantId, tenantName;
    DatabaseReference pesananRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_dashboard);

        SharedPreferences pref = getSharedPreferences("FoodCourtGoPrefs", MODE_PRIVATE);
        tenantId = pref.getString("tenantId", "");
        tenantName = pref.getString("namaUser", "Tenant");

        tvWelcome = findViewById(R.id.tv_tenant_welcome);
        tvTodayOrders = findViewById(R.id.tv_today_orders_value);
        tvProcessOrders = findViewById(R.id.tv_process_orders_value);
        tvDoneOrders = findViewById(R.id.tv_done_orders_value);
        tvTotalSales = findViewById(R.id.tv_total_sales_value);

        tvWelcome.setText("Halo, " + tenantName);
        pesananRef = FirebaseDatabase.getInstance().getReference("pesanan");

        pesananRef.orderByChild("tenantId").equalTo(tenantId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int pending = 0, processing = 0, done = 0;
                        long totalSales = 0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            PesananAdminModel p = ds.getValue(PesananAdminModel.class);
                            if (p == null) continue;
                            String status = p.getStatus();
                            if ("pending".equals(status)) pending++;
                            else if ("processing".equals(status)) processing++;
                            else if ("done".equals(status)) {
                                done++;
                                totalSales += p.getTotalHarga();
                            }
                        }
                        tvTodayOrders.setText(String.valueOf(pending + processing + done));
                        tvProcessOrders.setText(String.valueOf(processing));
                        tvDoneOrders.setText(String.valueOf(done));
                        tvTotalSales.setText("Rp " + String.format(Locale.getDefault(), "%,d", totalSales));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        // Bottom Navigation
        TextView navDashboard = findViewById(R.id.nav_tenant_dashboard);
        TextView navOrders = findViewById(R.id.nav_tenant_orders);
        TextView navMenu = findViewById(R.id.nav_tenant_menu);
        TextView navProfile = findViewById(R.id.nav_tenant_profile);

        navDashboard.setOnClickListener(v -> {});
        navOrders.setOnClickListener(v -> startActivity(new Intent(this, TenantOrdersActivity.class)));
        navMenu.setOnClickListener(v -> startActivity(new Intent(this, TenantMenuActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(this, TenantProfileActivity.class)));

        findViewById(R.id.btn_tenant_notification).setOnClickListener(v ->
                startActivity(new Intent(this, TenantNotificationsActivity.class)));
        findViewById(R.id.btn_view_all_orders).setOnClickListener(v ->
                startActivity(new Intent(this, TenantOrdersActivity.class)));
    }
}