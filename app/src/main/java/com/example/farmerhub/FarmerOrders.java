package com.example.farmerhub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.farmerhub.pojos.Order;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FarmerOrders extends AppCompatActivity {

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_orders);
        userId = getIntent().getStringExtra("userId");
        RecyclerView recyclerView = findViewById(R.id.ordersRecyclerView);
        List<Order> orders = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getString("buyerId") != null && document.getString("buyerId").equals(userId)) {

                        String farmerId = document.getString("farmerId");
                        if (farmerId != null && farmerId.equals(userId)) {
                            Order order = new Order(
                                    document.getString("id"),
                                    document.getString("buyerId"),
                                    farmerId,  // Use the already extracted farmerId
                                    document.getString("productId"),
                                    document.getString("qty"),
                                    document.getString("status"),
                                    document.getString("productTitle"),
                                    document.getString("productPrice")

                            );
                            orders.add(order);
                        }
                    }
                }
                OrdersListAdapter adapter = new OrdersListAdapter(orders, getApplication(), true, userId);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            } else {
                Log.w("TAG", "Error getting documents.", task.getException());
            }
        });

    }
}