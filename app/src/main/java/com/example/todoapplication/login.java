package com.example.todoapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class login extends AppCompatActivity {

    EditText email,password;
    Button loginbtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();
        // Check if the user is already authenticated
        if (mAuth.getCurrentUser() != null) {
            // User is already logged in, start Home activity
            startHomeActivity();
        }

        email = (EditText) findViewById(R.id.loginemail);
        password = (EditText) findViewById(R.id.loginpassword);
        loginbtn = (Button) findViewById(R.id.loginbtn);

    }
    public void login(View view) {
        String emailtxt = email.getText().toString();
        String passwordtxt = password.getText().toString();

        mAuth.signInWithEmailAndPassword(emailtxt, passwordtxt)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String uid = mAuth.getCurrentUser().getUid();

                            Map<String, String> userData = new HashMap<>();
                            //userData.put("username", usernametxt);
                            userData.put("email", emailtxt);
                            userData.put("password", passwordtxt);

                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference users = db.getReference("users");

                            users.child(uid).setValue(userData);

                            // Starting Home Screen Activity
                            startHomeActivity();


                        } else {
                            email.setText("");
                            password.setText("");
                            Toast.makeText(getApplicationContext(),"Invalid Email Or Password.",Toast.LENGTH_LONG).show();
                        }
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