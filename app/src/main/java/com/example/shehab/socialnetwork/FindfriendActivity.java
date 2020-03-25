package com.example.shehab.socialnetwork;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindfriendActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView allUserList;
    private DatabaseReference alldatabaseusersreference;
    private EditText SearchInputText;
    private ImageButton SearchButton;
    private Toolbar mtoolbar;
    private FirebaseAuth mAuht;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findfriend);

        mAuht=FirebaseAuth.getInstance();

        mtoolbar=(Toolbar)findViewById(R.id.all_users_app_bar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("find friend");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SearchInputText = (EditText) findViewById(R.id.search_input_text);
        SearchButton = (ImageButton) findViewById(R.id.search_people_button);


        allUserList = (RecyclerView) findViewById(R.id.all_users_list);
        allUserList.setHasFixedSize(true);
        allUserList.setLayoutManager(new LinearLayoutManager(this));

        alldatabaseusersreference = FirebaseDatabase.getInstance().getReference().child("Users");


        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String SearchUserName = SearchInputText.getText().toString();

                if (TextUtils.isEmpty(SearchUserName)) {
                    Toast.makeText(FindfriendActivity.this, "enter the name", Toast.LENGTH_SHORT).show();
                }
                SearchForPeopleAndFrinds(SearchUserName);
            }
        });
    }

    private void SearchForPeopleAndFrinds(String SearchUserName) {

        Toast.makeText(this, "Searching", Toast.LENGTH_SHORT).show();
        Query searchPeopleAndFriends = alldatabaseusersreference.orderByChild("fullName")
                .startAt(SearchUserName).endAt(SearchUserName + "\uf8ff");


        FirebaseRecyclerAdapter<Allusers, FindfriendActivity.AllUsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Allusers,FindfriendActivity.AllUsersViewHolder>(
                Allusers.class,
                R.layout.all_users_display_layout,
                AllUsersViewHolder.class,
                searchPeopleAndFriends
        ) {
            @Override
            protected void populateViewHolder(AllUsersViewHolder viewHolder, Allusers model, final int position) {

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