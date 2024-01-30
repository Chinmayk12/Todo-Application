package com.example.todoapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MyAdapter extends FirebaseRecyclerAdapter<Model,MyAdapter.myViewHolder> {

    public MyAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final Model model) {

        holder.day.setText(model.getDay());
        holder.date.setText((int) model.getDate());
        holder.month.setText(model.getMonth());
        holder.tasktitle.setText(model.getTasktitle());
        holder.taskdesc.setText(model.getTaskdesc());
        holder.time.setText(model.getTime());
        holder.status.setText(model.getStatus());
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder
    {
        TextView day,date,month,tasktitle,taskdesc,time,status;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            day = (TextView) itemView.findViewById(R.id.day);
            date = (TextView) itemView.findViewById(R.id.date);
            month = (TextView) itemView.findViewById(R.id.month);
            tasktitle = (TextView) itemView.findViewById(R.id.tasktitle);
            taskdesc = (TextView) itemView.findViewById(R.id.taskdescription);
            time = (TextView) itemView.findViewById(R.id.tasktitle);
            status = (TextView) itemView.findViewById(R.id.taskstatus);

        }
    }
}
