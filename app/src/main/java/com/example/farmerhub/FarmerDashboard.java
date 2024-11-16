package com.example.farmerhub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.farmerhub.pojos.Product;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FarmerDashboard extends AppCompatActivity {

    String farmerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_dashboard);
        FloatingActionButton addProductBtn = findViewById(R.id.addProductBtn);
        farmerId = getIntent().getStringExtra("farmerId");
        RecyclerView recyclerView = findViewById(R.id.productsRecyclerView);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        Menu menu = toolbar.getMenu();
        MenuItem editProfile =  menu.findItem(R.id.menuEditProfile);
        MenuItem menuOrders =  menu.findItem(R.id.menuOrders);
        MenuItem logOut =  menu.findItem(R.id.menuLogOut);

        editProfile.setOnMenuItemClickListener(view -> {
            Intent intent = new Intent(this, EditProfile.class);
            intent.putExtra("userId",farmerId);
            intent.putExtra("isFarmer",true);
            startActivity(intent);
            return false;
        });

        menuOrders.setOnMenuItemClickListener(view -> {
            Intent intent = new Intent(this, FarmerOrders.class);
            intent.putExtra("userId",farmerId);
            startActivity(intent);
            return false;
        });

        logOut.setOnMenuItemClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return false;
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Product> products = new ArrayList<>();
        db.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getString("farmerId").equals(farmerId)){
                        Product prod = new Product(document.getString("id"),
                                document.getString("img"),
                                document.getString("title"),
                                document.getString("price"),
                                document.getString("desc"),
                                document.getString("farmerId"));
                        products.add(prod);
                    }
                }
                ProductsListAdapter adapter = new ProductsListAdapter(products, getApplication(),true,farmerId );
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            } else {
                Log.w("TAG", "Error getting documents.", task.getException());
            }
        });

        Intent intent = new Intent(this, FarmerAddProduct.class);
        intent.putExtra("farmerId",farmerId);
        addProductBtn.setOnClickListener(view -> startActivity(intent));
    }


}