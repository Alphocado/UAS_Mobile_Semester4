package com.example.foodcourtgo;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LaporanActivity extends AppCompatActivity {

    private TextView tvReportIncome, tvReportOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        tvReportIncome = findViewById(R.id.tv_report_income);
        tvReportOrder = findViewById(R.id.tv_report_order);

        DatabaseReference pesananRef = FirebaseDatabase.getInstance().getReference("pesanan");
        pesananRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalOrders = (int) snapshot.getChildrenCount();
                long totalIncome = 0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Object totalObj = snap.child("totalHarga").getValue();
                    if (totalObj != null) totalIncome += (long) totalObj;
                }
                tvReportOrder.setText(String.valueOf(totalOrders));
                tvReportIncome.setText("Rp " + String.format("%,d", totalIncome).replace(',', '.'));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        findViewById(R.id.btn_export_report).setOnClickListener(v ->
                Toast.makeText(this, "Laporan diekspor", Toast.LENGTH_SHORT).show());
        findViewById(R.id.filter_daily).setOnClickListener(v -> Toast.makeText(this, "Filter harian", Toast.LENGTH_SHORT).show());
        findViewById(R.id.filter_monthly).setOnClickListener(v -> Toast.makeText(this, "Filter bulanan", Toast.LENGTH_SHORT).show());
        findViewById(R.id.filter_yearly).setOnClickListener(v -> Toast.makeText(this, "Filter tahunan", Toast.LENGTH_SHORT).show());
    }
}