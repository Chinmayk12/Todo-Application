package com.example.todoapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

public class MyAdapter extends FirebaseRecyclerAdapter<Model, MyAdapter.myViewHolder> {

    public MyAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final Model model) {

        holder.day.setText(model.getDay());
        holder.date.setText((model.getDate()));
        holder.month.setText(model.getMonth());
        holder.tasktitle.setText(model.getTasktitle());
        holder.taskdesc.setText(model.getTaskdesc());
        holder.time.setText(model.getTime());
        holder.status.setText(model.getTaskstatus());


        holder.moreoptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view,getRef(position).getKey()); // getRef(position).getKey() will give the id of the todo task
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView day, date, month, tasktitle, taskdesc, time, status;
        ImageView moreoptions;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            day = (TextView) itemView.findViewById(R.id.day);
            date = (TextView) itemView.findViewById(R.id.date);
            month = (TextView) itemView.findViewById(R.id.month);
            tasktitle = (TextView) itemView.findViewById(R.id.tasktitle);
            taskdesc = (TextView) itemView.findViewById(R.id.taskdescription);
            time = (TextView) itemView.findViewById(R.id.tasktitle);
            status = (TextView) itemView.findViewById(R.id.taskstatus);

            moreoptions = (ImageView) itemView.findViewById(R.id.moreoptions);

        }
    }

    private void showPopupMenu(View view,String todoid) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_options, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.menu_update) {
                    Toast.makeText(view.getContext(), "Update", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.menu_delete) {
                    deleteTodo(view,todoid);
                    //Toast.makeText(view.getContext(), "Delete", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.menu_delete) {
                    Toast.makeText(view.getContext(), "Delete", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

        });

        popupMenu.show();
    }

    private void deleteTodo(View view,String itemId) {
        FirebaseDatabase.getInstance().getReference().child("users").child("15qi5crURVTNPaI02fEbVquIX9r1").child("todo").child(itemId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(view.getContext(), "Item Id = "+itemId, Toast.LENGTH_SHORT).show();
                        Toast.makeText(view.getContext(), "Todo deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("MyAdapter", "Error deleting todo", e);
                        Toast.makeText(view.getContext(), "Error deleting todo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}