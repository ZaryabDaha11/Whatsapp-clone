package com.example.whatsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.ChatActivity;
import com.example.whatsapp.Models.User;
import com.example.whatsapp.ProfileViewer;
import com.example.whatsapp.R;
import com.example.whatsapp.databinding.RowBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    Context context;
    ArrayList<User> users;

    public UsersAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;

    }

    public UsersAdapter() {

    }

    @NonNull
    @NotNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UsersAdapter.UsersViewHolder holder, int position) {

        User user = users.get(position);

        String senderId = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderId + user.getUid();

        FirebaseDatabase.getInstance().getReference()
                .child("Chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                            Long time = snapshot.child("lastMsgTime").getValue(Long.class);

                            Long lstMsgTime = Long.parseLong(time.toString());
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

                            String lMT = simpleDateFormat.format(lstMsgTime);

                            holder.binding.lastMessage.setText(lastMsg);
                            holder.binding.lastMessageTime.setVisibility(View.VISIBLE);
                            holder.binding.lastMessageTime.setText(lMT + "");
                        }
                        else {
                            holder.binding.lastMessage.setText("Tap to Chat");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        holder.binding.chatterName.setText(user.getName());

        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.ooooprofile)
                .into(holder.binding.chatImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", user.getName());
                intent.putExtra("uid", user.getUid());
                intent.putExtra("profileImage", user.getProfileImage());
                intent.putExtra("number", user.getPhoneNumber());
                intent.putExtra("token", user.getToken());
                context.startActivity(intent);
            }
        });

        holder.binding.chatImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , ProfileViewer.class);
                intent.putExtra("name", user.getName() );
                intent.putExtra("profileImage", user.getProfileImage());
                intent.putExtra("number", user.getPhoneNumber());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{

        RowBinding binding;

        public UsersViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = RowBinding.bind(itemView);
        }
    }
}
