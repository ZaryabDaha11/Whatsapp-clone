package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.whatsapp.databinding.ActivityImageViewerBinding;

public class ImageViewer extends AppCompatActivity {

    ActivityImageViewerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

                                        //getting image from previous activity
        String image = getIntent().getStringExtra("image");

                                        // setting image on activity
        Glide.with(this).load(image)
                .placeholder(R.drawable.ooooprofile)
                .into(binding.image);

                                        //going back
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}