package com.example.todoapplication;

import android.annotation.SuppressLint;
import com.applandeo.materialcalendarview.CalendarDay;
import java.util.Calendar;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.utils.DateUtils;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Home extends AppCompatActivity {

    TextView useruid,username;
    Button logoutbtn;
    private FirebaseAuth mAuth;
    private AccessToken accessToken;
    private SharedPreferences sharedPreferences;
    ImageView calender;
    RecyclerView recyclerView;
    private MyAdapter myAdapter;
    DialogPlus dialogPlus;
    private List<String> taskDates;
    private CalendarView calendarView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        username = (TextView) findViewById(R.id.hello);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String usernametxt = user.getDisplayName();
        username.setText("Hello "+usernametxt);

        mAuth = FirebaseAuth.getInstance();
        //Toast.makeText(getApplicationContext(),"UID:"+mAuth.getCurrentUser().getUid(),Toast.LENGTH_SHORT).show();


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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Model> options =
                new FirebaseRecyclerOptions.Builder<Model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("todo"),Model.class)
                        .build();

        myAdapter = new MyAdapter(options);
        recyclerView.setAdapter(myAdapter);

    }

    public void openDateDialog(View view)
    {

        final DialogPlus dialogPlus = DialogPlus.newDialog(Home.this)
                .setContentHolder(new ViewHolder(R.layout.custom_date_picker))
                .setGravity(Gravity.BOTTOM)
                .setExpanded(true, 1500)
                .setCancelable(true)
                .create();

        View dialogView = dialogPlus.getHolderView();
        calendarView = dialogView.findViewById(R.id.materialCalendarView);
        markTaskDates(calendarView);

        dialogPlus.show();
    }

    private void markTaskDates(CalendarView calendarView) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(mAuth.getCurrentUser().getUid())
                .child("todo");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<EventDay> events = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Model task = snapshot.getValue(Model.class);
                    if (task != null && task.getDate() != null) {

                        // Assuming the date format is "d/M/yyyy"
                        String[] dateParts = task.getDate().split("/");
                        if (dateParts.length == 3) {

                            Toast.makeText(getApplicationContext(),"Entered",Toast.LENGTH_SHORT).show();

                            int dayOfMonth = Integer.parseInt(dateParts[0]);
                            int month = Integer.parseInt(dateParts[1]) - 1; // Months are 0-indexed
                            int year = Integer.parseInt(dateParts[2]);

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            calendar.set(Calendar.MONTH, month); // Months are 0-indexed
                            calendar.set(Calendar.YEAR, year);

                            events.add(new EventDay(calendar, R.drawable.dot, Color.parseColor("#228B22")));
                        }
                    }
                }

                // Set events to mark task dates with dots
                calendarView.setEvents(events);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }




    protected void onStart() {
        super.onStart();
        myAdapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        myAdapter.stopListening();
    }

    public void addData(View view) {

        final DialogPlus dialogPlus = DialogPlus.newDialog(Home.this)
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
                                taskdate.setText(dayOfMonth+"/"+(month+1)+"/"+year);
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

//                // Check if any field is empty
//                if (studentName.isEmpty() || studentCourse.isEmpty() || studentEmail.isEmpty() || studentImgUrl.isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                // Create a Map to represent the data
                Map<String, Object> newData = new HashMap<>();

                newData.put("tasktitle", tasktitletxt);
                newData.put("taskdesc", taskdesctxt);
                newData.put("date", datetxt);
                newData.put("time", tasktimetxt);
                newData.put("taskstatus", "Pending");

                String uniqueKey  = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("todo").push().getKey();;

                FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("todo").child(uniqueKey).setValue(newData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(),"Data Added",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Error While Adding Data",Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        dialogPlus.show();
    }
    public void onBackPressed() {
        // Check if DialogPlus is showing and dismiss it
        if (dialogPlus != null && dialogPlus.isShowing()) {
            Log.d("Back Button Clicked ?","Yes ");
            dialogPlus.dismiss();
        } else {
            // If DialogPlus is not showing, proceed with the default behavior
            super.onBackPressed();
        }
    }

}