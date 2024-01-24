package com.example.todoapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class signup extends AppCompatActivity {

    EditText username,email,password;
    Button signup;
    TextView aldlogin;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            // User is already logged in, start Home activity
            startHomeActivity();
        }
        username = (EditText) findViewById(R.id.loginemail);
        email = (EditText) findViewById(R.id.signupemail);
        password = (EditText) findViewById(R.id.loginpassword);
        signup = (Button) findViewById(R.id.loginbtn);
        aldlogin = (TextView) findViewById(R.id.alreadylogin);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailtxt,passwordtxt,usernametxt;

                usernametxt = username.getText().toString();
                emailtxt = email.getText().toString();
                passwordtxt = password.getText().toString();

                Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(emailtxt, passwordtxt)
                        .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(),"User Registed Successfully",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"AN Error Occured",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
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
        Intent intent = new Intent(getApplicationContext(), Home.class);
        intent.putExtra("email", mAuth.getCurrentUser().getEmail());
        intent.putExtra("uid", mAuth.getCurrentUser().getUid());
        startActivity(intent);
        finish(); // Finish the current activity to prevent going back to the login screen
    }

}