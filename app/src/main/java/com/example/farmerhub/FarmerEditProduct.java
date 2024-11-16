package com.example.farmerhub;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.farmerhub.pojos.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FarmerEditProduct extends AppCompatActivity {

    ProgressDialog dialog;
    Uri imageuri;
    String imageURL = "https://static.vecteezy.com/system/resources/previews/005/337/799/original/icon-image-not-found-free-vector.jpg";
    ImageView productImage;

    String productId;
Button delete;
    String documentId;
    String farmerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_edit_product);
        productId = getIntent().getStringExtra("productId");
        farmerId = getIntent().getStringExtra("farmerId");

        productImage = findViewById(R.id.productImage);
        EditText productTitle = findViewById(R.id.productTitle);
        EditText productPrice = findViewById(R.id.productPrice);
        EditText productDesc = findViewById(R.id.productDesc);
        Button updateProductSubmit = findViewById(R.id.updateProductSubmit);


        delete=findViewById(R.id.deleteFarmerproduct);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getString("id").equals(productId)) {
                        documentId = document.getId();
                        imageURL = document.getString("img");
                        Picasso.get().load(imageURL).into(productImage);
                        productTitle.setText(document.getString("title"));
                        productPrice.setText(document.getString("price"));
                        productDesc.setText(document.getString("desc"));
                    }
                }
            } else {
                Log.w("TAG", "Error getting details.", task.getException());
            }
        });

        updateProductSubmit.setOnClickListener(view -> onSubmitPress(productTitle.getText().toString(), productPrice.getText().toString(), productDesc.getText().toString()));

        productImage.setOnClickListener(view -> onSelectImage());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onAdminDeletePress(documentId);
                startActivity(new Intent(FarmerEditProduct.this,FarmerDashboard.class));
            }
        });




    }

    public void onSubmitPress(String productTitle, String productPrice, String productDesc) {
        Map<String, Object> updatedProduct = new HashMap<>();

        updatedProduct.put("img", imageURL);
        updatedProduct.put("title", productTitle);
        updatedProduct.put("price", productPrice);
        updatedProduct.put("desc", productDesc);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products").document(documentId).update(updatedProduct);

        Toast.makeText(this, "Product Updated Successfully", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(FarmerEditProduct.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    Toast.makeText(FarmerEditProduct.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    protected static void onAdminDeletePress(String index) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getString("id").equals(index)) {
                        db.collection("products").document(document.getId()).delete();

                    }
                }

            } else {
                Log.w("TAG", "Error getting details.", task.getException());
            }
        });
    }
}