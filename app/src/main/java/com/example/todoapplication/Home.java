package com.example.todoapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class Home extends AppCompatActivity {

    TextView useruid,username;
    Button logoutbtn;
    private FirebaseAuth mAuth;
    private AccessToken accessToken;
    private SharedPreferences sharedPreferences;
    ImageView calender;
    RecyclerView recyclerView;
    private MyAdapter myAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        Toast.makeText(getApplicationContext(),"UID:"+mAuth.getCurrentUser().getUid(),Toast.LENGTH_SHORT).show();
        /*
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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

        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
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


    private void showPopupMenu(View view)
    {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_options, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if(id==R.id.menu_update)
                {
                    Toast.makeText(getApplicationContext(),"Update",Toast.LENGTH_SHORT).show();
                }
                else if (id==R.id.menu_delete)
                {
                    Toast.makeText(getApplicationContext(),"Delete",Toast.LENGTH_SHORT).show();
                }
                else if (id==R.id.menu_delete)
                {
                    Toast.makeText(getApplicationContext(),"Delete",Toast.LENGTH_SHORT).show();
                }
                return true;
            }

        });

        popupMenu.show();
    }
}
