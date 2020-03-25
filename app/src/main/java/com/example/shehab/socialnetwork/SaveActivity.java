package com.example.shehab.socialnetwork;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SaveActivity extends AppCompatActivity {

    String idpost;
    private DatabaseReference saveref;
    private DatabaseReference Userref;
    private FirebaseAuth mAuth;
    private DatabaseReference postRef;
    String Current_user_id;
    private RecyclerView post_List;
    Boolean Likecheker = false;
    private DatabaseReference LikesRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);


        mAuth = FirebaseAuth.getInstance();
        Current_user_id = mAuth.getCurrentUser().getUid();

        postRef = FirebaseDatabase.getInstance().getReference().child("saveposts").child(Current_user_id);
        LikesRef=FirebaseDatabase.getInstance().getReference().child("Likes");
        Userref= FirebaseDatabase.getInstance().getReference().child("Users");
        post_List=(RecyclerView)findViewById(R.id.all_users_post_list);
        post_List.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        post_List.setLayoutManager(linearLayoutManager);


        Displayalluserposts();


    }



    private void Displayalluserposts()
    {


        Query sortpostDescending=postRef.orderByChild("counter");


        FirebaseRecyclerAdapter<Posts,SaveActivity.PostsViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Posts, SaveActivity.PostsViewHolder>(
                        Posts.class,
                        R.layout.all_post_layout,
                        SaveActivity.PostsViewHolder.class,
                        sortpostDescending
                )
                {
                    @Override
                    protected void populateViewHolder(SaveActivity.PostsViewHolder viewHolder, Posts model, int position) {

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
                        viewHolder.share_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent topostActivity = new Intent(getApplicationContext(),PostActivity.class);
                                topostActivity.putExtra("postkey",idlist);
                                startActivity(topostActivity);
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

                        viewHolder.saveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                postRef.child(idlist).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        String uid = dataSnapshot.child("uid").getValue().toString();
                                        String date = dataSnapshot.child("date").getValue().toString();
                                        String time  = dataSnapshot.child("time").getValue().toString();
                                        String imageprofile = dataSnapshot.child("postimage").getValue().toString();
                                        String imagepost = dataSnapshot.child("postprofileimag").getValue().toString();
                                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                                        String counter = dataSnapshot.child("counter").getValue().toString();
                                        String discription = dataSnapshot.child("description").getValue().toString();

                                        HashMap postMap = new HashMap();
                                        postMap.put("uid", uid);
                                        postMap.put("date", date);
                                        postMap.put("time", time);
                                        postMap.put("description", discription);
                                        postMap.put("postimage", imagepost);
                                        postMap.put("postprofileimag", imageprofile);
                                        postMap.put("fullname", fullname);
                                        postMap.put("counter", counter);

                                        saveref.child(Current_user_id).child(idlist)
                                                .updateChildren(postMap)
                                                .addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {

                                                        if(task.isSuccessful())
                                                        {
                                                            Toast.makeText(SaveActivity.this, "post saved successfully....", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

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

                        viewHolder.Displaynumoflike.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent LikeDisplayInetnt=new Intent(getApplicationContext(),LikeActivity.class);
                                LikeDisplayInetnt.putExtra("LikeDisplay",idlist);
                                startActivity(LikeDisplayInetnt);
                            }
                        });
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
                                            if (dataSnapshot.child(idlist).hasChild(Current_user_id))
                                            {
                                                LikesRef.child(idlist).child(Current_user_id).removeValue();
                                                Likecheker=false;
                                            }
                                            else
                                            {


                                                LikesRef.child(idlist).child(Current_user_id).child("Users").setValue(true)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                {



                                                                    Userref.child(Current_user_id).addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                                            if (dataSnapshot.exists()) {


                                                                                String name = dataSnapshot.child("fullName").getValue().toString();
                                                                                String profilImage = dataSnapshot.child("profilimage").getValue().toString();
                                                                                HashMap userMap=new HashMap();
                                                                                userMap.put("userName",name);
                                                                                userMap.put("fullName",name);
                                                                                userMap.put("country","country");
                                                                                userMap.put("status","hey there , i am using the social network app ,developed by shehab osama");
                                                                                userMap.put("gender","none");
                                                                                userMap.put("dob","none");
                                                                                userMap.put("relationship","none");
                                                                                userMap.put("profilimage",profilImage);
                                                                                LikesRef.child(idlist).child(Current_user_id).updateChildren(userMap)
                                                                                        .addOnCompleteListener(new OnCompleteListener() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task task) {
                                                                                                if (task.isSuccessful())
                                                                                                {
                                                                                                    Toast.makeText(SaveActivity.this, "done", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                        }
                                                                    });

                                                                }
                                                            }
                                                        });

                                                Likecheker = false;





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
        DatabaseReference LikesRef,postcommentRef,UserRef;
        String currentuserid;
        CircleImageView postprofileImage;
        TextView name;
        int countLikes;
        ImageButton share_button;
        ImageButton saveButton;




        public PostsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;


            postprofileImage=(CircleImageView) mView.findViewById(R.id.postprofileImage);
            name=(TextView)mView.findViewById(R.id.postfullname);
            Likepostbutton=(ImageButton)mView.findViewById(R.id.likepostbuttom);
            Commentpostbutton=(ImageButton)mView.findViewById(R.id.commentpostbutton);
            Displaynumoflike=(TextView)mView.findViewById(R.id.display_num_of_button);
            share_button = (ImageButton)mView.findViewById(R.id.sharepost);
            saveButton = (ImageButton)mView.findViewById(R.id.savepost);

            LikesRef=FirebaseDatabase.getInstance().getReference().child("Likes");
            UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
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
                        UserRef.child(currentuserid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String name=dataSnapshot.child("fullName").getValue().toString();
                                Displaynumoflike.setText(name+" and "+Integer.toString(countLikes-1)+" other Like post");

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
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
