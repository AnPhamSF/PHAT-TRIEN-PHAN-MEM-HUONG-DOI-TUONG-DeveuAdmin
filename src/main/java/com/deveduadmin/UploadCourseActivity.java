package com.deveduadmin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.deveduadmin.Model.CourseModel;
import com.deveduadmin.databinding.ActivityUploadCourseBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class UploadCourseActivity extends AppCompatActivity {


    ActivityUploadCourseBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri imageUri,videoUri;
    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUploadCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        loadingDialog = new Dialog(UploadCourseActivity.this);
        loadingDialog.setContentView(R.layout.loading_dialog);

        if(loadingDialog.getWindow() !=null){
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
        }


        binding.uploadThub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,1);
            }
        });

        binding.uploadintroV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 2);
            }
        });

        binding.btnUploadCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title= binding.edtTitle.getText().toString();
                String price= binding.edtPrice.getText().toString();
                String duration= binding.edtDuration.getText().toString();
                String rating= binding.edtRating.getText().toString();
                String description= binding.edtDescription.getText().toString();

                if (imageUri==null){

                    Toast.makeText(UploadCourseActivity.this, "select thumbnail image", Toast.LENGTH_SHORT).show();
                } else if (title.isEmpty()) {
                    binding.edtTitle.setError("Enter title");
                    
                } else if (price.isEmpty()) {
                    binding.edtPrice.setError("Enter price");

                } else if (duration.isEmpty()) {
                    binding.edtDuration.setError("Enter duration");
                } else if (rating.isEmpty()) {
                    binding.edtRating.setError("Enter rating");
                } else if (description.isEmpty()) {
                    binding.edtDescription.setError("Enter description");
                }
                else {

                    uploadCourse(title,price,duration,rating,description,imageUri);
                }
            }
        });

    }

    private void uploadCourse(String title, String price, String duration, String rating, String description, Uri imageUri) {

        loadingDialog.show();

        StorageReference reference = storage.getReference().child("thumbnail") .child(new Date().getTime()+"");

        reference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {


                        CourseModel model = new CourseModel();

                        model.setTitle(title);
                        model.setDuration(duration);
                        model.setRating(rating);
                        model.setDescription(description);
                        model.setPrice(Long.parseLong(price));
                        model.setThumbnail(imageUri.toString());
                        model.setIntroVideo(uri.toString());
                        model.setPostedBy(auth.getUid());
                        model.setEnable("false");

                        database.getReference().child("course")
                                .push()
                                .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                       loadingDialog.dismiss();

                                       Toast.makeText(UploadCourseActivity.this, "Course uploaded", Toast.LENGTH_SHORT).show();
                                       onBackPressed();
                                    }
                                });

                    }
                });
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1){

            if(data !=null){

                uploadThumbnail(data.getData());
            }


        }
        else {

            videoUri = data.getData();
        }
    }

    private void uploadThumbnail(Uri data) {

        loadingDialog.show();

        StorageReference reference = storage.getReference().child("thumbnail") .child(new Date().getTime()+"");

        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        imageUri = uri;
                        loadingDialog.dismiss();
                    }
                });
            }
        });
    }
}