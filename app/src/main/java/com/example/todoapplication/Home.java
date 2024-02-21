package com.example.todoapplication;
import android.annotation.SuppressLint;
import java.text.ParseException;
import java.util.Calendar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.facebook.AccessToken;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class Home extends AppCompatActivity {
    DialogPlus dialogPlus;

    public void logoutUser(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Sign out the current authenticated user from Firebase
                FirebaseAuth.getInstance().signOut();

                // Clear the entire task stack and start the signup activity as a new task
                Intent intent = new Intent(getApplicationContext(), signup.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
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

    // Added This Class For Inconsistency Error Resolving Which I Was Getting When I Goes To Allow Notification For App
    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        //... constructor
        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("TAG", "meet a IOOBE in RecyclerView");
            }
        }
    }

    private static final String CHANNEL_ID = "My Channel";

    TextView useruid, username;

    private FirebaseAuth mAuth;
    private AccessToken accessToken;
    private SharedPreferences sharedPreferences;
    ImageView calender, calenderBackButton;
    RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private CalendarView calendarView;
    GifImageView noTasksImageView;
    PopupMenu popupMenu;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        createNotificationChannel();

        username = (TextView) findViewById(R.id.hello);
        noTasksImageView = findViewById(R.id.noTasksImageView);
        noTasksImageView.setVisibility(View.GONE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String usernametxt = user.getDisplayName();
        username.setText("Hello " + usernametxt);

        mAuth = FirebaseAuth.getInstance();
        Toast.makeText(getApplicationContext(), "UID:" + mAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();

        // Check if the app has notification permission
        if (!isNotificationPermissionGranted()) {
            // If not granted, request the permission
            requestNotificationPermission();
        }

        /*
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // For name of user
        String usernametxt = user.getDisplayName();

        mAuth = FirebaseAuth.getInstance();
        accessToken = AccessToken.getCurrentAccessToken();

        username = findViewById(R.id.username);
        useruid = findViewById(R.id.useruid);
        logoutbtn = findViewById(R.id.logoutbtn);

        username.setText("Username = "+usernametxt);
        // Can also fetch UID od authenticated user from intent
        //useruid.setText("UID = " + getIntent().getStringExtra("uid"));
        useruid.setText("UID = " + mAuth.getCurrentUser().getUid());
        */


        /*
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sign out the current authenticated user from Firebase
                FirebaseAuth.getInstance().signOut();

                // Clear the entire task stack and start the signup activity as a new task
                Intent intent = new Intent(getApplicationContext(), signup.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        */

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        FirebaseRecyclerOptions<Model> options =
                new FirebaseRecyclerOptions.Builder<Model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("todo"), Model.class)
                        .build();

        myAdapter = new MyAdapter(options,this);
        recyclerView.setAdapter(myAdapter);

        checkTasksExistence();


    }

    private void checkTasksExistence() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(mAuth.getCurrentUser().getUid())
                .child("todo");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    noTasksImageView.setVisibility(View.GONE);
                } else {
                    noTasksImageView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("checkTasksExistence", "DatabaseError: " + databaseError.getMessage());
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Todo App";
            String description = "Task Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    private boolean isNotificationPermissionGranted() {
        // Check if notification permission is granted
        return NotificationManagerCompat.from(this).areNotificationsEnabled();
    }

    private void requestNotificationPermission() {
        // Display a dialog to the user to request notification permission
        new AlertDialog.Builder(this)
                .setTitle("Notification Permission")
                .setMessage("This app requires notification permission to function properly.")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Open notification settings to allow the permission
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                        startActivity(intent);

                        //startActivity(new Intent(getApplicationContext(), Home.class));
                    }
                })
                .setNegativeButton("Disallow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User disallowed notification permission
                        // You can handle this situation as needed
                    }
                })
                .show();
    }


    public void openDateDialog(View view) {
        final DialogPlus dialogPlus = DialogPlus.newDialog(Home.this)
                .setContentHolder(new ViewHolder(R.layout.custom_date_picker))
                .setGravity(Gravity.BOTTOM)
                .setExpanded(true, 1400)
                .setCancelable(true)
                .create();

        View dialogView = dialogPlus.getHolderView();
        calendarView = dialogView.findViewById(R.id.materialCalendarView);
        calenderBackButton = dialogView.findViewById(R.id.calenderbackbutton);

        calenderBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPlus.dismiss();
            }
        });

        // Manually mark a date for testing
        //markSpecificDate(calendarView, 2024, 2, 14);
        markTaskDates(calendarView);

        dialogPlus.show();
    }


    private void markTaskDates(CalendarView calendarView) {
        Log.d("markTaskDates", "Method called");

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(mAuth.getCurrentUser().getUid())
                .child("todo");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<EventDay> events = new ArrayList<>();

                Log.d("markTaskDates", "DataSnapshot children count: " + dataSnapshot.getChildrenCount());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Model task = snapshot.getValue(Model.class);
                    Log.d("markTaskDates", "Task: " + task);

                    if (task != null && task.getDate() != null) {
                        String[] dateParts = task.getFullDate().split("/");
                        if (dateParts.length >= 3) {
                            Calendar calendar = Calendar.getInstance();

                            String dd = dateParts[0]; // This is likely causing the issue
                            String month = dateParts[1];
                            String year = dateParts[2];

                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dd));
                            calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
                            calendar.set(Calendar.YEAR, Integer.parseInt(year));

                            events.add(new EventDay(calendar, R.drawable.dot));
                        }
                    }
                }

                Log.d("markTaskDates", "Events count: " + events.size());

                // Set the events for the calendar view
                calendarView.setEvents(events);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("markTaskDates", "DatabaseError: " + databaseError.getMessage());
            }
        });
    }

    protected void onStart() {
        super.onStart();
        myAdapter.startListening();
        checkTasksExistence(); // Update visibility when the activity starts
    }

    @Override
    protected void onStop() {
        super.onStop();
        myAdapter.stopListening();
    }
    @Override
    protected void onPause() {
        super.onPause();
        checkTasksExistence();
    }

    public void addData(View view) {

        dialogPlus = DialogPlus.newDialog(Home.this)
                .setContentHolder(new ViewHolder(R.layout.add_task))
                .setGravity(Gravity.BOTTOM)
                .setExpanded(true, 2000)
                .setCancelable(true)
                .create();

        // Inflate the layout inside the DialogPlus content view
        View dialogView = dialogPlus.getHolderView();

        EditText tasktitle = dialogView.findViewById(R.id.tasktitle);
        EditText taskdesc = dialogView.findViewById(R.id.taskdescription);
        EditText taskdate = dialogView.findViewById(R.id.taskdate);
        EditText tasktime = dialogView.findViewById(R.id.tasktime);

        Button adddata = dialogView.findViewById(R.id.addtask);

        taskdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("DatePicker", "EditText clicked"); // Add this line
                DatePickerDialog dialog = new DatePickerDialog(Home.this,
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
                        Home.this,
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

        adddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tasktitletxt = tasktitle.getText().toString();
                String taskdesctxt = taskdesc.getText().toString();
                String datetxt = taskdate.getText().toString();
                String tasktimetxt = tasktime.getText().toString();

                // Check if any field is empty
                if (tasktitletxt.isEmpty() || taskdesctxt.isEmpty() || datetxt.isEmpty() || tasktimetxt.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a Map to represent the data
                Map<String, Object> newData = new HashMap<>();

                newData.put("tasktitle", tasktitletxt);
                newData.put("taskdesc", taskdesctxt);
                newData.put("date", datetxt);
                newData.put("time", tasktimetxt);
                newData.put("taskstatus", "Pending");

                String uniqueKey = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("todo").push().getKey();
                ;

                FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("todo").child(uniqueKey).setValue(newData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                addAlarmForTask(datetxt, tasktimetxt, tasktitletxt, taskdesctxt);
                                Toast.makeText(getApplicationContext(), "Data Added", Toast.LENGTH_SHORT).show();
                                checkTasksExistence();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Error While Adding Data", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        dialogPlus.show();
    }

    private void addAlarmForTask(String date, String time, String title, String description) {
        try {
            // Combine date and time strings to create a DateTime object
            String dateTimeString = date + " " + time;
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date dateTime = format.parse(dateTimeString);

            // Calculate alarm time
            Calendar alarmTime = Calendar.getInstance();
            alarmTime.setTime(dateTime);

            // Create an intent to start the AlarmReceiver class
            Intent intent = new Intent(getApplicationContext(), MyReceiver.class);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("datetime", dateTimeString);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Get the AlarmManager service
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            // Set the alarm to trigger at the calculated alarm time
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
            }

            // Display a message to indicate that the alarm has been set
            Toast.makeText(getApplicationContext(), "Alarm set for task: " + title, Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void toolbarmenu(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.nav_menu_options, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.aboutus) {
                    //Toast.makeText(view.getContext(), "About Us", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), about_us.class));
                } else if (id == R.id.logoutuser) {
                    //Toast.makeText(view.getContext(), "Log out", Toast.LENGTH_SHORT).show();
                    logoutUser(view);
                }
                return true;
            }

        });

        popupMenu.show();
    }


    public void onBackPressed() {
        // Check if DialogPlus is showing and dismiss it
        if (dialogPlus != null && dialogPlus.isShowing()) {
            Log.d("Back Button Clicked ?", "Yes ");
            dialogPlus.dismiss();
        } else {
            // If DialogPlus is not showing, proceed with the default behavior
            super.onBackPressed();
        }
    }

}
