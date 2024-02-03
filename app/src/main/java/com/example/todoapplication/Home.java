package com.example.todoapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
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
        Toast.makeText(getApplicationContext(),"UID:"+mAuth.getCurrentUser().getUid(),Toast.LENGTH_SHORT).show();


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

        calender = (ImageView) findViewById(R.id.calenderimg);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Model> options =
                new FirebaseRecyclerOptions.Builder<Model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("todo"),Model.class)
                        .build();

        myAdapter = new MyAdapter(options);
        recyclerView.setAdapter(myAdapter);

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
                Log.d("DatePicker", "EditText clicked"); // Add this line
                DatePickerDialog dialog = new DatePickerDialog(Home.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view,
                                                  int year, int month, int dayOfMonth) {
                                taskdate.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                            }
                        }, 2024, 2, 7);
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