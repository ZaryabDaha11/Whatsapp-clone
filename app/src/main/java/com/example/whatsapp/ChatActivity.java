package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.whatsapp.Adapters.MessagesAdapter;
import com.example.whatsapp.Models.Message;
import com.example.whatsapp.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String senderRoom,receiverRoom;
    ArrayList<Message> messageArrayList;
    MessagesAdapter adapter;
    FirebaseStorage storage;
    ProgressDialog dialog;
    String senderId,receiverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        messageArrayList = new ArrayList<>();
        adapter = new MessagesAdapter(this,messageArrayList);

        getSupportActionBar().hide();

        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRecyclerView.setAdapter(adapter);

                                            //getting user data from main screen
        String name = getIntent().getStringExtra("name");
        receiverId = getIntent().getStringExtra("uid");
        String profilePic = getIntent().getStringExtra("profileImage");
        String number = getIntent().getStringExtra("number");
        String token = getIntent().getStringExtra("token");
        senderId = auth.getUid();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending...");
        dialog.setCancelable(false);

        binding.chatterName.setText(name);
        Glide.with(this).load(profilePic)
                .placeholder(R.drawable.ooooprofile)
                .into(binding.userChatImage);

                                                    //showing users profile
        binding.chatterinfobox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this , ProfileViewer.class);
                intent.putExtra("name", name );
                intent.putExtra("profileImage", profilePic);
                intent.putExtra("number", number);
                startActivity(intent);
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

                                            //showing users online/offline status on top
        database.getReference().child("Presence").child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String  onlineStatus = snapshot.getValue(String.class);
                    if (!onlineStatus.isEmpty()) {
                        binding.onlineShows.setText(onlineStatus);
                        binding.onlineShows.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        senderRoom = senderId + receiverId;
        receiverRoom = receiverId + senderId;

        database.getReference().child("Chats")
                .child(senderRoom)
                .child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                messageArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);
                    messageArrayList.add(message);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

                                            //sending message
        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt = binding.messageTyper.getText().toString();
                if (messageTxt.isEmpty()){
                    Toast.makeText(ChatActivity.this, "Enter Valid Message", Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.messageTyper.setText("");

                Date date = new Date();
                Message message = new Message(messageTxt,senderId,date.getTime());

                String randomKey = database.getReference().push().getKey();

                HashMap<String, Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastMsg", message.getMessage());
                lastMsgObj.put("lastMsgTime", date.getTime());

                database.getReference().child("Chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("Chats").child(receiverRoom).updateChildren(lastMsgObj);

                database.getReference().child("Chats")
                        .child(senderRoom)
                        .child("Messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("Chats")
                                .child(receiverRoom)
                                .child("Messages")
                                .child(randomKey)
                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                sendNotification(name, message.getMessage(), token);
                            }
                        });
                    }
                });
            }
        });

                            //sending images/videos
        binding.itemSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,26);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 26){
            if (data != null){
                if (data.getData() != null){
                    Uri selectedItem = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("Chats").child(calendar.getTimeInMillis()+ "");
                    dialog.show();
                    reference.putFile(selectedItem).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filePath = uri.toString();

                                        String messageTxt = binding.messageTyper.getText().toString();
                                        binding.messageTyper.setText("");

                                        Date date = new Date();
                                        Message message = new Message(messageTxt,senderId,date.getTime());
                                        message.setImageUrl(filePath);
                                        message.setMessage("Item");

                                        String randomKey = database.getReference().push().getKey();

                                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMsg", message.getMessage());
                                        lastMsgObj.put("lastMsgTime", date.getTime());

                                        database.getReference().child("Chats").child(senderRoom).updateChildren(lastMsgObj);
                                        database.getReference().child("Chats").child(receiverRoom).updateChildren(lastMsgObj);

                                        database.getReference().child("Chats")
                                                .child(senderRoom)
                                                .child("Messages")
                                                .child(randomKey)
                                                .setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                database.getReference().child("Chats")
                                                        .child(receiverRoom)
                                                        .child("Messages")
                                                        .child(randomKey)
                                                        .setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {

                                                    }
                                                });
                                            }
                                        });
                                    }
                                });

                            }

                        }

                    });

                                                        //setting users online/offline status
                    final Handler handler = new Handler();
                    binding.messageTyper.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            database.getReference().child("Presence").child(senderId).setValue("typing...");
                            handler.removeCallbacksAndMessages(null);
                            handler.postDelayed(userStopTyping,2000);

                        }
                        Runnable userStopTyping = new Runnable() {
                            @Override
                            public void run() {
                                database.getReference().child("Presence").child(senderId).setValue("Online");
                            }
                        };
                    });
                }
            }
        }
    }

                                            //sending notification
    void sendNotification(String name, String message, String token){
        try {

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://fcm.googleapis.com/fcm/send";

        JSONObject data = new JSONObject();
        data.put("title", name);
        data.put("body", message);

        JSONObject notificationData = new JSONObject();
        notificationData.put("notification", data);
        notificationData.put("to", token);

            JsonObjectRequest objectRequest = new JsonObjectRequest(url, notificationData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    Toast.makeText(ChatActivity.this, "success", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ChatActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    String key = "key=AAAAQ3QrW_M:APA91bH1mNjlbgrDQAzBTxYJ5UbaQ2aZXsUFwoSqJP_ku4Z_06yO1rX9mBpTJ0HTx3otwej-eCNB84kqWITptrN4QKKOlbgNI1hIn8ZIHHAlrGvy-gRABuAXeSVy8n7YXclHw0WNHmnL";
                    map.put("Authorization", key);
                    map.put("Content-type","application/json");

                    return map;
                }
            };
            queue.add(objectRequest);

        }catch (Exception e){

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}