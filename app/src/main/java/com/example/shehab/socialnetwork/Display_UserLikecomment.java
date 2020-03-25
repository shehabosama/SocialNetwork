package com.example.shehab.socialnetwork;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Display_UserLikecomment extends AppCompatActivity {

    private String idcomment;
    private DatabaseReference likecommentref;
    private RecyclerView Like_List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display__user_likecomment);
        idcomment = getIntent().getExtras().getString("idcommentintent").toString();
        likecommentref = FirebaseDatabase.getInstance().getReference().child("Likes_comment").child(idcomment);

        Like_List=(RecyclerView)findViewById(R.id.all_users_Like_list);
        Like_List.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        Like_List.setLayoutManager(linearLayoutManager);
        SearchForPeopleAndFriends();

    }

    private void SearchForPeopleAndFriends() {



        FirebaseRecyclerAdapter<Allusers, LikeActivity.AllUsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Allusers, LikeActivity.AllUsersViewHolder>(
                Allusers.class,
                R.layout.all_users_display_layout,
                LikeActivity.AllUsersViewHolder.class,
                likecommentref
        ) {
            @Override
            protected void populateViewHolder(LikeActivity.AllUsersViewHolder viewHolder, Allusers model, final int position) {

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
        Like_List.setAdapter(firebaseRecyclerAdapter);
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
