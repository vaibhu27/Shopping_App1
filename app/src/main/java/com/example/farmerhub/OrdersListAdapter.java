package com.example.farmerhub;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmerhub.pojos.Order;
import com.example.farmerhub.pojos.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListViewHolder> {

    List<Order> list = Collections.emptyList();
    Context context;

    boolean isFarmer = false;

    String userId;

    public OrdersListAdapter(List<Order> list,
                             Context context, boolean isFarmer, String userId) {
        this.list = list;
        this.context = context;
        this.isFarmer = isFarmer;
        this.userId = userId;
    }

    @NonNull
    @Override
    public OrdersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View parentView = inflater.inflate(R.layout.orders_card, parent, false);
        OrdersListViewHolder viewHolder = new OrdersListViewHolder(parentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersListViewHolder holder, int position) {
        holder.productTitle.setText(list.get(position).productTitle);
        int price = Integer.parseInt(list.get(position).productPrice);
        int qty = Integer.parseInt(list.get(position).qty);
        holder.productPrice.setText("Total : " + (price * qty) + "₹");
        holder.productStatus.setText("Status : " + list.get(position).status);
        if (isFarmer) {
            if (list.get(position).status.equals("Pending")) {
                holder.buttonsLayout.setVisibility(View.VISIBLE);
            } else {
                holder.buttonsLayout.setVisibility(View.GONE);
            }

            holder.approveButton.setOnClickListener(view -> {
                onApprove(position);
                holder.buttonsLayout.setVisibility(View.GONE);
                holder.productStatus.setText("Status : Approved");
            });

            holder.declineButton.setOnClickListener(view -> {
                onDecline(position);
                holder.buttonsLayout.setVisibility(View.GONE);
                holder.productStatus.setText("Status : Declined");
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void onApprove(int index) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getString("id").equals(list.get(index).id)) {
                        Map<String, Object> updatedOrder = new HashMap<>();
                        updatedOrder.put("status", "Approved");
                        db.collection("orders").document(document.getId()).update(updatedOrder);
                    }
                }
            } else {
                Log.w("TAG", "Error getting details.", task.getException());
            }
        });
    }

    public void onDecline(int index) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getString("id").equals(list.get(index).id)) {
                        Map<String, Object> updatedOrder = new HashMap<>();
                        updatedOrder.put("status", "Declined");
                        db.collection("orders").document(document.getId()).update(updatedOrder);
                    }
                }
            } else {
                Log.w("TAG", "Error getting details.", task.getException());
            }
        });
    }
}
