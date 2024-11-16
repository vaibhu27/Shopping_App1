package com.example.farmerhub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmerhub.pojos.Order;
import com.example.farmerhub.pojos.PaymentActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class ProductDetail extends AppCompatActivity {

    private String productId;
    private String farmerId;
    private String userId;

    private String title;
    private String price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Retrieve user ID and product ID from Intent
        userId = getIntent().getStringExtra("userId");
        productId = getIntent().getStringExtra("productId");

        ImageView productImage = findViewById(R.id.productImage);
        TextView productTitle = findViewById(R.id.productTitle);
        TextView productPrice = findViewById(R.id.productPrice);
        TextView productDesc = findViewById(R.id.productDesc);
        MaterialButton buyNow = findViewById(R.id.buyNow);
        EditText productQty = findViewById(R.id.productQty);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch product details from Firestore
        db.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean productFound = false;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getString("id").equals(productId)) {
                        productFound = true; // Set the flag if product is found
                        Picasso.get().load(document.getString("img")).into(productImage);
                        productTitle.setText(document.getString("title"));
                        title = document.getString("title");
                        productPrice.setText(document.getString("price"));
                        price = document.getString("price");
                        productDesc.setText(document.getString("desc"));
                        farmerId = document.getString("farmerId");
                        break; // No need to continue the loop once the product is found
                    }
                }

                // Handle case where product was not found
                if (!productFound) {
                    Toast.makeText(this, "Product not found!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w("TAG", "Error getting details.", task.getException());
                Toast.makeText(this, "Error fetching product details!", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle buy now button click
        buyNow.setOnClickListener(view -> {
            String quantity = productQty.getText().toString();
            if (!quantity.isEmpty()) {
                // Create an order object
                Order order = new Order(String.valueOf(UUID.randomUUID()), userId, farmerId, productId, quantity, "Pending", title, price);

                db.collection("orders").add(order).addOnSuccessListener(documentReference -> {
                    // Pass the newly created order ID and other necessary data to PaymentActivity
                    Intent intent = new Intent(ProductDetail.this, PaymentActivity.class);
                    intent.putExtra("orderId", documentReference.getId());  // Pass the order ID
                    intent.putExtra("productId", productId); // Pass the product ID
                    intent.putExtra("price", price);
                    intent.putExtra("title", title);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }).addOnFailureListener(e -> {
                    Toast.makeText(ProductDetail.this, "Order creation failed!", Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(this, "Please Enter Qty", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
