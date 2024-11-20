package com.deveduadmin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deveduadmin.Model.CourseModel;
import com.deveduadmin.Model.UserModel;
import com.deveduadmin.R;
import com.deveduadmin.UploadPlayListActivity;
import com.deveduadmin.databinding.RvCourseDesginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CourseAdapter extends  RecyclerView.Adapter<CourseAdapter.viewHolder>{

    Context context;
    ArrayList<CourseModel>list;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public CourseAdapter(Context context, ArrayList<CourseModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CourseAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.rv_course_desgin,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.viewHolder holder, int position) {

        CourseModel model = list.get(position);

        Picasso.get().load(model.getThumbnail())
                .placeholder(R.drawable.placeholder)
                .into(holder.binding.courseImage);


        holder.binding.courseTitle.setText(model.getTitle());
        holder.binding.coursePrice.setText(model.getPrice()+"");

        database.getReference().child("admin_details").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    UserModel userModel = snapshot.getValue(UserModel.class);

                    Picasso.get().load(userModel.getProfile())
                            .placeholder(R.drawable.placeholder)
                            .into(holder.binding.postedByProfile);

                    holder.binding.name.setText(userModel.getName());

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, UploadPlayListActivity.class);
                intent.putExtra("postId",model.getPostId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class  viewHolder extends RecyclerView.ViewHolder {

        RvCourseDesginBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = RvCourseDesginBinding.bind(itemView);
        }
    }
}
