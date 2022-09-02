package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.example.whatsapp.Adapters.MessagesAdapter;
import com.example.whatsapp.Adapters.UsersAdapter;
import com.example.whatsapp.Models.Message;
import com.example.whatsapp.Models.User;
import com.example.whatsapp.databinding.ActivityNewChatBinding;
import com.example.whatsapp.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class NewChatActivity extends AppCompatActivity {

    ActivityNewChatBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ArrayList<User> users;
    UsersAdapter usersAdapter;
    String senderRoom,receiverRoom,senderId,receiverId;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        users = new ArrayList<>();

        senderId = auth.getUid();
        receiverId = user.getUid();
        senderRoom = senderId + receiverId;
        receiverRoom = receiverId + senderId;

        usersAdapter = new UsersAdapter(this, users);
        binding.menuRecyclerView.setAdapter(usersAdapter);

        database.getReference().child("Users").child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    user.setUid(snapshot1.getKey());
//                    if (!user.getUid().equals(FirebaseAuth.getInstance().getUid()))
//                    if (!user.getPhoneNumber().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                        users.add(user);
                }
                usersAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}