package com.deveduadmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deveduadmin.Model.PlayListModel;
import com.deveduadmin.R;
import com.deveduadmin.databinding.RvPlaylistDesignBinding;

import java.util.ArrayList;


public class PlayListAdapter extends  RecyclerView.Adapter<PlayListAdapter.viewHolder>{

    Context context;
    ArrayList<PlayListModel>list;

    public PlayListAdapter(Context context, ArrayList<PlayListModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PlayListAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.rv_playlist_design,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListAdapter.viewHolder holder, int position) {

        PlayListModel model = list.get(position);

        holder.binding.title.setText(model.getTitle());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class  viewHolder extends RecyclerView.ViewHolder {

        RvPlaylistDesignBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = RvPlaylistDesignBinding.bind(itemView);
        }
    }
}
