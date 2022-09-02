package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.whatsapp.databinding.ActivityWelcomeScreen2Binding;
import com.google.firebase.auth.FirebaseAuth;

public class WelcomeScreen2 extends AppCompatActivity {
    ActivityWelcomeScreen2Binding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeScreen2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


                    // if current user is already in database then login him
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null){
            Intent intent = new Intent(this , MainActivity.class);
            startActivity(intent);
            finish();
        }

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeScreen2.this, PhoneVerify.class));
            }
        });
    }
}