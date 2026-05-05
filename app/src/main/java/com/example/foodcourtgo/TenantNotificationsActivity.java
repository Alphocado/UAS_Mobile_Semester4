package com.example.foodcourtgo;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class TenantNotificationsActivity extends AppCompatActivity {
    RecyclerView rv;
    TenantNotificationAdapter adapter;
    List<NotificationModel> list = new ArrayList<>();
    String tenantId;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_notifications);
        tenantId = getSharedPreferences("FoodCourtGoPrefs", MODE_PRIVATE).getString("tenantId", "");
        rv = findViewById(R.id.rv_tenant_notifications);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TenantNotificationAdapter(list);
        rv.setAdapter(adapter);
        loadNotifications();
        findViewById(R.id.btn_back_notifications).setOnClickListener(v -> finish());
    }

    private void loadNotifications() {
        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference("notifications");
        notifRef.orderByChild("tenantId").equalTo(tenantId)
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            NotificationModel n = ds.getValue(NotificationModel.class);
                            if (n != null) list.add(n);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {}
                });
    }
}