package com.example.farmerhub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmerhub.enums.UserRole;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class BuyerLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyerlogin);

        EditText bemail = findViewById(R.id.bemail);
        EditText bpass = findViewById(R.id.bpass);
        Button loginBtn = findViewById(R.id.loginbtn);
        loginBtn.setOnClickListener(view -> onLoginPress(bemail.getText().toString(), bpass.getText().toString()));

        TextView registerLink = findViewById(R.id.regilink);
        registerLink.setOnClickListener(view -> startActivity(new Intent(BuyerLogin.this, BuyerRegistration.class)));
    }

    public void onLoginPress(String bemail, String bpass) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getString("email").equals(bemail) && document.getString("password").equals(bpass) && document.getString("role").equals(UserRole.BUYER.toString())) {
                        Toast.makeText(this, "Logged in Successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(BuyerLogin.this, BuyerDashboard.class);
                        intent.putExtra("userId",document.getString("id"));
                        startActivity(intent);
                    }
                }
            } else {
                Log.w("TAG", "Error getting documents.", task.getException());
            }
        });
    }
}