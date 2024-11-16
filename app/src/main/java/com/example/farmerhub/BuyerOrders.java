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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BuyerOrders extends AppCompatActivity {

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_orders);

        // Retrieve userId passed from PaymentActivity
        userId = getIntent().getStringExtra("userId");

        // Check if userId is null
        if (userId == null) {
            Log.e("TAG", "User ID is null!");
            return; // Optionally, show an error message or redirect to another activity
        }

        RecyclerView recyclerView = findViewById(R.id.ordersRecyclerView);
        List<Order> orders = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String buyerId = document.getString("buyerId"); // Get buyerId from document

                    // Check if buyerId is not null before comparing
                    if (buyerId != null && buyerId.equals(userId)) {
                        Order order = new Order(
                                document.getString("id"),
                                buyerId,
                                document.getString("farmerId"),
                                document.getString("productId"),
                                document.getString("qty"),
                                document.getString("status"),
                                document.getString("productTitle"),
                                document.getString("productPrice")
                        );
                        orders.add(order);
                    }
                }

                // Sort the orders by their ID (or any relevant property)
                Collections.sort(orders, new Comparator<Order>() {
                    @Override
                    public int compare(Order o1, Order o2) {
                        return o2.getId().compareTo(o1.getId());
                    }
                });

                // Set up the adapter and attach it to the RecyclerView
                OrdersListAdapter adapter = new OrdersListAdapter(orders, getApplication(), false, userId);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            } else {
                Log.w("TAG", "Error getting documents.", task.getException());
            }
        });
    }
}
