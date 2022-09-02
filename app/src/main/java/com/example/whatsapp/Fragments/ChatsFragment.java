package com.example.whatsapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsapp.Adapters.FragmentsAdapter;
import com.example.whatsapp.Adapters.UsersAdapter;
import com.example.whatsapp.Models.User;
import com.example.whatsapp.R;
import com.example.whatsapp.databinding.FragmentChatsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class ChatsFragment extends Fragment {

    FragmentChatsBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ArrayList<User> users;
    UsersAdapter usersAdapter;

    User user;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        users = new ArrayList<>();

        binding = FragmentChatsBinding.inflate(inflater, container, false);
        usersAdapter = new UsersAdapter(getContext(), users);
        binding.menuRecyclerView.setAdapter(usersAdapter);
                                            // adding token/key for notifications on database
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {

                HashMap<String, Object> obj = new HashMap<>();
                obj.put("token", token);

           database.getReference().child("Users").child(auth.getUid()).updateChildren(obj);
            }
        });
                                                    //getting users from database
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
                                        // showing users on screen
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    user.setUid(snapshot1.getKey());
//                    if (!user.getUid().equals(FirebaseAuth.getInstance().getUid()))
                        if (!user.getPhoneNumber().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                        users.add(user);
                }
                usersAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return binding.getRoot();
    }
}