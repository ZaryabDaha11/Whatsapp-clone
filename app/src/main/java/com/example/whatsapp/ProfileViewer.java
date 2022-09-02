package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.whatsapp.databinding.ActivityProfileViewerBinding;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class ProfileViewer extends AppCompatActivity {

    ActivityProfileViewerBinding binding;
    FirebaseDatabase database;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        getSupportActionBar().hide();

                                                //getting data from previous activity
        String name = getIntent().getStringExtra("name");
        String profilePic = getIntent().getStringExtra("profileImage");
        String number = getIntent().getStringExtra("number");


        binding.name.setText(name);
        binding.number.setText(number);
        Glide.with(this).load(profilePic)
                .placeholder(R.drawable.ooooprofile)
                .into(binding.image);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

                                        //showing image
        binding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileViewer.this,ImageViewer.class);
                intent.putExtra("image", profilePic);
                startActivity(intent);
            }
        });
    }
}