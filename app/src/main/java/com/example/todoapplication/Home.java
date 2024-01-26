package com.example.todoapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

public class Home extends AppCompatActivity {

    TextView useremail,useruid;
    Button logoutbtn;
    private FirebaseAuth mAuth;
    private AccessToken accessToken;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        accessToken = AccessToken.getCurrentAccessToken();

        useremail = findViewById(R.id.useremail);
        useruid = findViewById(R.id.useruid);
        logoutbtn = findViewById(R.id.logoutbtn);

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        try {
                            String name = object.getString("name");
                            useremail.setText("Name:"+name);
                            Log.d("Graph API Response", object.toString());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link");
                request.setParameters(parameters);
                request.executeAsync();

//        useremail.setText("Username = "+getIntent().getStringExtra("email").toString());
//        useruid.setText("UID = "+getIntent().getStringExtra("uid").toString());

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), signup.class));

            }
        });
    }
}

