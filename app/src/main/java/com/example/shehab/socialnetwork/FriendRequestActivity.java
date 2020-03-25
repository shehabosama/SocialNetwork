package com.example.shehab.socialnetwork;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestActivity extends AppCompatActivity {
    private RecyclerView myChatsList;

    private DatabaseReference FriendsReference;
    private DatabaseReference UsersReference;

    private FirebaseAuth mAuth;
    String online_user_id;

    private View myMainView;

    private DatabaseReference FriendsDatabaseRef;
    private DatabaseReference FriendsReqDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);


        myChatsList=(RecyclerView)findViewById(R.id.Request_list);

        mAuth= FirebaseAuth.getInstance();


        online_user_id= mAuth.getCurrentUser().getUid();

        FriendsReference= FirebaseDatabase.getInstance().getReference().child("Friend_Request").child(online_user_id);

        UsersReference=FirebaseDatabase.getInstance().getReference().child("Users");
        FriendsDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsReqDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Friend_Request");

        myChatsList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        myChatsList.setLayoutManager(linearLayoutManager);

    }


    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Friends, FriendRequestActivity.ChatsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Friends, FriendRequestActivity.ChatsViewHolder>(
                Friends.class,
                R.layout.all_users_display_layout,
                FriendRequestActivity.ChatsViewHolder.class,
                FriendsReference
        ) {
            @Override
            protected void populateViewHolder(final FriendRequestActivity.ChatsViewHolder viewHolder, Friends model, int position) {

                final String list_user_id = getRef(position).getKey();

                DatabaseReference get_type_ref=getRef(position).child("request_type").getRef();
                get_type_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            String request_type=dataSnapshot.getValue().toString();
                            if (request_type.equals("received"))
                            {

                                UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {


                                        final String name = dataSnapshot.child("fullName").getValue().toString();
                                        final String thumbImage = dataSnapshot.child("profilimage").getValue().toString();

                                        final String userStatus = dataSnapshot.child("status").getValue().toString();

                                        viewHolder.setUserName(name);
                                        viewHolder.setThumbImage(thumbImage,getBaseContext());

                                        viewHolder.setUserStatus(userStatus);

                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                CharSequence option[]=new CharSequence[]{
                                                        "Accept friend request",
                                                        "Cancel friend request"
                                                };

                                                AlertDialog.Builder builder =new AlertDialog.Builder(FriendRequestActivity.this);
                                                builder.setTitle("Friend request option");
                                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        if(which==0){

                                                            Calendar calForeDate=Calendar.getInstance();
                                                            SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
                                                            final String SaveCurrentDate=currentdate.format(calForeDate.getTime());

                                                            FriendsDatabaseRef.child(online_user_id).child(list_user_id).child("date").setValue(SaveCurrentDate)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {

                                                                            FriendsDatabaseRef.child(list_user_id).child(online_user_id).child("date").setValue(SaveCurrentDate)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

                                                                                            FriendsReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if(task.isSuccessful()){
                                                                                                                FriendsReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                if (task.isSuccessful()){
                                                                                                                                    Toast.makeText(getBaseContext(), "Friend request accept successfully", Toast.LENGTH_SHORT).show();
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
                                                        if(which==1){
                                                            FriendsReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                FriendsReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                if (task.isSuccessful()){
                                                                                                    Toast.makeText(getBaseContext(), "Friend request cancel successfully!", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }


                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            }else if(request_type.equals("sent")){



                                UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {


                                        final String name = dataSnapshot.child("fullName").getValue().toString();
                                        final String thumbImage = dataSnapshot.child("profilimage").getValue().toString();

                                        final String userStatus = dataSnapshot.child("status").getValue().toString();

                                        viewHolder.setUserName(name);
                                        viewHolder.setThumbImage(thumbImage, FriendRequestActivity.this);

                                        viewHolder.setUserStatus(userStatus);


                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {


                                                CharSequence option[]=new CharSequence[]{
                                                        "Cancel friend request",

                                                };

                                                AlertDialog.Builder builder =new AlertDialog.Builder(FriendRequestActivity.this);
                                                builder.setTitle("FriendRequest sent");
                                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        if(which==0){
                                                            FriendsReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                FriendsReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                if (task.isSuccessful()){
                                                                                                    Toast.makeText(getBaseContext(), "Friend request cancel successfully!", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }


                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                                builder.show();

                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };

        myChatsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ChatsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }



        public  void setUserName(String userName){
            TextView userNameDisplay=(TextView)mView.findViewById(R.id.all_users_username);
            userNameDisplay.setText(userName);
        }


        public  void setThumbImage(final String thumbImage, final Context ctx) {
            final CircleImageView thumb_image=(CircleImageView)mView.findViewById(R.id.all_users_profile_image);


        Picasso.with(ctx).load(thumbImage).placeholder(R.drawable.backgroundprof).into(thumb_image);




        }

        public void setUseronline(String  online_status) {


            // ImageView online_statusView=(ImageView)mView.findViewById(R.id.online_status);

            //   if(online_status==true){

            //    online_statusView.setVisibility(View.VISIBLE);
            //  }else{

            //  online_statusView.setVisibility(View.INVISIBLE);
            //}

        }

        public void setUserStatus(String userStatus) {

            TextView user_status=(TextView)mView.findViewById(R.id.all_users_status);
            user_status.setText(userStatus);
        }
    }

}
