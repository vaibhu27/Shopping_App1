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

import java.util.UUID;

public class FarmerRegistration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmerregistration);

        EditText fname = findViewById(R.id.fname);
        EditText femail = findViewById(R.id.femail);
        EditText fpassword = findViewById(R.id.fpassword);
        Button registerBtn = findViewById(R.id.regibtn);

        registerBtn.setOnClickListener(view -> onRegisterPress(fname.getText().toString(),femail.getText().toString(),fpassword.getText().toString()));

    }

    public void onRegisterPress(String fname , String femail, String fpass){
        User newUser = new User(UUID.randomUUID().toString(),fname,femail,fpass, UserRole.FARMER);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").add(newUser)
                .addOnSuccessListener(documentReference -> Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.d("TAG", "Error adding document", e));
        Toast.makeText(this,"Registered Successfully",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, FarmerLogin.class);
        startActivity(intent);
    }
}