package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsapp.databinding.ActivityOtpVerifyBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OtpVerify extends AppCompatActivity {

    ActivityOtpVerifyBinding binding;
    FirebaseAuth auth;
    ProgressDialog dialog1,dialog2;
    private static final String TAG = "OtpVerify";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

                    //getting data from previous activity
        auth = FirebaseAuth.getInstance();
        String userNumber = getIntent().getStringExtra("userNumber");
        String countryCode = getIntent().getStringExtra("countryCode");
        binding.phoneLabel.setText("Verify  " + countryCode + " " + userNumber);

        dialog1 = new ProgressDialog(OtpVerify.this);
        dialog1.setMessage("Sending OTP...");
        dialog1.setCancelable(false);
        dialog1.show();

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(countryCode + userNumber).setTimeout(60L, TimeUnit.SECONDS).setActivity(OtpVerify.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInUser(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        dialog2.dismiss();
                        Log.d(TAG, "onVerificationFailed: " + e.getLocalizedMessage());
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        dialog1.dismiss();
                        binding.otpVerifyButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog2 = new ProgressDialog(OtpVerify.this);
                                dialog2.setMessage("Verifying OTP...");
                                dialog2.setCancelable(false);
                                dialog2.show();
                                String verificationCode = binding.OtpBox.getText().toString();
                                if (verificationCode.isEmpty()) {
                                    binding.OtpBox.setError("Please Enter Your OTP");
                                } else {
                                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verificationCode);
                                    signInUser(credential);
                                }
                            }
                        });
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void signInUser(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(OtpVerify.this, ProfileSetup.class));
                    finish();
                    dialog2.dismiss();
                } else {
                    Log.d(TAG, "onComplete: " + Objects.requireNonNull(task.getException()).getLocalizedMessage());
                }
            }
        });
    }
}