package com.example.todoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class alarm_display_acticity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    ImageView backbtn;
    TextView titleTextView,descriptionTextView,alarmdateandtime;
    private NetworkChangeReceiver networkChangeReceiver;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_display_acticity);

        // For Network Connectivity Checking
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);


        // Retrieve task details from the intent
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String alarmdatetime = getIntent().getStringExtra("datetime");

        //Toast.makeText(getApplicationContext(),title+":"+description,Toast.LENGTH_SHORT).show();

        // Display task details in TextViews or other UI elements
        titleTextView = findViewById(R.id.alarmtasktitle);
        descriptionTextView = findViewById(R.id.alarmtaskdescription);
        alarmdateandtime = findViewById(R.id.alarmdateandtime);
        backbtn = findViewById(R.id.backBtn);

        titleTextView.setText(title);
        descriptionTextView.setText(description);
        alarmdateandtime.setText(alarmdatetime);


        mediaPlayer =  MediaPlayer.create(this, R.raw.notification);
        mediaPlayer.start();

        // Add functionality for the "Close" button if needed
        Button closeButton = (Button) findViewById(R.id.closebtn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the activity
                mediaPlayer.stop();
                startActivity(new Intent(getApplicationContext(),Home.class));
                finishAffinity();
            }

        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                startActivity(new Intent(getApplicationContext(),Home.class));
                finishAffinity();
            }
        });

    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }
}