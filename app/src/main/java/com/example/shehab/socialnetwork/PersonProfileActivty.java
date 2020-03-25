package com.example.shehab.socialnetwork;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivty extends AppCompatActivity {

    private TextView userName,userProfilename,userStatus,userCountry,userDob,userGender,user_relationship;
    private CircleImageView userProfileImage;
    private DatabaseReference FriendRequestReference,vistRef;
    private String sender_user_id;
    private FirebaseAuth mAuth;
    private String receiver_user_id;
    private String CURRUNT_STATE;
    private Button SendFriendRequestButton,declineFriendRequestButton;
    private DatabaseReference FriendsReference,NotificationReference;
    private RecyclerView post_List;
    String currentuserid;
    private DatabaseReference LikesRef, postRef;
    private Toolbar mtoolbar;
    Boolean Likecheker = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile_activty);
        mAuth= FirebaseAuth.getInstance();

        receiver_user_id =getIntent().getExtras().getString("Visituserid").toString();
        sender_user_id=mAuth.getCurrentUser().getUid();

        vistRef= FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestReference=FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        FriendsReference=FirebaseDatabase.getInstance().getReference().child("Friends");
        NotificationReference=FirebaseDatabase.getInstance().getReference().child("Notification");

        currentuserid=mAuth.getCurrentUser().getUid();

        postRef= FirebaseDatabase.getInstance().getReference().child("posts");
        LikesRef=FirebaseDatabase.getInstance().getReference().child("Likes");


        userName=(TextView)findViewById(R.id.user_name_profile);
        userProfilename=(TextView)findViewById(R.id.user_name);
        userStatus=(TextView)findViewById(R.id.user_status_profile);
        userCountry=(TextView)findViewById(R.id.user_country_profile);
        userDob=(TextView)findViewById(R.id.user_dob_profile);
        userGender=(TextView)findViewById(R.id.user_gender_profile);
        user_relationship=(TextView)findViewById(R.id.user_relation_profile);
        userProfileImage=(CircleImageView)findViewById(R.id.profile_image);
        SendFriendRequestButton=(Button)findViewById(R.id.send_friend_request);
        declineFriendRequestButton=(Button)findViewById(R.id.Decline_Friend_Request);

        post_List=(RecyclerView)findViewById(R.id.all_users_post_list);
        post_List.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        post_List.setLayoutManager(linearLayoutManager);
        Displayalluserposts();

        CURRUNT_STATE="not_friends";





        vistRef.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {

                    String username=dataSnapshot.child("userName").getValue().toString();
                    String userprofilename=dataSnapshot.child("fullName").getValue().toString();
                    String userstatus=dataSnapshot.child("status").getValue().toString();
                    String usergender=dataSnapshot.child("gender").getValue().toString();
                    String usercountry=dataSnapshot.child("country").getValue().toString();
                    String userdob=dataSnapshot.child("dob").getValue().toString();
                    String userrelationsip=dataSnapshot.child("relationship").getValue().toString();
                    String profileImage=dataSnapshot.child("profilimage").getValue().toString();
                    userName.setText(userprofilename);
                    userStatus.setText(userstatus);
                    userGender.setText("gender: "+usergender);
                    user_relationship.setText("relationship: "+userrelationsip);
                    userCountry.setText("country: "+usercountry);
                    userProfilename.setText(username);
                    userDob.setText("dob: "+userdob);

                    Picasso.with(getBaseContext()).load(profileImage).into(userProfileImage);



                    FriendRequestReference.child(sender_user_id)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(receiver_user_id)){


                                        String rqu_type=dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();
                                        if(rqu_type.equals("sent")){
                                            CURRUNT_STATE="request_sent";
                                            SendFriendRequestButton.setText("cancel friend request");
                                            declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                            declineFriendRequestButton.setEnabled(false);
                                        }
                                        else if(rqu_type.equals("received")){
                                            CURRUNT_STATE="request_received";
                                            SendFriendRequestButton.setText("accept friend request");


                                            declineFriendRequestButton.setVisibility(View.VISIBLE);
                                            declineFriendRequestButton.setEnabled(true);

                                            declineFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    DeclineFriendRequest();
                                                }
                                            });
                                        }


                                    }
                                    else{
                                        FriendsReference.child(sender_user_id)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if(dataSnapshot.hasChild(receiver_user_id)){
                                                            CURRUNT_STATE="friend";
                                                            SendFriendRequestButton.setText("Unfriend this person");

                                                            declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                            declineFriendRequestButton.setEnabled(false);

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                    }
                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                  // saveStatusCancelButton();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        declineFriendRequestButton.setVisibility(View.INVISIBLE);
        declineFriendRequestButton.setEnabled(false);

        declineFriendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if(!sender_user_id.equals(receiver_user_id)){
            SendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SendFriendRequestButton.setEnabled(false);


                    if(CURRUNT_STATE.equals("not_friends")){
                        SendFriendRequestToaPerson();
                    }
                    if(CURRUNT_STATE.equals("request_sent")){
                        CancelFriendRequest();
                    }
                    if (CURRUNT_STATE.equals("request_received")){
                        AcceptFriendRequest();
                    }
                    if(CURRUNT_STATE.equals("friend")){
                        UnFriendaFriend();

                    }
                }
            });
        }else{
            declineFriendRequestButton.setVisibility(View.INVISIBLE);
            SendFriendRequestButton.setVisibility(View.INVISIBLE);
        }

    }



    private void DeclineFriendRequest() {

        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                SendFriendRequestButton.setEnabled(true);
                                                CURRUNT_STATE="not_friends";
                                                SendFriendRequestButton.setText("send friend request");

                                                declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                declineFriendRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }


                    }
                });



    }


    private void UnFriendaFriend() {


        FriendsReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            FriendsReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                SendFriendRequestButton.setEnabled(true);
                                                CURRUNT_STATE="not_friends";
                                                SendFriendRequestButton.setText("send friend request");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void AcceptFriendRequest() {
        Calendar calForeDate=Calendar.getInstance();
        SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
        final String SaveCurrentDate=currentdate.format(calForeDate.getTime());

        FriendsReference.child(sender_user_id).child(receiver_user_id).child("date").setValue(SaveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        FriendsReference.child(receiver_user_id).child(sender_user_id).child("date").setValue(SaveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if (task.isSuccessful()){
                                                                                SendFriendRequestButton.setEnabled(true);
                                                                                CURRUNT_STATE="friend";
                                                                                SendFriendRequestButton.setText("Unfriend this person");
                                                                                declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                                                declineFriendRequestButton.setEnabled(false);
                                                                            }
                                                                        }
                                                                    });
                                                        }


                                                    }
                                                });
                                    }
                                });
                    }
                });


    }








    private void CancelFriendRequest() {

        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                SendFriendRequestButton.setEnabled(true);
                                                CURRUNT_STATE="not_friends";
                                                SendFriendRequestButton.setText("send friend request");

                                                declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                declineFriendRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }


                    }
                });

    }






    private void SendFriendRequestToaPerson() {

        FriendRequestReference.child(sender_user_id).child(receiver_user_id)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            FriendRequestReference.child(receiver_user_id).child(sender_user_id)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                HashMap<String,String> notification=new HashMap<String, String>();
                                                notification.put("from",sender_user_id);
                                                notification.put("type","request");

                                                NotificationReference.child(receiver_user_id).push().setValue(notification)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    SendFriendRequestButton.setEnabled(true);
                                                                    CURRUNT_STATE="request_sent";
                                                                    SendFriendRequestButton.setText("Cancel Friend request");

                                                                    declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                                    declineFriendRequestButton.setEnabled(false);
                                                                }

                                                            }
                                                        });




                                            }


                                        }
                                    });

                        }


                    }
                });

    }



    private void Displayalluserposts()
    {

        Query mypost=postRef.orderByChild("uid").startAt(receiver_user_id)
                .endAt(receiver_user_id +"\uf8ff");


        FirebaseRecyclerAdapter<Posts,MainActivity.PostsViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Posts, MainActivity.PostsViewHolder>(
                        Posts.class,
                        R.layout.all_post_layout,
                        MainActivity.PostsViewHolder.class,
                        mypost
                )
                {
                    @Override
                    protected void populateViewHolder(MainActivity.PostsViewHolder viewHolder, Posts model, int position) {

                        final String idlist=getRef(position).getKey();
                        viewHolder.postprofileImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent vistActivityIntent=new Intent(getApplicationContext(),VisitProfileActivity.class);
                                vistActivityIntent.putExtra("postkey",idlist);
                                startActivity(vistActivityIntent);
                            }
                        });
                        viewHolder.name.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent vistActivityIntent=new Intent(getApplicationContext(),VisitProfileActivity.class);
                                vistActivityIntent.putExtra("postkey",idlist);
                                startActivity(vistActivityIntent);
                            }
                        });
                        viewHolder.setFullname(model.getFullname());
                        viewHolder.setPostprofileimag(getBaseContext(),model.getPostprofileimag());
                        viewHolder.setDate(model.getDate());
                        viewHolder.setTime(model.getTime());
                        viewHolder.setDescription(model.getDescription());
                        viewHolder.setPostimage(getBaseContext(),model.getPostimage());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent onClickIntent=new Intent(getApplicationContext(),ClickPostActivity.class);
                                onClickIntent.putExtra("postkey",idlist);
                                startActivity(onClickIntent);
                            }
                        });

                        viewHolder.Commentpostbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent onClickIntent=new Intent(getApplicationContext(),CommentActivity.class);
                                onClickIntent.putExtra("postkey",idlist);
                                startActivity(onClickIntent);
                            }
                        });
                        viewHolder.setLikebuttonstatus(idlist);
                        viewHolder.Likepostbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                Likecheker = true;

                                LikesRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        if(Likecheker.equals(true))
                                        {
                                            if (dataSnapshot.child(idlist).hasChild(currentuserid))
                                            {
                                                LikesRef.child(idlist).child(currentuserid).removeValue();
                                                Likecheker=false;
                                            }
                                            else
                                            {
                                                LikesRef.child(idlist).child(currentuserid).setValue(true);
                                                Likecheker=false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        });
                    }
                };
        post_List.setAdapter(firebaseRecyclerAdapter);
    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {

        View mView;
        ImageButton Likepostbutton,Commentpostbutton;
        TextView Displaynumoflike;
        DatabaseReference LikesRef,postcommentRef;
        String currentuserid;
        CircleImageView postprofileImage;
        TextView name;
        int countLikes;



        public PostsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;


            postprofileImage=(CircleImageView) mView.findViewById(R.id.postprofileImage);
            name=(TextView)mView.findViewById(R.id.postfullname);
            Likepostbutton=(ImageButton)mView.findViewById(R.id.likepostbuttom);
            Commentpostbutton=(ImageButton)mView.findViewById(R.id.commentpostbutton);
            Displaynumoflike=(TextView)mView.findViewById(R.id.display_num_of_button);

            LikesRef=FirebaseDatabase.getInstance().getReference().child("Likes");

            currentuserid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        public void setLikebuttonstatus(final String postkey)
        {


            postcommentRef= FirebaseDatabase.getInstance().getReference().child("posts").child(postkey).child("comments");


            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.child(postkey).hasChild(currentuserid)) {

                        countLikes = (int) dataSnapshot.child(postkey).getChildrenCount();
                        Likepostbutton.setImageResource(R.drawable.like);
                        Displaynumoflike.setText(Integer.toString(countLikes)+" Like");
                    }
                    else
                    {
                        countLikes = (int) dataSnapshot.child(postkey).getChildrenCount();
                        Likepostbutton.setImageResource(R.drawable.dislike);
                        Displaynumoflike.setText(Integer.toString(countLikes) +" Like");
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }




        public void setFullname(String fullname)
        {
            name=(TextView)mView.findViewById(R.id.postfullname);
            name.setText(fullname);
        }

        public void setPostprofileimag(Context context, String postprofileimag)
        {
            postprofileImage=(CircleImageView) mView.findViewById(R.id.postprofileImage);

            Picasso.with(context).load(postprofileimag).into(postprofileImage);

        }

        public void setDate(String date)
        {
            TextView textdate=(TextView)mView.findViewById(R.id.textdatepost);
            textdate.setText(date);
        }

        public void setTime(String time)
        {
            TextView textTime=(TextView) mView.findViewById(R.id.textTimepost);

            textTime.setText(time);
        }

        public void setDescription(String description)
        {
            TextView textDescription=(TextView)mView.findViewById(R.id.textdescriptionpost);
            textDescription.setText(description);
        }

        public void setPostimage(Context context,String postimage)
        {
            ImageView postImage=(ImageView)mView.findViewById(R.id.postImage);
            Picasso.with(context).load(postimage).into(postImage);
        }


    }




}
