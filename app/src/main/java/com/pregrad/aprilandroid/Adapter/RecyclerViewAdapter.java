package com.pregrad.aprilandroid.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pregrad.aprilandroid.Model.Students;
import com.pregrad.aprilandroid.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>{
    List<Students> studentsList;

    public RecyclerViewAdapter(List<Students> studentsList) {
        this.studentsList = studentsList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Students students = studentsList.get(position);
        holder.name.setText(students.getName());
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), students.getName()+ " clicked", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
        }
    }
}
