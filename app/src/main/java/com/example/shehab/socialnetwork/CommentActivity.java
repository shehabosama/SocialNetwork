package com.example.shehab.socialnetwork;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private String postkey;
    private RecyclerView commentList;
    private EditText commentInput;
    private ImageButton postcommentbutton;
    private DatabaseReference Userref,postcommentRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference replaycommentRef;
    Boolean Likecheker = false;
    int countreplay;
    private DatabaseReference LikesRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);


        mAuth=FirebaseAuth.getInstance();

        currentUserId=mAuth.getCurrentUser().getUid();

        postkey=getIntent().getExtras().getString("postkey").toString();


        Userref= FirebaseDatabase.getInstance().getReference().child("Users");
        postcommentRef= FirebaseDatabase.getInstance().getReference().child("posts").child(postkey).child("comments");
        replaycommentRef = FirebaseDatabase.getInstance().getReference().child("posts").child(postkey);
        LikesRef=FirebaseDatabase.getInstance().getReference().child("Likes_comment");

        commentList=(RecyclerView)findViewById(R.id.comment_list);
        commentList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        commentList.setLayoutManager(linearLayoutManager);

        commentInput=(EditText)findViewById(R.id.comment_input_text);
        postcommentbutton=(ImageButton)findViewById(R.id.post_comment_button);

        postcommentbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Userref.child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String username=dataSnapshot.child("userName").getValue().toString();
                        String current_profile_image = dataSnapshot.child("profilimage").getValue().toString();

                        Validatecomment(username,current_profile_image);
                        commentInput.setText("");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();




        FirebaseRecyclerAdapter<Allcomments, CommentActivity.PostsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Allcomments, PostsViewHolder>(
                Allcomments.class,
                R.layout.all_comments_layout,
                CommentActivity.PostsViewHolder.class,
                postcommentRef
        ) {
            @Override
            protected void populateViewHolder(final CommentActivity.PostsViewHolder viewHolder, Allcomments model, final int position) {

                final String idcomment = getRef(position).getKey();



                replaycommentRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            countreplay = (int) dataSnapshot.child("comments").child(idcomment).child("replay_comment").getChildrenCount();

                            if (countreplay > 0)
                            {
                                viewHolder.replaytext.setText(countreplay+" replay");
                            }
                            else
                            {
                                viewHolder.replaytext.setText("replay");

                            }
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                viewHolder.setUsername(model.getUsername());
                viewHolder.setComment(model.getComment());
                viewHolder.setDate( model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setProfilimage(getApplicationContext(),model.getProfilimage());
                viewHolder.replaytext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toreplayActivity = new Intent(getApplicationContext(),Replay_comment_Activity.class);
                        toreplayActivity.putExtra("idcomment",idcomment);
                        toreplayActivity.putExtra("idpost",postkey);
                        startActivity(toreplayActivity);
                    }
                });
                viewHolder.setLikebuttonstatus(idcomment);

                viewHolder.Displaynumoflike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent tolikecommentActivity = new Intent(getApplicationContext(),Display_UserLikecomment.class);
                        tolikecommentActivity.putExtra("idcommentintent",idcomment);
                        startActivity(tolikecommentActivity);
                    }
                });

                viewHolder.likebutton.setOnClickListener(new View.OnClickListener() {
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
                                    if (dataSnapshot.child(idcomment).hasChild(currentUserId))
                                    {
                                        LikesRef.child(idcomment).child(currentUserId).removeValue();
                                        Likecheker=false;
                                    }
                                    else
                                    {


                                        LikesRef.child(idcomment).child(currentUserId).child("Users").setValue(true)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())
                                                        {



                                                            Userref.child(currentUserId).addValueEventListener(new ValueEventListener() {
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
                                                                        LikesRef.child(idcomment).child(currentUserId).updateChildren(userMap)
                                                                                .addOnCompleteListener(new OnCompleteListener() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task task) {
                                                                                        if (task.isSuccessful())
                                                                                        {
                                                                                            Toast.makeText(CommentActivity.this, "done", Toast.LENGTH_SHORT).show();
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
        commentList.setAdapter(firebaseRecyclerAdapter);
    }






    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {

        View mView;
        private TextView replaytext;
        private TextView liketext;
        private CircleImageView profileImage;
        private ImageButton likebutton;
        private DatabaseReference Likecomment;
        private DatabaseReference UserRef;
        String  currentuserid;
        int countLikes;
        private TextView Displaynumoflike;

        public PostsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

            replaytext = (TextView)mView.findViewById(R.id.replaycomment);
            profileImage = (CircleImageView)mView.findViewById(R.id.image_profile_comment);
            likebutton = (ImageButton)mView.findViewById(R.id.likecommentbutton);
            Displaynumoflike=(TextView)mView.findViewById(R.id.likecommenttext);


            Likecomment=FirebaseDatabase.getInstance().getReference().child("Likes_comment");
            UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
            currentuserid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        }





        public void setLikebuttonstatus(final String postkey)
        {


           // postcommentRef= FirebaseDatabase.getInstance().getReference().child("posts").child(postkey).child("comments");


            Likecomment.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.child(postkey).hasChild(currentuserid)) {

                        countLikes = (int) dataSnapshot.child(postkey).getChildrenCount();
                        likebutton.setImageResource(R.drawable.smallheartred);
                        UserRef.child(currentuserid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String name=dataSnapshot.child("fullName").getValue().toString();
                                Displaynumoflike.setText(Integer.toString(countLikes)+"like");

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else
                    {
                        countLikes = (int) dataSnapshot.child(postkey).getChildrenCount();
                        likebutton.setImageResource(R.drawable.smallheart);
                        Displaynumoflike.setText(Integer.toString(countLikes) +" Like");
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }




        public void setUsername(String username)
        {
            TextView textname=(TextView)mView.findViewById(R.id.textnamecomment);
            textname.setText(username);
        }

        public void setComment(String comment)
        {
            TextView textcomment=(TextView)mView.findViewById(R.id.textcomment);
            textcomment.setText(comment);
        }

        public void setDate(String date)
        {
            TextView textdate=(TextView)mView.findViewById(R.id.textdatecomment);
            textdate.setText(date);
        }

        public void setTime(String time)
        {
            TextView textTime=(TextView) mView.findViewById(R.id.textTimecomment);

            textTime.setText(time);
        }


        public void setProfilimage(Context context,String profilimage)
        {
            CircleImageView profileImageprofile = (CircleImageView)mView.findViewById(R.id.image_profile_comment);
            Picasso.with(context).load(profilimage).into(profileImageprofile);
        }

    }





    private void Validatecomment(String username,String Current_profile_image)
    {
        String commenttext=commentInput.getText().toString();

        if(TextUtils.isEmpty(commenttext))
        {
            Toast.makeText(this, "please write your comment..", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar callForDate=Calendar.getInstance();
            SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
           final String savecurrentdate=currentdate.format(callForDate.getTime());


            Calendar callForTime=Calendar.getInstance();
            SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
            final String savecurrenttime=currentTime.format(callForTime.getTime());

            String Randomkey=currentUserId+savecurrentdate+savecurrenttime;

            HashMap commentdec=new HashMap();
            commentdec.put("date",savecurrentdate);
            commentdec.put("time",savecurrenttime);
            commentdec.put("comment",commenttext);
            commentdec.put("uid",currentUserId);
            commentdec.put("username",username);
            commentdec.put("profilimage",Current_profile_image);

            postcommentRef.child(Randomkey).updateChildren(commentdec)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(CommentActivity.this, "saved post successfully..", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(CommentActivity.this, "error occurred...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
