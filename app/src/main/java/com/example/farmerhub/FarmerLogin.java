package com.example.farmerhub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmerhub.enums.UserRole;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class FarmerLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmerlogin);
        EditText bemail = findViewById(R.id.femail);
        EditText bpass = findViewById(R.id.fpass);
        Button loginBtn = findViewById(R.id.loginbtn);
        loginBtn.setOnClickListener(view -> onLoginPress(bemail.getText().toString(), bpass.getText().toString()));

        TextView registerLink = findViewById(R.id.regilink);
        registerLink.setOnClickListener(view -> startActivity(new Intent(this, FarmerRegistration.class)));
    }

    public void onLoginPress(String femail, String fpass) {
        if (femail.equals("admin") && fpass.equals("admin")) {
            Intent intent = new Intent(this, AdminDashboard.class);
            startActivity(intent);
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    boolean isValid = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("email").equals(femail) && document.getString("password").equals(fpass) && document.getString("role").equals(UserRole.FARMER.toString())) {
                            isValid = true;
                            Toast.makeText(this, "Logged in Successfully", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(this, FarmerDashboard.class);
                            intent.putExtra("farmerId", document.getString("id"));
                            startActivity(intent);
                        }
                    }
                    if (!isValid) {
                        Toast.makeText(this, "Email or Password is invalid", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.w("TAG", "Error getting documents.", task.getException());
                }
            });
        }

    }
}