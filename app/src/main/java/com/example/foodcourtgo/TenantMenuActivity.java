package com.example.foodcourtgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class TenantMenuActivity extends AppCompatActivity {
    RecyclerView rvMenu;
    MenuAdminAdapter adapter;
    List<MenuModel> menuList = new ArrayList<>();
    String tenantId;
    DatabaseReference menuRef;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_menu);

        tenantId = getSharedPreferences("FoodCourtGoPrefs", MODE_PRIVATE).getString("tenantId", "");
        rvMenu = findViewById(R.id.rv_menu);
        FloatingActionButton fab = findViewById(R.id.fab_add_menu);

        rvMenu.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MenuAdminAdapter(menuList, menu -> {
            // hapus menu
            menuRef.child(menu.getMenuId()).removeValue();
        });
        rvMenu.setAdapter(adapter);

        menuRef = FirebaseDatabase.getInstance().getReference("menu");
        loadMenu();

        fab.setOnClickListener(v -> startActivity(new Intent(this, TenantAddMenuActivity.class)));
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void loadMenu() {
        menuRef.orderByChild("tenantId").equalTo(tenantId)
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        menuList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            MenuModel menu = ds.getValue(MenuModel.class);
                            if (menu != null) {
                                menu.setMenuId(ds.getKey());
                                menuList.add(menu);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {}
                });
    }
}