package com.example.todoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class alarm_display_acticity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    TextView titleTextView,descriptionTextView,alarmdateandtime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_display_acticity);

        // Retrieve task details from the intent
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String alarmdatetime = getIntent().getStringExtra("datetime");

        Toast.makeText(getApplicationContext(),title+":"+description,Toast.LENGTH_SHORT).show();

        // Display task details in TextViews or other UI elements
        titleTextView = findViewById(R.id.alarmtasktitle);
        descriptionTextView = findViewById(R.id.alarmtaskdescription);
        alarmdateandtime = findViewById(R.id.alarmdateandtime);

        titleTextView.setText(title);
        descriptionTextView.setText(description);
        alarmdateandtime.setText(alarmdatetime);


        mediaPlayer =  MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
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
    }
}