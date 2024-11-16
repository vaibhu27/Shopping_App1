package com.example.farmerhub;

import static com.example.farmerhub.FarmerEditProduct.onAdminDeletePress;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmerhub.pojos.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListViewHolder> {

    List<Product> list = Collections.emptyList();
    Context context;

    boolean isFarmer = false;

    String userId;

    public ProductsListAdapter(List<Product> list,
                               Context context, boolean isFarmer, String userId) {
        this.list = list;
        this.context = context;
        this.isFarmer = isFarmer;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ProductsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View parentView = inflater.inflate(R.layout.products_card, parent, false);
        ProductsListViewHolder viewHolder = new ProductsListViewHolder(parentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsListViewHolder holder, int position) {
        Product product = list.get(position);
        Picasso.get().load(product.getImg()).into(holder.productImage);
        holder.productTitle.setText(product.getTitle());
        holder.productPrice.setText(product.getPrice() + "/kg");

        holder.view.setOnClickListener(view -> {
            if (isFarmer) {
                onAdminCardPress(position);
            } else {
                onCardPressed(position);
            }
        });

        if (isFarmer) {
            holder.productDelete.setVisibility(View.VISIBLE);
            holder.productDelete.setOnClickListener(view -> onAdminDeletePress(product.getId(), position));
        } else {
            // Hide delete button for non-farmers
            holder.productDelete.setVisibility(View.GONE);
        }
    }

    private void onAdminDeletePress(String productId, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Delete the product from Firestore
        db.collection("products").document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                    // Remove the product from the list and notify the adapter
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, list.size());
                })
                .addOnFailureListener(e -> {
                    Log.w("TAG", "Error deleting document", e);
                    Toast.makeText(context, "Failed to delete product", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void onCardPressed(int index) {
        Intent intent = new Intent(context, ProductDetail.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("productId", list.get(index).getId());
        intent.putExtra("userId",userId);
        context.startActivity(intent);
    }

    public void onAdminCardPress(int index) {
        Intent intent = new Intent(context, FarmerEditProduct.class);
        intent.putExtra("productId", list.get(index).getId());
        intent.putExtra("farmerId", userId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}
