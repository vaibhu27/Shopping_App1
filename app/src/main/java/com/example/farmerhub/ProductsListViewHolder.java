package com.example.farmerhub;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

public class ProductsListViewHolder extends RecyclerView.ViewHolder {

    ImageView productImage;
    TextView productTitle;
    TextView productPrice;
    ImageButton productDelete;
    View view;
    public ProductsListViewHolder(@NonNull View itemView) {
        super(itemView);
        productImage =  itemView.findViewById(R.id.productImage);
        productTitle =  itemView.findViewById(R.id.productTitle);
        productPrice = itemView.findViewById(R.id.productPrice);
        productDelete = itemView.findViewById(R.id.deleteButton);
        view = itemView;
    }
}
