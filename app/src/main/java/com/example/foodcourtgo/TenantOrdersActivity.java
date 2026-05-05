package com.example.foodcourtgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class TenantOrdersActivity extends AppCompatActivity {

    RecyclerView rvOrders;
    TenantOrderAdapter adapter;
    List<PesananAdminModel> allOrders = new ArrayList<>();
    String tenantId;
    DatabaseReference pesananRef;
    TextView tabAll, tabPending, tabProcessing, tabDone;
    String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_orders);

        rvOrders = findViewById(R.id.rv_tenant_orders);
        tenantId = getSharedPreferences("FoodCourtGoPrefs", MODE_PRIVATE).getString("tenantId", "");

        tabAll = findViewById(R.id.tab_orders_all);
        tabPending = findViewById(R.id.tab_orders_pending);
        tabProcessing = findViewById(R.id.tab_orders_process);
        tabDone = findViewById(R.id.tab_orders_done);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TenantOrderAdapter(new ArrayList<>(), order -> {
            Intent i = new Intent(this, TenantOrderDetailActivity.class);
            i.putExtra("pesananId", order.getId());
            startActivity(i);
        });
        rvOrders.setAdapter(adapter);

        pesananRef = FirebaseDatabase.getInstance().getReference("pesanan");
        loadOrders();

        tabAll.setOnClickListener(v -> setFilter("all"));
        tabPending.setOnClickListener(v -> setFilter("pending"));
        tabProcessing.setOnClickListener(v -> setFilter("processing"));
        tabDone.setOnClickListener(v -> setFilter("done"));

        findViewById(R.id.btn_back_orders).setOnClickListener(v -> finish());
    }

    private void loadOrders() {
        pesananRef.orderByChild("tenantId").equalTo(tenantId)
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allOrders.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            PesananAdminModel p = ds.getValue(PesananAdminModel.class);
                            if (p != null) {
                                p.setId(ds.getKey());
                                allOrders.add(p);
                            }
                        }
                        applyFilter();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {}
                });
    }

    private void setFilter(String filter) {
        currentFilter = filter;
        // set background
        tabAll.setBackgroundResource(filter.equals("all") ? R.drawable.bg_nav_active : R.drawable.bg_card);
        tabPending.setBackgroundResource(filter.equals("pending") ? R.drawable.bg_nav_active : R.drawable.bg_card);
        tabProcessing.setBackgroundResource(filter.equals("processing") ? R.drawable.bg_nav_active : R.drawable.bg_card);
        tabDone.setBackgroundResource(filter.equals("done") ? R.drawable.bg_nav_active : R.drawable.bg_card);
        applyFilter();
    }

    private void applyFilter() {
        List<PesananAdminModel> filtered = new ArrayList<>();
        for (PesananAdminModel p : allOrders) {
            if (currentFilter.equals("all") || p.getStatus().equals(currentFilter)) {
                filtered.add(p);
            }
        }
        adapter.updateList(filtered);
    }
}