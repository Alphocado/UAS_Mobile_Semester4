package com.example.foodcourtgo;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

public class TenantReportActivity extends AppCompatActivity {
    TextView tvTotalIncome, tvTotalOrder, tvDailyAvg;
    String tenantId;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_report);
        tenantId = getSharedPreferences("FoodCourtGoPrefs", MODE_PRIVATE).getString("tenantId", "");
        tvTotalIncome = findViewById(R.id.tv_report_total_income);
        tvTotalOrder = findViewById(R.id.tv_report_total_order);
        tvDailyAvg = findViewById(R.id.tv_report_daily_average);

        loadReport();
        findViewById(R.id.btn_back_report).setOnClickListener(v -> finish());
    }

    private void loadReport() {
        DatabaseReference pesananRef = FirebaseDatabase.getInstance().getReference("pesanan");
        pesananRef.orderByChild("tenantId").equalTo(tenantId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long totalIncome = 0;
                        int totalOrders = (int) snapshot.getChildrenCount();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Object totalObj = ds.child("totalHarga").getValue();
                            if (totalObj != null) totalIncome += (long) totalObj;
                        }
                        tvTotalIncome.setText("Rp " + String.format("%,d", totalIncome));
                        tvTotalOrder.setText(String.valueOf(totalOrders));
                        tvDailyAvg.setText("Rp " + String.format("%,d", totalOrders > 0 ? totalIncome / totalOrders : 0));
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {}
                });
    }
}