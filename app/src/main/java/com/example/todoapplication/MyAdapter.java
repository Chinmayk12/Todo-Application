package com.example.todoapplication;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MyAdapter extends FirebaseRecyclerAdapter<Model, MyAdapter.myViewHolder> {

    public MyAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {
        super(options);
    }
    @Override
    public int getItemCount() {
        // Return the total number of items in your data set
        return super.getItemCount(); // Assuming super class provides item count
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final Model model) {

        // Check task status and set color accordingly
        if ("Done".equals(model.getTaskstatus())) {
            holder.status.setTextColor(Color.GREEN); // Set your completed color
        } else if ("Ongoing".equals(model.getTaskstatus())) {
            holder.status.setTextColor(Color.rgb(255, 165, 0)); // Orange for Ongoing
        } else if ("Pending".equals(model.getTaskstatus())) {
            holder.status.setTextColor(Color.RED); // Red for pending color
        }

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
                showPopupMenu(view, getRef(position).getKey()); // getRef(position).getKey() will give the id of the todo task
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
            time = (TextView) itemView.findViewById(R.id.tasktime);
            status = (TextView) itemView.findViewById(R.id.taskstatus);

            moreoptions = (ImageView) itemView.findViewById(R.id.moreoptions);

        }
    }

    private void showPopupMenu(View view, String todoid) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_options, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.menu_update) {
                    updateTodo(view, todoid);
                    //Toast.makeText(view.getContext(), "Update", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.menu_delete) {
                    deleteTodo(view, todoid);
                    //Toast.makeText(view.getContext(), "Delete", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.menu_complete) {
                    completeTodo(view, todoid);
                    //Toast.makeText(view.getContext(), "Complete", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

        });

        popupMenu.show();
    }

    private void completeTodo(View view, String todoid) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference todoRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUserId)
                .child("todo")
                .child(todoid);

        // Check if task is already completed
        todoRef.child("taskstatus").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String taskStatus = snapshot.getValue(String.class);
                if (taskStatus != null && taskStatus.equals("Done")) {
                    // Task is already completed, show a message or perform any action
                    Toast.makeText(view.getContext(), "Task is already completed", Toast.LENGTH_SHORT).show();
                } else {
                    // Task is not completed, show completion dialog
                    showCompletionDialog(view, todoid, todoRef);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(view.getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCompletionDialog(View view, String todoid, DatabaseReference todoRef) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Complete Todo");
        builder.setMessage("Are you sure you want to Complete this Todo?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked Yes, proceed with the completion
                todoRef.child("taskstatus").setValue("Done")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Successfully updated status
                                showTaskCompleteDialog(view.getContext());
                                Toast.makeText(view.getContext(), "Task Completed", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure
                                Toast.makeText(view.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface alertDialog, int which) {
                // User clicked No, do nothing
                alertDialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void showTaskCompleteDialog(Context context) {
        // Inflate custom dialog layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_task_complete, null);

        // Access views in custom layout
        ImageView taskCompleteImage = dialogView.findViewById(R.id.taskcompleteimage);
        TextView taskDoneText = dialogView.findViewById(R.id.taskdonetxt);
        Button taskDoneButton = dialogView.findViewById(R.id.taskdonebtn);

        // Create MediaPlayer instance
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.success_sound);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();

        taskDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when Done button is clicked
                alertDialog.dismiss();
                // Release MediaPlayer resources
                mediaPlayer.release();
            }
        });

        // Set OnDismissListener to release MediaPlayer resources when the dialog is dismissed
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mediaPlayer.release();
            }
        });

        // Play the music
        mediaPlayer.start();

        // Show the dialog
        alertDialog.show();
    }

    private void updateTodo(View view, String todoid) {
        final DialogPlus dialogPlus = DialogPlus.newDialog(view.getContext())
                .setContentHolder(new ViewHolder(R.layout.update_task))
                .setGravity(Gravity.BOTTOM)
                .setExpanded(true, 2100)
                .create();

        // Inflate the layout inside the DialogPlus content view
        final View[] dialogView = {dialogPlus.getHolderView()};

        final String[] oldTime = new String[1];

        final String[] selectedStatus = new String[1];

        EditText tasktitle = dialogView[0].findViewById(R.id.updatetasktitle);
        EditText taskdesc = dialogView[0].findViewById(R.id.updatetaskdescription);
        EditText taskdate = dialogView[0].findViewById(R.id.updatetaskdate);
        EditText tasktime = dialogView[0].findViewById(R.id.updatetasktime);


        String[] statusOptions = {"Pending", "Ongoing"};
        Spinner taskStatus = dialogView[0].findViewById(R.id.spinnerTaskStatus);
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, statusOptions);
        taskStatus.setAdapter(statusAdapter);

        taskStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected status
                selectedStatus[0] = statusOptions[position];
                // Do something with the selected status
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
        Button updateData = dialogView[0].findViewById(R.id.updatebtn);

        DatabaseReference todoRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("todo")
                .child(todoid);

        todoRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Model existingModel = dataSnapshot.getValue(Model.class);

                    oldTime[0] = existingModel.getTime();

                    // Set the existing data to the corresponding fields in the update dialog
                    tasktitle.setText(existingModel.getTasktitle());
                    taskdesc.setText(existingModel.getTaskdesc());
                    taskdate.setText(existingModel.getFullDate());
                    tasktime.setText(existingModel.getTime());
                }
            }
        });

        taskdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DatePicker", "EditText clicked"); // Add this line
                DatePickerDialog dialog = new DatePickerDialog(view.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view,
                                                  int year, int month, int dayOfMonth) {
                                taskdate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        tasktime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                // Create a TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        view.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Update the tasktime EditText with the selected time
                                tasktime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                            }
                        },
                        hour, minute, false  // 24-hour format
                );

                timePickerDialog.show();
            }
        });

        updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> updateData = new HashMap<>();

                String tasktitletxt = tasktitle.getText().toString().trim();
                String taskdesctxt = taskdesc.getText().toString().trim();
                String datetxt = taskdate.getText().toString().trim();
                String tasktimetxt = tasktime.getText().toString().trim();

                if (tasktitletxt.isEmpty() || taskdesctxt.isEmpty() || datetxt.isEmpty() || tasktimetxt.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateData.put("tasktitle", tasktitletxt);
                updateData.put("taskdesc", taskdesctxt);
                updateData.put("date", datetxt);
                updateData.put("time", tasktimetxt);
                updateData.put("taskstatus", selectedStatus[0]);

                Task<Void> todoRef = FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("todo")
                        .child(todoid)
                        .updateChildren(updateData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                cancelAlarmForTask(getApplicationContext(), todoid, oldTime[0]);
                                updateAlarmForTask(view, datetxt, tasktimetxt, todoid, tasktitletxt, taskdesctxt);
                                Toast.makeText(view.getContext(), "Data Updated", Toast.LENGTH_SHORT).show();
                                dialogPlus.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(view.getContext(), "Error:" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                Log.d("Error", e.getMessage().toString());
                            }
                        });
            }
        });

        dialogPlus.show();
    }


    private void cancelAlarmForTask(Context context, String todoid, String oldTime) {
        // Convert the old time string to milliseconds
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date oldDateTime = format.parse(oldTime);
            long oldTimeMillis = oldDateTime.getTime();

            // Create an intent for the alarm receiver
            Intent intent = new Intent(context, MyReceiver.class);
            //PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT |PendingIntent.FLAG_IMMUTABLE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Get the AlarmManager service and cancel the pending intent
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void updateAlarmForTask(View view, String date, String time, String todoid, String title, String description) {
        try {
            // Combine date and time strings to create a DateTime object
            String dateTimeString = date + " " + time;
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date dateTime = format.parse(dateTimeString);

            // Calculate alarm time
            Calendar alarmTime = Calendar.getInstance();
            alarmTime.setTime(dateTime);

            // Create an intent to start the AlarmReceiver class
            Intent intent = new Intent(view.getContext(), MyReceiver.class);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("datetime", dateTimeString);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(view.getContext(), todoid.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Get the AlarmManager service
            AlarmManager alarmManager = (AlarmManager) view.getContext().getSystemService(Context.ALARM_SERVICE);

            // Set the alarm to trigger at the calculated alarm time
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
            }

            // Display a message to indicate that the alarm has been set
            Toast.makeText(view.getContext(), "Alarm set for task: " + title, Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void deleteTodo(View view, String itemId) {

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Delete Todo");
        builder.setMessage("Are you sure you want to delete this Todo?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked Yes, proceed with the deletion
                FirebaseDatabase.getInstance().getReference().child("users")
                        .child(currentUserId).child("todo").child(itemId)
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Toast.makeText(view.getContext(), "Item Id = " + itemId, Toast.LENGTH_SHORT).show();
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
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface alertDialog, int which) {
                // User clicked No, do nothing
                alertDialog.dismiss();
            }
        });

        builder.create().show();
    }

}