package com.example.shehab.socialnetwork;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesActivity extends AppCompatActivity {



    private RecyclerView myFriendList;
    private DatabaseReference FriendsReference;
    private DatabaseReference UsersReference;

    private FirebaseAuth mAuth;
    String online_user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        myFriendList=(RecyclerView)findViewById(R.id.friends_list);

        mAuth= FirebaseAuth.getInstance();


        online_user_id= mAuth.getCurrentUser().getUid();

        FriendsReference= FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        FriendsReference.keepSynced(true);

        UsersReference=FirebaseDatabase.getInstance().getReference().child("Users");

        myFriendList.setLayoutManager(new LinearLayoutManager(this));


    }



    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Friends,FriendsActivity.FriendsViewHolder> firebaseRecyclerAdapter
                =new FirebaseRecyclerAdapter<Friends, FriendsActivity.FriendsViewHolder>(
                Friends.class,
                R.layout.all_users_display_layout,
                FriendsActivity.FriendsViewHolder.class,
                FriendsReference
        ) {
            @Override
            protected void populateViewHolder(final FriendsActivity.FriendsViewHolder viewHolder, Friends model, int position) {



                viewHolder.setDate(model.getDate());



                final String list_user_id=getRef(position).getKey();
                UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("online")){

                            String online_status=(String)dataSnapshot.child("online").getValue().toString();

                            // viewHolder.setUseronline(online_status);
                        }

                        final String name=dataSnapshot.child("fullName").getValue().toString();
                        String thumbImage=dataSnapshot.child("profilimage").getValue().toString();


                        viewHolder.setUserName(name);
                        viewHolder.setThumbImage(thumbImage,getBaseContext());


                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {






                                            Intent messageIntnent=new Intent(getApplicationContext(),ChatActivity.class);
                                            messageIntnent.putExtra("Visituserid",list_user_id);
                                            messageIntnent.putExtra("userName",name);
                                            startActivity(messageIntnent);



                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        myFriendList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setDate(String date){
            TextView sinceFriendsDate=(TextView)mView.findViewById(R.id.all_users_status);
            sinceFriendsDate.setText("Friends since:\n "+date);
        }

        public  void setUserName(String userName){
            TextView userNameDisplay=(TextView)mView.findViewById(R.id.all_users_username);
            userNameDisplay.setText(userName);
        }


        public  void setThumbImage(final String thumbImage, final Context ctx) {
            final CircleImageView thumb_image=(CircleImageView)mView.findViewById(R.id.all_users_profile_image);


            Picasso.with(ctx).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(thumb_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(thumbImage).into(thumb_image);


                        }
                    });

        }

        public void setUseronline(String  online_status) {


            // ImageView online_statusView=(ImageView)mView.findViewById(R.id.online_status);

            //   if(online_status==true){

            //    online_statusView.setVisibility(View.VISIBLE);
            //  }else{

            //  online_statusView.setVisibility(View.INVISIBLE);
            //}

        }
    }



}
