package com.example.foodcourtgo;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

public class TenantOrderDetailActivity extends AppCompatActivity {
    TextView tvOrderNumber, tvTableCode, tvOrderTime, tvMenu1, tvMenu2, tvSubtotal;
    String pesananId;
    DatabaseReference pesananRef;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_order_detail);
        pesananId = getIntent().getStringExtra("pesananId");
        pesananRef = FirebaseDatabase.getInstance().getReference("pesanan").child(pesananId);

        tvOrderNumber = findViewById(R.id.tv_detail_order_number);
        tvTableCode = findViewById(R.id.tv_detail_table_code);
        tvOrderTime = findViewById(R.id.tv_detail_order_time);
        tvMenu1 = findViewById(R.id.tv_detail_item_1);
        tvMenu2 = findViewById(R.id.tv_detail_item_2);
        tvSubtotal = findViewById(R.id.tv_detail_subtotal);

        loadDetail();
        findViewById(R.id.btn_back_order_detail).setOnClickListener(v -> finish());
        findViewById(R.id.btn_process_order_detail).setOnClickListener(v -> updateStatus("processing"));
        findViewById(R.id.btn_cancel_order_detail).setOnClickListener(v -> updateStatus("cancelled"));
    }

    private void loadDetail() {
        pesananRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                PesananAdminModel p = snap.getValue(PesananAdminModel.class);
                if (p == null) return;
                tvOrderNumber.setText(p.getId());
                tvTableCode.setText("Meja / Kode: " + p.getMeja());
                tvOrderTime.setText("Waktu: " + p.getWaktu());
                // Tampilkan dua item pertama (karena layout hanya punya dua TextView)
                if (p.getItems() != null && p.getItems().size() > 0) {
                    ItemPesananModel item1 = p.getItems().get(0);
                    tvMenu1.setText(item1.getNama() + "  " + item1.getQty() + "x  Rp " + item1.getHarga());
                }
                if (p.getItems() != null && p.getItems().size() > 1) {
                    ItemPesananModel item2 = p.getItems().get(1);
                    tvMenu2.setText(item2.getNama() + "  " + item2.getQty() + "x  Rp " + item2.getHarga());
                }
                tvSubtotal.setText("Subtotal Rp " + String.format("%,d", p.getTotalHarga()));
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void updateStatus(String status) {
        pesananRef.child("status").setValue(status).addOnSuccessListener(u ->
                Toast.makeText(this, "Status diubah ke " + status, Toast.LENGTH_SHORT).show());
    }
}