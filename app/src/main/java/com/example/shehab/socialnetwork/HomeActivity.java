package com.example.shehab.socialnetwork;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private DatabaseReference FriendsReference;
    private FirebaseAuth mAuth;
    private String Current_user_id;
    private DatabaseReference postRef,LikesRef;
    private RecyclerView homistpost;
    Boolean Likecheker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth=FirebaseAuth.getInstance();
        Current_user_id=mAuth.getCurrentUser().getUid();

        FriendsReference= FirebaseDatabase.getInstance().getReference().child("Friends").child(Current_user_id);
        postRef=FirebaseDatabase.getInstance().getReference().child("posts");

        homistpost=(RecyclerView)findViewById(R.id.homlistPost);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        homistpost.setLayoutManager(linearLayoutManager);

        Displayalluserposts();


    }




    private void Displayalluserposts()
    {

        String resever=FriendsReference.getKey();
        Toast.makeText(this, ""+resever, Toast.LENGTH_SHORT).show();


        if(Current_user_id.equals(resever)) {
            Query sortpostDescending = postRef.orderByChild("counter");


            FirebaseRecyclerAdapter<Posts, HomeActivity.PostsViewHolder> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<Posts, HomeActivity.PostsViewHolder>(
                            Posts.class,
                            R.layout.all_post_layout,
                            HomeActivity.PostsViewHolder.class,
                            sortpostDescending
                    ) {
                        @Override
                        protected void populateViewHolder(HomeActivity.PostsViewHolder viewHolder, Posts model, int position) {

                            final String idlist = getRef(position).getKey();
                            viewHolder.postprofileImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent vistActivityIntent = new Intent(getApplicationContext(), VisitProfileActivity.class);
                                    vistActivityIntent.putExtra("postkey", idlist);
                                    startActivity(vistActivityIntent);
                                }
                            });
                            viewHolder.name.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent vistActivityIntent = new Intent(getApplicationContext(), VisitProfileActivity.class);
                                    vistActivityIntent.putExtra("postkey", idlist);
                                    startActivity(vistActivityIntent);
                                }
                            });
                            viewHolder.setFullname(model.getFullname());
                            viewHolder.setPostprofileimag(getBaseContext(), model.getPostprofileimag());
                            viewHolder.setDate(model.getDate());
                            viewHolder.setTime(model.getTime());
                            viewHolder.setDescription(model.getDescription());
                            viewHolder.setPostimage(getBaseContext(), model.getPostimage());
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent onClickIntent = new Intent(getApplicationContext(), ClickPostActivity.class);
                                    onClickIntent.putExtra("postkey", idlist);
                                    startActivity(onClickIntent);
                                }
                            });

                            viewHolder.Commentpostbutton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent onClickIntent = new Intent(getApplicationContext(), CommentActivity.class);
                                    onClickIntent.putExtra("postkey", idlist);
                                    startActivity(onClickIntent);
                                }
                            });
                            viewHolder.setLikebuttonstatus(idlist);
                            viewHolder.Likepostbutton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Likecheker = true;

                                    LikesRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (Likecheker.equals(true)) {
                                                if (dataSnapshot.child(idlist).hasChild(Current_user_id)) {
                                                    LikesRef.child(idlist).child(Current_user_id).removeValue();
                                                    Likecheker = false;
                                                } else {
                                                    LikesRef.child(idlist).child(Current_user_id).setValue(true);
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
            homistpost.setAdapter(firebaseRecyclerAdapter);
        }
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
                        Displaynumoflike.setText(Integer.toString(countLikes));
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
