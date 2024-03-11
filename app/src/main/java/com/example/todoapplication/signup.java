package com.example.todoapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class signup extends AppCompatActivity {

    EditText email, password, username;
    Button signup;
    TextView aldlogin;
    private FirebaseAuth mAuth;
    private NetworkChangeReceiver networkChangeReceiver;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        // For Network Connectivity Checking
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);


        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            // User is already logged in, start Home activity
            startHomeActivity();
        }

        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.signupemail);
        password = (EditText) findViewById(R.id.signuppassword);
        signup = (Button) findViewById(R.id.signupbtn);
        aldlogin = (TextView) findViewById(R.id.alreadylogin);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailtxt, passwordtxt, usernametxt;

                emailtxt = email.getText().toString();
                passwordtxt = password.getText().toString();
                usernametxt = username.getText().toString();

                if ((emailtxt.isEmpty() && passwordtxt.isEmpty()) || (emailtxt.isEmpty() || passwordtxt.isEmpty()) || (usernametxt.isEmpty() || usernametxt.isEmpty())) {
                    Toast.makeText(getApplicationContext(), "Empty Field.", Toast.LENGTH_SHORT).show();
                } else if (passwordtxt.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
                } else {
                    Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(emailtxt, passwordtxt)
                            .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putInt("flag", 1);
                                        editor.apply();

                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        //Setting userProfleName of Email by UserProfileChangeRequest.Builder and set Name from usertxt
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(usernametxt)
                                                // You can also set other profile information like photo URL
                                                //.setPhotoUri(Uri.parse("https://example.com/johndoe/photo.jpg"))
                                                .build();

                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // Profile updated successfully
                                                        } else {
                                                            // Failed to update profile
                                                        }
                                                    }
                                                });


                                        Toast.makeText(getApplicationContext(), "User Registed Successfully", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(signup.this, login.class));

                                    } else {
                                        // User registration failed
                                        Toast.makeText(getApplicationContext(), "An error occurred: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        Log.e("Signup Error", "Error creating user", task.getException());
                                    }
                                }
                            });
                }
            }
        });
        aldlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(signup.this, login.class);
                startActivity(i);
            }
        });
    }

    private void startHomeActivity() {
        //Intent intent = new Intent(getApplicationContext(), Home.class);
        startActivity(new Intent(getApplicationContext(), Home.class));
        finishAffinity(); // Finish the current activity to prevent going back to the login screen
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

}