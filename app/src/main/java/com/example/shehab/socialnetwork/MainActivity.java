package com.example.shehab.socialnetwork;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;



public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar mtoolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentuser;
    private DatabaseReference Userref,postRef,LikesRef;
    private CircleImageView NavprofileImage;
    private TextView Navusername;
    String currentuserid;
    private ImageButton NewPostButton,NewRequestFriends;
    private RecyclerView post_List;
    Boolean Likecheker = false;
    private TextView countertextFriend;
    private DatabaseReference FriendsReference;
    private ImageButton messagebar;
    private ImageButton notification_button;
    private RecyclerView list_stores;
    private DatabaseReference usersettingref,usersettingref2;
    private StorageReference UserprofileImageStorge;
    private static final int picgallery=1;
    private DatabaseReference saveref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();

        currentuserid=mAuth.getCurrentUser().getUid();


        Userref= FirebaseDatabase.getInstance().getReference().child("Users");
        postRef=FirebaseDatabase.getInstance().getReference().child("posts");
        LikesRef=FirebaseDatabase.getInstance().getReference().child("Likes");
        FriendsReference= FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        usersettingref= FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserid);
        usersettingref2= FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserid);
        saveref = FirebaseDatabase.getInstance().getReference().child("saveposts");
        UserprofileImageStorge= FirebaseStorage.getInstance().getReference().child("profile image");


        mtoolbar=(Toolbar)findViewById(R.id.mtoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("hom");


        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer);


        actionBarDrawerToggle= new ActionBarDrawerToggle(MainActivity.this,mDrawerLayout,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        navigationView=(NavigationView)findViewById(R.id.nav);

        View nav_view=navigationView.inflateHeaderView(R.layout.header);

        NavprofileImage=(CircleImageView)nav_view.findViewById(R.id.nav_profile_image);
        Navusername=(TextView)nav_view.findViewById(R.id.nav_user_full_name);
        NewPostButton=(ImageButton)findViewById(R.id.add_new_post_button);
        NewRequestFriends=(ImageButton)findViewById(R.id.frieind_request_button);
        post_List=(RecyclerView)findViewById(R.id.all_users_post_list);
        countertextFriend=(TextView)findViewById(R.id.counter_friend_request);
        messagebar = (ImageButton)findViewById(R.id.messagebar);
        notification_button = (ImageButton)findViewById(R.id.notificationbutton);
        list_stores = (RecyclerView) findViewById(R.id.stores);

        countertextFriend.setVisibility(View.INVISIBLE);

        list_stores.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        list_stores.setLayoutManager(linearLayoutManager2);


        post_List.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        post_List.setLayoutManager(linearLayoutManager);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                UserMenuSelector(item);
                return false;
            }
        });

        Userref.child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    if(dataSnapshot.hasChild("fullName"))
                    {
                        String name = dataSnapshot.child("fullName").getValue().toString();
                        Navusername.setText(name);

                    }
                    if(dataSnapshot.hasChild("profilimage")){
                        String profilImage = dataSnapshot.child("profilimage").getValue().toString();
                        Picasso.with(getBaseContext()).load(profilImage).into(NavprofileImage);

                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "profile image and name not exists", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        NewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent PostActivityintent=new Intent(getApplicationContext(),MainActivity2.class);
                startActivity(PostActivityintent);
            }
        });

        NewRequestFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newrequestFrindIntent=new Intent(getApplicationContext(),FriendRequestActivity.class);
                startActivity(newrequestFrindIntent);
            }
        });

        messagebar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatActivityIntent=new Intent(getApplicationContext(),MessagesActivity.class);
                startActivity(chatActivityIntent);
            }
        });

        Displayalluserposts();
        Displayallstores();


        FriendsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                int counterFriendsRequest=(int) dataSnapshot.child(currentuserid).getChildrenCount();
                if(counterFriendsRequest  > 0){
                    countertextFriend.setVisibility(View.VISIBLE);
                    countertextFriend.setText(Integer.toString(counterFriendsRequest));

                }else
                {
                    countertextFriend.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        notification_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notificationActivityintent=new Intent(getApplicationContext(),NotificationActivity.class);
                startActivity(notificationActivityintent);
            }
        });


        NavprofileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                startActivityForResult(galleryIntent,picgallery);

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == picgallery && resultCode == RESULT_OK && data !=null)
        {
            Uri Imageuri=data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .setMaxCropResultSize(700,700)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                Uri resulturi=result.getUri();

                final StorageReference filePath=UserprofileImageStorge.child(currentuserid + ".jpg");

                filePath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){


                            final String downloadUrl=task.getResult().getDownloadUrl().toString();
                            Toast.makeText(MainActivity.this, "the photo uploaded successfully.", Toast.LENGTH_SHORT).show();

                            usersettingref2.child("profilimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {


                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(MainActivity.this, "the pic saved in database", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                String message=task.getException().getMessage();
                                                Toast.makeText(MainActivity.this, "error occurred"+message, Toast.LENGTH_SHORT).show();

                                            }
                                        }

                                    });
                        }
                    }
                });



            }
        }


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

        Userref.child(currentuserid).child("user_status").updateChildren(currentuserstate);

    }


    private void Displayalluserposts()
    {


        Query sortpostDescending=postRef.orderByChild("counter");


        FirebaseRecyclerAdapter<Posts,PostsViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(
                        Posts.class,
                        R.layout.all_post_layout,
                        PostsViewHolder.class,
                        sortpostDescending
                )
                {
                    @Override
                    protected void populateViewHolder(PostsViewHolder viewHolder, Posts model, int position) {

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
                                        String imagepost = dataSnapshot.child("postimage").getValue().toString();
                                        String imageprofile = dataSnapshot.child("postprofileimag").getValue().toString();
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

                                        saveref.child(currentuserid).child(idlist)
                                                .updateChildren(postMap)
                                                .addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {

                                                        if(task.isSuccessful())
                                                        {
                                                            Toast.makeText(MainActivity.this, "post saved successfully....", Toast.LENGTH_SHORT).show();
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
                                          if (dataSnapshot.child(idlist).hasChild(currentuserid))
                                          {
                                              LikesRef.child(idlist).child(currentuserid).removeValue();
                                              Likecheker=false;
                                          }
                                          else
                                          {


                                                          LikesRef.child(idlist).child(currentuserid).child("Users").setValue(true)
                                                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                      @Override
                                                                      public void onComplete(@NonNull Task<Void> task) {
                                                                          if (task.isSuccessful())
                                                                          {



                                                                              Userref.child(currentuserid).addValueEventListener(new ValueEventListener() {
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
                                                                                          LikesRef.child(idlist).child(currentuserid).updateChildren(userMap)
                                                                                                  .addOnCompleteListener(new OnCompleteListener() {
                                                                                                      @Override
                                                                                                      public void onComplete(@NonNull Task task) {
                                                                                                          if (task.isSuccessful())
                                                                                                          {
                                                                                                              Toast.makeText(MainActivity.this, "done", Toast.LENGTH_SHORT).show();
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



    private void Displayallstores()
    {

        FirebaseRecyclerAdapter<DataStores,StoresViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<DataStores, StoresViewHolder>(
                        DataStores.class,
                        R.layout.layout_stores,
                        StoresViewHolder.class,
                        Userref
                )
                {
                    @Override
                    protected void populateViewHolder(StoresViewHolder viewHolder, DataStores model, int position) {

                        final String idlist=getRef(position).getKey();

                        viewHolder.setFullName(model.getFullName());
                        viewHolder.setProfilimage(getBaseContext(),model.getProfilimage());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(),Progress_activity.class);
                                startActivity(intent);
                            }
                        });





                    }
                };
        list_stores.setAdapter(firebaseRecyclerAdapter);

    }




    public static class StoresViewHolder extends RecyclerView.ViewHolder
    {

        View mView;
        CircleImageView storesImage;
        TextView name;



        public StoresViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

        }

        public void setProfilimage(Context context,String profilimage)
        {
            storesImage = (CircleImageView) mView.findViewById(R.id.imagestore);

            Picasso.with(context).load(profilimage).into(storesImage);
        }

        public void setFullName(String fullName)
        {
            TextView name = (TextView) mView.findViewById(R.id.nameprofile);
            name.setText(fullName);
        }

    }



    @Override
    protected void onStart() {
        super.onStart();
        currentuser=mAuth.getCurrentUser();

        if (currentuser == null)
        {
            sandtologinActivity();
        }
        else
        {
            CheckuserExsistnce();
        }
    }

    private void CheckuserExsistnce()
    {
        final String current_user_id=mAuth.getCurrentUser().getUid();


        Userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!dataSnapshot.hasChild(current_user_id))
                {
                    senduserTosetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void senduserTosetupActivity() {
        Intent setupIntent=new Intent(MainActivity.this,SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void sandtologinActivity() {

        Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {

        switch(item.getItemId()){

            case R.id.nav_post:


                Intent PostActivityintent=new Intent(getApplicationContext(),MainActivity2.class);
                startActivity(PostActivityintent);
                break;

            case R.id.nav_profile:
                Intent ProfileActivityintent=new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(ProfileActivityintent);
                Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_hom:
                Intent HomActivityIntent=new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(HomActivityIntent);
                Toast.makeText(this, "hom", Toast.LENGTH_SHORT).show();
                break;
                case R.id.nav_Friends:

                    Intent FriendsIntent=new Intent(getApplicationContext(),FriendsActivity.class);
                    startActivity(FriendsIntent);
                Toast.makeText(this, "friends", Toast.LENGTH_SHORT).show();
                break;
                case R.id.nav_find_friends:
                    Intent FindActivityintent=new Intent(getApplicationContext(),FindfriendActivity.class);
                    startActivity(FindActivityintent);
                Toast.makeText(this, "find friends", Toast.LENGTH_SHORT).show();
                break;
                case R.id.nav_messages:

                    Intent messageActivityIntent=new Intent(getApplicationContext(),MessagesActivity.class);

                    startActivity(messageActivityIntent);

                Toast.makeText(this, "message", Toast.LENGTH_SHORT).show();
                break;
                case R.id.nav_setting:
                    Intent settingIntent=new Intent(getApplicationContext(),SettingActivity.class);
                    startActivity(settingIntent);
                Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:


                mAuth.signOut();
                Intent registerIntent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(registerIntent);
                Toast.makeText(this, "log out", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_save:

                Intent saveIntent=new Intent(getApplicationContext(),SaveActivity.class);
                startActivity(saveIntent);
                Toast.makeText(this, "save", Toast.LENGTH_SHORT).show();
                break;
        }



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        userUpdateStatus("offline");
        android.os.Process.killProcess(android.os.Process.myPid());
        finish();

    }
}

