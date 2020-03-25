package com.example.shehab.socialnetwork;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationActivity extends AppCompatActivity {

    private DatabaseReference notiref;
    private DatabaseReference postref;
    private DatabaseReference userref;
    private RecyclerView allUserList;
    private FirebaseAuth mAuth;
    String Current_user_id;
    int  likecount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mAuth = FirebaseAuth.getInstance();
        Current_user_id = mAuth.getCurrentUser().getUid();

        notiref= FirebaseDatabase.getInstance().getReference().child("Likes");

        postref= FirebaseDatabase.getInstance().getReference().child("posts");
        userref= FirebaseDatabase.getInstance().getReference().child("Users");


        allUserList = (RecyclerView) findViewById(R.id.all_users_list);
        allUserList.setHasFixedSize(true);
        allUserList.setLayoutManager(new LinearLayoutManager(this));






    }


    @Override
    protected void onStart()
    {
        super.onStart();

      notiref.addChildEventListener(new ChildEventListener() {
          @Override
          public void onChildAdded(DataSnapshot dataSnapshot, String s) {
              Toast.makeText(NotificationActivity.this, ""+dataSnapshot.getKey().toString(),
                      Toast.LENGTH_SHORT).show();
              

          }

          @Override
          public void onChildChanged(DataSnapshot dataSnapshot, String s) {

          }

          @Override
          public void onChildRemoved(DataSnapshot dataSnapshot) {

          }

          @Override
          public void onChildMoved(DataSnapshot dataSnapshot, String s) {

          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });
/*
        FirebaseRecyclerAdapter<Allusers, NotificationActivity.AllUsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Allusers, NotificationActivity.AllUsersViewHolder>(
                Allusers.class,
                R.layout.all_users_display_layout,
                NotificationActivity.AllUsersViewHolder.class,
                notiref.child("4c4Rk3Ctc6hMa7pF5XvdYk1ovgX224-September-201806:53")
        ) {
            @Override
            protected void populateViewHolder(NotificationActivity.AllUsersViewHolder viewHolder, Allusers model, final int position) {

                viewHolder.setUser_name(model.getUser_name());
                viewHolder.setUser_status(model.getUser_status());
                viewHolder.setUser_image(getApplicationContext(), model.getUser_image());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String visit_user_id = getRef(position).getKey();
                        Intent personprofileIntent = new Intent(getApplicationContext(), PersonProfileActivty.class);
                        personprofileIntent.putExtra("Visituserid", visit_user_id);
                        startActivity(personprofileIntent);
                    }
                });
            }
        };

        firebaseRecyclerAdapter.notifyDataSetChanged();
        allUserList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
        */
    }


    public static class AllUsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public AllUsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUser_name(String user_name) {
            TextView name = (TextView) mView.findViewById(R.id.all_users_username);
            name.setText(user_name);
        }

        public void setUser_status(String user_status) {
            TextView status = mView.findViewById(R.id.all_users_status);
            status.setText(user_status);
        }

        public void setUser_image(Context ctx, String user_image) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.all_users_profile_image);


            Picasso.with(ctx).load(user_image).placeholder(R.drawable.backgroundprof).into(image);

        }
    }
}
