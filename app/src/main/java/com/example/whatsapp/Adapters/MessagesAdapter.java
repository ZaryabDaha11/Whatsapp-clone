package com.example.whatsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.ImageViewer;
import com.example.whatsapp.Models.Message;
import com.example.whatsapp.R;
import com.example.whatsapp.databinding.MessageRecievedBinding;
import com.example.whatsapp.databinding.MessageSentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MessagesAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message> messageArrayList;
    int ITEM_SENT = 1, ITEM_RECEIVED = 2;

    public MessagesAdapter(Context context, ArrayList<Message> messageArrayList) {
        this.context = context;
        this.messageArrayList = messageArrayList;
    }


    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.message_sent, parent, false);
            return new SenderViewHolder(view);
        }
        else
            {
            View view = LayoutInflater.from(context).inflate(R.layout.message_recieved, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

        Message message = messageArrayList.get(position);
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            if (message.getMessage().equals("Item")){
                viewHolder.binding.chatSendImage.setVisibility(View.VISIBLE);
                viewHolder.binding.sentMessage.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl())
                        .placeholder(R.drawable.textboxoutline).into(viewHolder.binding.chatSendImage);

            }
            viewHolder.binding.sentMessage.setText(message.getMessage());

            Long time = message.getTimestamp();

            Long msgTime = Long.parseLong(time.toString());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

            String lMT = simpleDateFormat.format(msgTime);
            viewHolder.binding.messageTime.setText(lMT);

            viewHolder.binding.chatSendImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImageViewer.class);
                    intent.putExtra("image", message.getImageUrl());
                    context.startActivity(intent);
                }
            });
        }
        else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            if (message.getMessage().equals("Item")){
                viewHolder.binding.chatSendImage.setVisibility(View.VISIBLE);
                viewHolder.binding.receivedMessage.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl())
                        .placeholder(R.drawable.textboxoutline).into(viewHolder.binding.chatSendImage);
            }
            viewHolder.binding.receivedMessage.setText(message.getMessage());

            Long time = message.getTimestamp();

            Long msgTime = Long.parseLong(time.toString());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

            String lMT = simpleDateFormat.format(msgTime);
            viewHolder.binding.messageTime.setText(lMT);

            viewHolder.binding.chatSendImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImageViewer.class);
                    intent.putExtra("image", message.getImageUrl());
                    context.startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getSenderId())){
            return ITEM_SENT;
        }
        else
        {
            return ITEM_RECEIVED;
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder{
        MessageSentBinding binding;
        public SenderViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = MessageSentBinding.bind(itemView);
        }
    }
    public class ReceiverViewHolder extends RecyclerView.ViewHolder{
        MessageRecievedBinding binding;
        public ReceiverViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = MessageRecievedBinding.bind(itemView);
        }
    }
}
