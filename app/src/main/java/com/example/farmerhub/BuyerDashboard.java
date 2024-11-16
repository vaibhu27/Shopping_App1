package com.example.farmerhub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.farmerhub.pojos.Product;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.search.SearchBar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BuyerDashboard extends AppCompatActivity {

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_dashboard);
        userId = getIntent().getStringExtra("userId");


        EditText searchBar = findViewById(R.id.productSearch);
        Button searchBtn = findViewById(R.id.productSearchBtn);
        RecyclerView recyclerView = findViewById(R.id.productsRecyclerView);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Product> products = new ArrayList<>();

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        Menu menu = toolbar.getMenu();
        MenuItem editProfile =  menu.findItem(R.id.menuEditProfile);
        MenuItem ordersMenu =  menu.findItem(R.id.menuOrders);
        MenuItem logOut =  menu.findItem(R.id.menuLogOut);


        editProfile.setOnMenuItemClickListener(view -> {
            Intent intent = new Intent(this, EditProfile.class);
            intent.putExtra("userId",userId);
            intent.putExtra("isFarmer",false);
            startActivity(intent);
            return false;
        });

        ordersMenu.setOnMenuItemClickListener(view -> {
            Intent intent = new Intent(this, BuyerOrders.class);
            intent.putExtra("userId",userId);
            startActivity(intent);
            return false;
        });

        logOut.setOnMenuItemClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return false;
        });

        db.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Product prod = new Product(document.getString("id"),
                            document.getString("img"),
                            document.getString("title"),
                            document.getString("price"),
                            document.getString("desc"),
                            document.getString("farmerId"));
                    products.add(prod);
                }
                ProductsListAdapter adapter = new ProductsListAdapter(products, getApplication(), false, userId);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            } else {
                Log.w("TAG", "Error getting documents.", task.getException());
            }
        });

        searchBtn.setOnClickListener(view -> {
            List<Product> newList = products.stream().filter(product -> product.getTitle().contains(searchBar.getText())).collect(Collectors.toList());
            ProductsListAdapter adapter = new ProductsListAdapter(newList, getApplication(), false, userId);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(BuyerDashboard.this));
        });


    }
}