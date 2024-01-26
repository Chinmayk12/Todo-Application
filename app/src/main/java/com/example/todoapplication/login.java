package com.example.todoapplication;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {
    private static final int REQ_ONE_TAP = 100;
    EditText email,password;
    private FirebaseAuth mAuth;
    private BeginSignInRequest signInRequest;
    private SignInClient oneTapClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        int flag =  sharedPreferences.getInt("flag", 0);

        mAuth = FirebaseAuth.getInstance();

        // Check if the user is already authenticated
        if (mAuth.getCurrentUser() != null && flag!=1) {
            // User is already logged in, start Home activity
            startHomeActivity();
        }

        email = (EditText) findViewById(R.id.loginemail);
        password = (EditText)findViewById(R.id.loginpassword);
    }

    public void login(View view) {

        String emailtxt = email.getText().toString();
        String passwordtxt = password.getText().toString();

        if ((emailtxt.isEmpty() && passwordtxt.isEmpty()) || (emailtxt.isEmpty() || passwordtxt.isEmpty())) {
            Toast.makeText(getApplicationContext(), "Empty Field.", Toast.LENGTH_SHORT).show();
        }
        else {
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
                        Toast.makeText(getApplicationContext(), "Invalid Email Or Password.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    public void googleLogin(View view) {

        //Google Identity Services API to create a sign-in client for handling Google Sign-In.
        oneTapClient = Identity.getSignInClient(this);

        // Making a signin request to google
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server' s client ID, not your Android client ID.
                        .setServerClientId("683343048151-ka2gucftadutja36daiohjtc0cnkch7q.apps.googleusercontent.com")
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();

        //Receiving Sigin In Request.
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e("Error:", "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d(  "Error:", e.getLocalizedMessage());
                    }
                });
    }
    @Override
        // Results for google SignIn
        protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode) {
                case REQ_ONE_TAP:
                    try {
                        //If the result is successful,
                        // it extracts the SignInCredential from the intent data and uses it to sign in with Firebase using the Google ID token.
                        SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);

                        // Authenticate with Firebase using the Google ID token
                        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(credential.getGoogleIdToken(), null))
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            String uid = mAuth.getCurrentUser().getUid();

                                            Map<String, String> userData = new HashMap<>();
                                            //userData.put("username", usernametxt);
                                            userData.put("email", mAuth.getCurrentUser().getEmail());

                                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                                            DatabaseReference users = db.getReference("users");

                                            users.child(uid).setValue(userData);

                                            startHomeActivity();
                                        } else {
                                            Log.w("Google SignIn", "signInWithCredential:failure", task.getException());
                                            Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    } catch (ApiException e) {
                        // Handle API exception
                        Log.e("Google SignIn", "API exception: " + e.getStatusCode());
                    }
                    break;
            }
        }
    private void startHomeActivity() {
        Intent intent = new Intent(getApplicationContext(),Home.class);
        intent.putExtra("email", mAuth.getCurrentUser().getEmail());
        intent.putExtra("uid", mAuth.getCurrentUser().getUid());

        startActivity(intent);
        finish(); // Finish the current activity to prevent going back to the login screen
    }
}
