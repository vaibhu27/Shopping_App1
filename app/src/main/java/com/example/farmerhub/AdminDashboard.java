package com.example.farmerhub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.farmerhub.enums.UserRole;
import com.example.farmerhub.pojos.Order;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        TextView userCount = findViewById(R.id.userCount);
        TextView farmerCount = findViewById(R.id.farmerCount);
        RecyclerView recyclerView = findViewById(R.id.ordersRecyclerView);
        List<Order> orders = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int uCount = 0, fCount = 0;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getString("role").equals("FARMER")){
                        fCount = fCount +1;
                    }else {
                        uCount = uCount +1;
                    }
                }
                userCount.setText(""+uCount);
                farmerCount.setText(""+fCount);
            } else {
                Log.w("TAG", "Error getting documents.", task.getException());
            }
        });

        db.collection("orders").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Order order = new Order(
                            document.getString("id"),
                            document.getString("buyerId"),
                            document.getString("farmerId"),
                            document.getString("productId"),
                            document.getString("qty"),
                            document.getString("status"),
                            document.getString("productTitle"),
                            document.getString("productPrice")
                    );
                    orders.add(order);
                }
                OrdersListAdapter adapter = new OrdersListAdapter(orders, getApplication(), false, "userId");
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            } else {
                Log.w("TAG", "Error getting documents.", task.getException());
            }
        });
    }
}