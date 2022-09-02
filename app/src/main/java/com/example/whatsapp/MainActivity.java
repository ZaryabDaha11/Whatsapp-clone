package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsapp.Adapters.FragmentsAdapter;
import com.example.whatsapp.Adapters.UsersAdapter;
import com.example.whatsapp.Models.User;
import com.example.whatsapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ArrayList<User> users;
    UsersAdapter usersAdapter;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        users = new ArrayList<>();

        binding.viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }
                                                                //adding users online/offline status
    @Override
    protected void onResume() {

        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("Presence").child(currentId).setValue("Online");
    }

    @Override
    protected void onPause() {
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("Presence").child(currentId).setValue("Offline");
        super.onPause();
    }
                                                        //adding top-right menu/settings
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
                                                        // top-right settings onclick
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.topSetting:
                startActivity(new Intent(this,Settings.class));
                break;

            case R.id.logout:
                auth.signOut();
                startActivity(new Intent(this,WelcomeScreen.class));
                break;

            case R.id.newGroup:
//                startActivity(new Intent(this,NewChatActivity.class));
                break;

            case R.id.newBroadcast:
                startActivity(new Intent(this,NewChatActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}