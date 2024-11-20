package com.deveduadmin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.deveduadmin.Adapter.CourseAdapter;
import com.deveduadmin.Model.CourseModel;
import com.deveduadmin.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    Dialog loadingDialog;
    ArrayList<CourseModel>list;
    CourseAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        loadingDialog = new Dialog(MainActivity.this);
        loadingDialog.setContentView(R.layout.loading_dialog);

        if(loadingDialog.getWindow() !=null){
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
        }

        loadingDialog.show();

        list= new ArrayList<>();

        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this,2);
        binding.rvCourse.setLayoutManager(layoutManager);

        adapter = new CourseAdapter(MainActivity.this,list);
        binding.rvCourse.setAdapter(adapter);

        database.getReference().child("course").orderByChild("postedBy").equalTo(auth.getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()){

                                    list.clear();

                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                                        CourseModel model = dataSnapshot.getValue(CourseModel.class);
                                        model.setPostId(dataSnapshot.getKey());
                                        list.add(model);

                                    }
                                    adapter.notifyDataSetChanged();
                                    loadingDialog.dismiss();
                                }
                                else {

                                    loadingDialog.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();

                            }

                        });



        binding.uploadCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MainActivity.this,UploadCourseActivity.class);
                startActivity(intent);
            }
        });

    }
}