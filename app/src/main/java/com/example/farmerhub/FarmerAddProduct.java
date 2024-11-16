package com.example.farmerhub;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.farmerhub.enums.UserRole;
import com.example.farmerhub.pojos.Product;
import com.example.farmerhub.pojos.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class FarmerAddProduct extends AppCompatActivity {

    ProgressDialog dialog;
    Uri imageuri;
    String imageURL = "https://static.vecteezy.com/system/resources/previews/005/337/799/original/icon-image-not-found-free-vector.jpg";
    ImageView productImage;
    String farmerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_add_product);
        farmerId = getIntent().getStringExtra("farmerId");

        productImage = findViewById(R.id.productImage);
        EditText productTitle = findViewById(R.id.productTitle);
        EditText productPrice = findViewById(R.id.productPrice);
        EditText productDesc = findViewById(R.id.productDesc);
        Button addProductSubmit = findViewById(R.id.addProductSubmit);

        addProductSubmit.setOnClickListener(view -> onSubmitPress(productTitle.getText().toString(), productPrice.getText().toString(), productDesc.getText().toString()));

        productImage.setOnClickListener(view -> onSelectImage());
    }

    public void onSubmitPress(String productTitle, String productPrice, String productDesc) {
        Product newProduct = new Product(UUID.randomUUID().toString(), imageURL, productTitle, productPrice, productDesc, farmerId);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products").add(newProduct)
                .addOnSuccessListener(documentReference -> Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.d("TAG", "Error adding document", e));
        Toast.makeText(this, "Product Added Successfully", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, FarmerDashboard.class);
        intent.putExtra("farmerId",farmerId);
        startActivity(intent);
    }

    public void onSelectImage() {
        Intent imageIntent = new Intent();
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);

        imageIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(imageIntent, "select image"), 22);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            // Here we are initialising the progress dialog box
            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading");

            // this will show message uploading
            // while pdf is uploading
            dialog.show();
            imageuri = data.getData();
            final String timestamp = "" + System.currentTimeMillis();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            // Here we are uploading the pdf in firebase storage with the name of current time
            final StorageReference fileRef = storageReference.child("Images/" + timestamp + ".jpg");
            UploadTask uploadTask =  fileRef.putFile(imageuri);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.d("TAG", "onActivityResult: "+downloadUri);
                    dialog.dismiss();
                    imageURL = downloadUri.toString();
                    Picasso.get().load(imageURL).resize(50,50).onlyScaleDown().into(productImage);
                    productImage.setScaleType(ImageView.ScaleType.FIT_XY);
                    Toast.makeText(FarmerAddProduct.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    task.getException();
                    Toast.makeText(FarmerAddProduct.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}