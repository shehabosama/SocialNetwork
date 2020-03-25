package com.example.shehab.socialnetwork;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>
{
    private List<Messages> usermessagelist;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseReference;

    public MessagesAdapter(List<Messages> usermessagelist)
    {
        this.usermessagelist = usermessagelist;
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        TextView senderMessageText;
        TextView receiverMessageText;
        CircleImageView receiverProfileImage;

        public MessageViewHolder(View itemView) {
            super(itemView);

            senderMessageText=(TextView)itemView.findViewById(R.id.text_sender_message);
            receiverMessageText=(TextView)itemView.findViewById(R.id.text_receiver_message);
            receiverProfileImage=(CircleImageView)itemView.findViewById(R.id.image_profile_custom_chat);

        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_chat_activtiy,parent,false);

        mAuth= FirebaseAuth.getInstance();


        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position)
    {
        String messageSenderId=mAuth.getCurrentUser().getUid();

        Messages messages=usermessagelist.get(position);

        String fromuserId=messages.getFrom();
        String frommessagestype=messages.getType();

        userDatabaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(fromuserId);
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String image =dataSnapshot.child("profilimage").getValue().toString();

                    Picasso.with(holder.receiverProfileImage.getContext()).load(image)
                           .placeholder(R.drawable.backgroundprof)
                            .into(holder.receiverProfileImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if(frommessagestype.equals("text"))
        {
            holder.receiverMessageText.setVisibility(View.INVISIBLE);
            holder.receiverProfileImage.setVisibility(View.INVISIBLE);

            if (fromuserId.equals(messageSenderId))
            {
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_message_bacground);
                holder.senderMessageText.setTextColor(Color.WHITE);
                holder.senderMessageText.setGravity(Gravity.LEFT);
                holder.senderMessageText.setText(messages.getMessage());
            }
            else
            {
                holder.senderMessageText.setVisibility(View.INVISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverProfileImage.setVisibility(View.VISIBLE);


                holder.receiverMessageText.setBackgroundResource(R.drawable.reseiver_message_background);
                holder.receiverMessageText.setTextColor(Color.WHITE);
                holder.receiverMessageText.setGravity(Gravity.LEFT);
                holder.receiverMessageText.setText(messages.getMessage());
            }

        }

    }

    @Override
    public int getItemCount() {
        return usermessagelist.size();
    }
}
