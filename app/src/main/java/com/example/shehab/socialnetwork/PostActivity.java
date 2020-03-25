package com.example.shehab.socialnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    Toolbar mtoolbar;
    private ImageButton selectpostimage;
    private EditText descriptionPost;
    private Button UpdatePostbutton;
    private final static int Gellary_pic=1;
    private Uri ImageUri;
    private String discription;
    private StorageReference postImagesReference;
    private String savecurrentdate,savecurrenttime,PostRamdomNme,downloadUrl,current_user_id;
    private DatabaseReference userRef,postRef2;
    private FirebaseAuth mAuth;
    private DatabaseReference postRef;
    private ProgressDialog lodingbar;
    private long countpost=0;
    private String receiveridpost;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        receiveridpost = getIntent().getExtras().getString("postkey").toString();

        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();

        postImagesReference= FirebaseStorage.getInstance().getReference();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        postRef=FirebaseDatabase.getInstance().getReference().child("posts");
        postRef2=FirebaseDatabase.getInstance().getReference().child("posts");


        selectpostimage=(ImageButton)findViewById(R.id.select_Post_image);
        descriptionPost=(EditText)findViewById(R.id.Post_description_post);
        UpdatePostbutton=(Button)findViewById(R.id.Update_post_button);
        mtoolbar=(Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Update Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        UpdatePostbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInformationtoDatabase();
                ValdataPost();

            }
        });

        lodingbar=new ProgressDialog(this);

        postRef.child(receiveridpost).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String description = dataSnapshot.child("description").getValue().toString();
                String imagepost= dataSnapshot.child("postimage").getValue().toString();
                Picasso.with(getBaseContext()).load(imagepost).into(selectpostimage);
                descriptionPost.setText(description);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void ValdataPost()
    {

            lodingbar.setTitle("uploading your post..");
            lodingbar.setMessage("wait while uploading the post  ....");
            lodingbar.show();
            lodingbar.setCanceledOnTouchOutside(true);

            StoringImageToStroge();

    }

    private void StoringImageToStroge()
    {
        Calendar callForDate=Calendar.getInstance();
        SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
        savecurrentdate=currentdate.format(callForDate.getTime());


        Calendar callForTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
        savecurrenttime=currentTime.format(callForTime.getTime());

        PostRamdomNme=savecurrentdate+savecurrenttime;


    }




    private void saveInformationtoDatabase()
    {


        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    countpost = dataSnapshot.getChildrenCount();
                }
                else
                {
                    countpost=0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        userRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String userFullname=dataSnapshot.child("fullName").getValue().toString();
                final  String userprofilimage=dataSnapshot.child("profilimage").getValue().toString();

                postRef.child(receiveridpost).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        String imagepost= dataSnapshot.child("postimage").getValue().toString();
                        String date = dataSnapshot.child("date").getValue().toString();
                        String time = dataSnapshot.child("time").getValue().toString();
                        String description = dataSnapshot.child("description").getValue().toString();
                        String count_post = dataSnapshot.child("counter").getValue().toString();
                        String orignal_user= dataSnapshot.child("fullname").getValue().toString();



                        HashMap postMap=new HashMap();
                        postMap.put("uid",current_user_id);
                        postMap.put("date",date);
                        postMap.put("time",time);
                        postMap.put("uid",current_user_id);
                        postMap.put("description",description);
                        postMap.put("postimage",imagepost);
                        postMap.put("postprofileimag",userprofilimage);
                        postMap.put("fullname",userFullname+"\n shared from:  "+orignal_user);
                        postMap.put("counter",countpost);

                        postRef.child(current_user_id +PostRamdomNme).updateChildren(postMap)
                                .addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful())
                                        {
                                            sendUserTomainActivity();
                                            Toast.makeText(PostActivity.this, "post update successfully... ", Toast.LENGTH_SHORT).show();

                                            lodingbar.dismiss();
                                        }
                                        else
                                        {
                                            Toast.makeText(PostActivity.this, "error occurred..", Toast.LENGTH_SHORT).show();
                                            lodingbar.dismiss();
                                        }
                                    }
                                });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    private void sendUserTomainActivity() {
        Intent mainActivityintent=new Intent(getApplicationContext(),MainActivity.class);
        mainActivityintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityintent);
        finish();
    }




}
