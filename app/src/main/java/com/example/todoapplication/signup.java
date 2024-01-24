package com.example.todoapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

        username = (EditText) findViewById(R.id.loginemail);
        email = (EditText) findViewById(R.id.signupemail);
        password = (EditText) findViewById(R.id.loginpassword);
        signup = (Button) findViewById(R.id.loginbtn);
        aldlogin = (TextView) findViewById(R.id.alreadylogin);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailtxt,passwordtxt;

                emailtxt = email.getText().toString();
                passwordtxt = password.getText().toString();

                mAuth = FirebaseAuth.getInstance();

                Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(emailtxt, passwordtxt)
                        .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(),"User Registed Successfully",Toast.LENGTH_LONG).show();
                                } else {
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

}