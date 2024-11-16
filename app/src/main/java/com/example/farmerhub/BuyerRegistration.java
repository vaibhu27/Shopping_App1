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

public class BuyerRegistration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_registration);
        EditText bname = findViewById(R.id.bname);
        EditText bemail = findViewById(R.id.bemail);
        EditText bpass = findViewById(R.id.bpassword);
        Button registerBtn = findViewById(R.id.regibtn);

        registerBtn.setOnClickListener(view -> onRegisterPress(bname.getText().toString(),bemail.getText().toString(),bpass.getText().toString()));

        Log.d("TAG", "onCreate: "+bemail + bname + bpass);
    }

    public void onRegisterPress(String bname , String bemail, String bpass){
        User newUser = new User(UUID.randomUUID().toString(),bname,bemail,bpass, UserRole.BUYER);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").add(newUser)
                .addOnSuccessListener(documentReference -> Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.d("TAG", "Error adding document", e));
        Toast.makeText(this,"Registered Successfully",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(BuyerRegistration.this, BuyerLogin.class);
        startActivity(intent);
    }

}