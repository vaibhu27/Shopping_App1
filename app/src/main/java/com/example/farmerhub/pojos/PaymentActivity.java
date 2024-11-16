package com.example.farmerhub.pojos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farmerhub.BuyerOrders;
import com.example.farmerhub.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {

    private static final String TAG = "PaymentActivity";

    ImageView imageView;
    TextView title, price, desc;
    Button btn;
    EditText mobile, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        imageView = findViewById(R.id.img);
        title = findViewById(R.id.tit);
        price = findViewById(R.id.pric);
        desc = findViewById(R.id.desc);
        btn = findViewById(R.id.paymentproceed);
        mobile = findViewById(R.id.mobilenum);
        email = findViewById(R.id.emailname);

        // Retrieve orderId, userId, and other data from the Intent
        String orderId = getIntent().getStringExtra("orderId");
        String productId = getIntent().getStringExtra("productId");
        String productTitle = getIntent().getStringExtra("title");
        String productPrice = getIntent().getStringExtra("price");
        String userId = getIntent().getStringExtra("userId"); // Retrieve userId

        title.setText(productTitle);
        price.setText(productPrice);
        desc.setText("Pending");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean productFound = false;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getString("id").equals(productId)) {
                        productFound = true;
                        Picasso.get().load(document.getString("img")).into(imageView);
                        break;
                    }
                }
                if (!productFound) {
                    Toast.makeText(this, "Product not found!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w(TAG, "Error getting details.", task.getException());
                Toast.makeText(this, "Error fetching product details!", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle payment button click
        btn.setOnClickListener(view -> {
            if (orderId != null) {
                startPayment(email.getText().toString(), mobile.getText().toString(), productPrice);
            } else {
                Toast.makeText(this, "Invalid order ID!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startPayment(String email, String number, String price) {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_aMNqw4zJIdz6wK");  // Use your own Razorpay test key
        checkout.setImage(R.drawable.ic_launcher_background);

        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Pay For Now");
            options.put("description", "Payment for " + title.getText().toString());
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg");
            options.put("currency", "INR");
            options.put("amount", String.valueOf(Integer.parseInt(price) * 100));  // Amount in paise
            options.put("prefill.email", email);
            options.put("prefill.contact", number);

            checkout.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in starting payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error in starting payment", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();
        btn.setVisibility(View.GONE);

        // Update the order status to "Paid" in Firestore
        String orderId = getIntent().getStringExtra("orderId");
        String userId = getIntent().getStringExtra("userId"); // Get the userId from the intent
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders").document(orderId).update("status", "Paid")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Order updated to 'Paid'", Toast.LENGTH_SHORT).show();
                    // Navigate to BuyerOrders activity
                    Intent intent = new Intent(PaymentActivity.this, BuyerOrders.class);
                    intent.putExtra("userId", userId); // Pass userId to BuyerOrders
                    startActivity(intent);
                    finish(); // Finish PaymentActivity to prevent going back to it
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update order status", e);
                    Toast.makeText(this, "Failed to update order status", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed: " + s, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Payment Error: " + s);
    }
}
