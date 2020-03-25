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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Replay_comment_Activity extends AppCompatActivity implements AnimationListener {

    private String idcomment;
    private String idpost;
    private DatabaseReference replaycommentRef;
    private EditText replaycommentInput;
    private RecyclerView commentList;
    private ImageButton postcommentbutton;
    private FirebaseAuth mAuth;
    private String Current_user_id;
    private DatabaseReference UserRef;
    private DatabaseReference commentref;
    private CircleImageView imageprofile;
    private TextView datecomment;
    private TextView Comment;
    private TextView timecomment;
    private TextView namecomment;
    Animation animBlink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rplay_comment_);
        mAuth = FirebaseAuth.getInstance();
        Current_user_id = mAuth.getCurrentUser().getUid();


        idcomment = getIntent().getExtras().getString("idcomment").toString();
        idpost = getIntent().getExtras().getString("idpost").toString();
        replaycommentRef = FirebaseDatabase.getInstance().getReference().child("posts").child(idpost)
                .child("comments").child(idcomment).child("replay_comment");
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        commentref = FirebaseDatabase.getInstance().getReference().child("posts").child(idpost).child("comments").child(idcomment);

        commentList=(RecyclerView)findViewById(R.id.comment_list);
        commentList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        commentList.setLayoutManager(linearLayoutManager);


        replaycommentInput=(EditText)findViewById(R.id.comment_input_text);
        postcommentbutton=(ImageButton)findViewById(R.id.post_comment_button);
        imageprofile = (CircleImageView)findViewById(R.id.image_profile_comment);
        namecomment = (TextView)findViewById(R.id.textnamecomment);
        datecomment = (TextView)findViewById(R.id.textdatecomment);
        timecomment = (TextView)findViewById(R.id.textTimecomment);
        Comment = (TextView)findViewById(R.id.textcomment);
        postcommentbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserRef.child(Current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String username=dataSnapshot.child("userName").getValue().toString();
                        String profileimage=dataSnapshot.child("profilimage").getValue().toString();


                        Validatecomment(username,profileimage);
                        replaycommentInput.setText("");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });



        commentref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String woner_name_comment =dataSnapshot.child("username").getValue().toString();
                    String first_comment = dataSnapshot.child("comment").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();
                    String time = dataSnapshot.child("time").getValue().toString();
                    String profileimage=dataSnapshot.child("profilimage").getValue().toString();

                    Picasso.with(getBaseContext()).load(profileimage).into(imageprofile);
                    datecomment.setText(date);
                    timecomment.setText(time);
                    Comment.setText(first_comment);
                    namecomment.setText(woner_name_comment);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        // load the animation
        animBlink = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.rotate);

        // set animation listener
        animBlink.setAnimationListener(this);

        // button click event
        namecomment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                namecomment.setVisibility(View.VISIBLE);

                // start the animation
                namecomment.startAnimation(animBlink);
            }
        });

    }




    @Override
    protected void onStart() {
        super.onStart();




        FirebaseRecyclerAdapter<Allcomments, Replay_comment_Activity.PostsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Allcomments, Replay_comment_Activity.PostsViewHolder>(
                Allcomments.class,
                R.layout.all_replay_comments_layout,
                Replay_comment_Activity.PostsViewHolder.class,
                replaycommentRef
        ) {
            @Override
            protected void populateViewHolder(Replay_comment_Activity.PostsViewHolder viewHolder, Allcomments model, final int position) {

                final String idcomment = getRef(position).getKey();

                viewHolder.setUsername(model.getUsername());
                viewHolder.setComment(model.getComment());
                viewHolder.setDate( model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setProfilimage(getApplicationContext(),model.getProfilimage());



            }
        };
        commentList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {

        View mView;
        private TextView replaytext;
        private TextView liketext;
        private CircleImageView profileImage;



        public PostsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

            replaytext = (TextView)mView.findViewById(R.id.replaycomment);
            profileImage = (CircleImageView)mView.findViewById(R.id.image_profile_comment);

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

        public void setProfilimage(Context context, String profilimage)
        {
            CircleImageView profileImageprofile = (CircleImageView)mView.findViewById(R.id.image_profile_commentreplay);
            Picasso.with(context).load(profilimage).into(profileImageprofile);
        }


    }



    private void Validatecomment(String username,String Current_profile_image)
    {
        String commenttext=replaycommentInput.getText().toString();

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

            String Randomkey=Current_user_id+savecurrentdate+savecurrenttime;

            HashMap commentdec=new HashMap();
            commentdec.put("date",savecurrentdate);
            commentdec.put("time",savecurrenttime);
            commentdec.put("comment",commenttext);
            commentdec.put("uid",Current_user_id);
            commentdec.put("username",username);
            commentdec.put("profilimage",Current_profile_image);


            replaycommentRef.child(Randomkey).updateChildren(commentdec)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(Replay_comment_Activity.this, "saved post successfully..", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(Replay_comment_Activity.this, "error occurred...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}
