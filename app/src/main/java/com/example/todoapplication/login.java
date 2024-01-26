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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {
    private static final int REQ_ONE_TAP = 100;
    EditText email,password;
    private FirebaseAuth mAuth;
    private BeginSignInRequest signInRequest;
    private SignInClient oneTapClient;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        email = (EditText) findViewById(R.id.loginemail);
        password = (EditText)findViewById(R.id.loginpassword);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        int flag =  sharedPreferences.getInt("flag", 0);

        mAuth = FirebaseAuth.getInstance();

        // Check if the user is already authenticated
        if (mAuth.getCurrentUser() != null && flag!=1) {
            // User is already logged in, start Home activity
            startHomeActivity();
        }

    }

    public void emailLogin(View view) {

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
                            // Call TO onActivityResult()
                            startIntentSenderForResult(result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
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
    // Results for google and FB SignIn
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // For Google Sign In
        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);

                mAuth.signInWithCredential(GoogleAuthProvider.getCredential(credential.getGoogleIdToken(), null))
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Getting Current Id of user
                                    String uid = mAuth.getCurrentUser().getUid();

                                    Map<String, String> userData = new HashMap<>();
                                    //userData.put("username", usernametxt);
                                    userData.put("email", mAuth.getCurrentUser().getEmail());

                                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                                    DatabaseReference users = db.getReference("users");

                                    users.child(uid).setValue(userData);

                                    // Call to startHomeActivity
                                    startHomeActivity();
                                } else {
                                    Log.w("Google SignIn", "signInWithCredential:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } catch (ApiException e) {
                Log.e("Google SignIn", "API exception: " + e.getStatusCode());
            }
        } else {
            // Facebook CallbackManager
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void facebookLogin(View view) {
        // For FB Login
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        LoginManager.getInstance().logInWithReadPermissions(login.this, Arrays.asList("public_profile"));
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();

                            // Save Facebook ID to Firebase along with other user details
                            String uid = user.getUid();
                            Map<String, String> userData = new HashMap<>();
                            //userData.put("username", usernametxt);

                            userData.put("email", user.getEmail());
                            userData.put("facebook_id", user.getUid()); // Save Facebook ID

                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference users = db.getReference("users");

                            users.child(uid).setValue(userData);


                            // Call to startHomeActivity
                            startActivity(new Intent(login.this, Home.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Facebook SignIn", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void startHomeActivity() {
        Intent intent = new Intent(getApplicationContext(),Home.class);
        intent.putExtra("email", mAuth.getCurrentUser().getEmail());
        intent.putExtra("uid", mAuth.getCurrentUser().getUid());

        startActivity(intent);
        finish(); // Finish the current activity to prevent going back to the login screen
    }
}
