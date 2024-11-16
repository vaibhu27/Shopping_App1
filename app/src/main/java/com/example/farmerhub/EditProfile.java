package com.example.farmerhub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.farmerhub.enums.UserRole;
import com.example.farmerhub.pojos.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditProfile extends AppCompatActivity {

    String userId;
    String documentId;
    boolean isFarmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        userId = getIntent().getStringExtra("userId");
        isFarmer = getIntent().getBooleanExtra("isFarmer", false);

        EditText name = findViewById(R.id.name);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        Button updateUser = findViewById(R.id.updateUser);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getString("id").equals(userId)) {
                        documentId = document.getId();
                        name.setText(document.getString("name"));
                        email.setText(document.getString("email"));
                        password.setText(document.getString("password"));
                    }
                }
            } else {
                Log.w("TAG", "Error getting details.", task.getException());
            }
        });

        updateUser.setOnClickListener(v -> onUpdatePress(name.getText().toString(), email.getText().toString(), password.getText().toString()));

    }

    public void onUpdatePress(String name, String email, String pass) {
        Map<String, Object> updatedUser = new HashMap<>();

        updatedUser.put("name", name);
        updatedUser.put("email", email);
        updatedUser.put("password", pass);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(documentId).update(updatedUser);

        Toast.makeText(this, "User Updated Successfully", Toast.LENGTH_LONG).show();

        if (isFarmer) {
            Intent intent = new Intent(this, FarmerDashboard.class);
            intent.putExtra("farmerId", userId);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, BuyerDashboard.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        }
    }
}