package com.example.farmerhub;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;


public class OrdersListViewHolder extends RecyclerView.ViewHolder {
    TextView productTitle;
    TextView productPrice;
    TextView productStatus;

    LinearLayout buttonsLayout;

    MaterialButton approveButton;

    MaterialButton declineButton;

    View view;

    public OrdersListViewHolder(@NonNull View itemView) {
        super(itemView);
        productTitle = itemView.findViewById(R.id.productTitle);
        productPrice = itemView.findViewById(R.id.productPrice);
        productStatus = itemView.findViewById(R.id.productStatus);
        buttonsLayout = itemView.findViewById(R.id.buttonsLayout);
        approveButton = itemView.findViewById(R.id.approveButton);
        declineButton = itemView.findViewById(R.id.declineButton);
        view = itemView;
    }
}
