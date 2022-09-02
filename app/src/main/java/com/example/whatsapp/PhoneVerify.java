package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsapp.databinding.ActivityPhoneVerifyBinding;
import com.google.firebase.auth.FirebaseAuth;

public class PhoneVerify extends AppCompatActivity {

    ActivityPhoneVerifyBinding binding;
    FirebaseAuth auth;

    int numberLength = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null){
            Intent intent = new Intent(this , MainActivity.class);
            startActivity(intent);
            finish();
        }

                        //it takes the data to otp activity
        binding.sendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userNumber = binding.PhoneNumber.getText().toString();
                String countryCode = binding.countryCode.getText().toString();
                if (userNumber.isEmpty()) {
                    binding.PhoneNumber.setError("Please Enter Your Number");
                } else if (userNumber.length() < numberLength) {
                    binding.PhoneNumber.setError("Incorrect Number");
                }
                else if (countryCode.isEmpty()){
                    binding.countryCode.setError("Please Enter Your Country Code");
                }
                else if (countryCode.length() > 3) {
                    binding.countryCode.setError("Incorrect Country code");
                }
                    else {
                    Intent intent = new Intent(PhoneVerify.this, OtpVerify.class);
                    intent.putExtra("userNumber", userNumber);
                    intent.putExtra("countryCode", countryCode);
                    startActivity(intent);
                }
            }
        });
    }
}