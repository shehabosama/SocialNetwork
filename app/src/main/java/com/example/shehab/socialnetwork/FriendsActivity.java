package com.example.shehab.socialnetwork;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView myFriendList;
    private DatabaseReference FriendsReference;
    private DatabaseReference UsersReference;

    private FirebaseAuth mAuth;
    String online_user_id;
    private View mymainView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);


        myFriendList=(RecyclerView)findViewById(R.id.friends_list);

        mAuth=FirebaseAuth.getInstance();


        online_user_id= mAuth.getCurrentUser().getUid();

        FriendsReference= FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        FriendsReference.keepSynced(true);

        UsersReference=FirebaseDatabase.getInstance().getReference().child("Users");

        myFriendList.setLayoutManager(new LinearLayoutManager(this));


    }

    public void userUpdateStatus(String status)
    {

        String savecurrentdate,savecurrenttime;

        Calendar callForDate=Calendar.getInstance();
        SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
        savecurrentdate=currentdate.format(callForDate.getTime());


        Calendar callForTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
        savecurrenttime=currentTime.format(callForTime.getTime());

        Map currentuserstate = new HashMap();
        currentuserstate.put("date",savecurrentdate);
        currentuserstate.put("time",savecurrenttime);
        currentuserstate.put("type",status);

        UsersReference.child(online_user_id).child("user_status")
                .updateChildren(currentuserstate);


    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> firebaseRecyclerAdapter
                =new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.all_users_display_layout,
                FriendsViewHolder.class,
                FriendsReference
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {



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

                                CharSequence option[]=new CharSequence[]{
                                        name +"'s Profile",
                                        "send message"
                                };

                                AlertDialog.Builder builder =new AlertDialog.Builder(FriendsActivity.this);
                                builder.setTitle("Select option");
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(which==0){
                                            Intent profileIntent=new Intent(getApplicationContext(),PersonProfileActivty.class);
                                            profileIntent.putExtra("Visituserid",list_user_id);
                                            startActivity(profileIntent);
                                        }
                                        if(which==1){

                                            Intent messageIntnent=new Intent(getApplicationContext(),ChatActivity.class);
                                            messageIntnent.putExtra("Visituserid",list_user_id);
                                            messageIntnent.putExtra("userName",name);
                                            startActivity(messageIntnent);
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
