package com.example.shehab.socialnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private EditText userStatus;
    private EditText userName;
    private EditText userProfieName;
    private EditText userCountry;
    private EditText userDob;
    private EditText userGender;
    private EditText userRelationship;
    private Button update_account_button;
    private DatabaseReference usersettingref,usersettingref2;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private static final int picgallery=1;
    private ProgressDialog lodingbar;
    private StorageReference UserprofileImageStorge;
    private Toolbar mtoolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        mtoolbar=(Toolbar)findViewById(R.id.settingtoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        profileImage=(CircleImageView)findViewById(R.id.image_profile_setting);
        userStatus=(EditText) findViewById(R.id.status_setting);
        userName=(EditText)findViewById(R.id.user_name_setting);
        userProfieName=(EditText)findViewById(R.id.profile_name_setting);
        userCountry=(EditText)findViewById(R.id.country_setting);
        userDob=(EditText)findViewById(R.id.date_of_birth_setting);
        userGender=(EditText)findViewById(R.id.gender_setting);
        userRelationship=(EditText)findViewById(R.id.relation_setting);
        update_account_button=(Button)findViewById(R.id.save_change_settings);

        mAuth=FirebaseAuth.getInstance();

        current_user_id=mAuth.getCurrentUser().getUid();
        usersettingref= FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        usersettingref2= FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        UserprofileImageStorge= FirebaseStorage.getInstance().getReference().child("profile image");




        usersettingref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username=dataSnapshot.child("userName").getValue().toString();
                String userstatus=dataSnapshot.child("status").getValue().toString();
                String userprofilename=dataSnapshot.child("fullName").getValue().toString();
                String usercountry=dataSnapshot.child("country").getValue().toString();
                String userdob=dataSnapshot.child("dob").getValue().toString();
                String usergender=dataSnapshot.child("gender").getValue().toString();
                String userrealtion=dataSnapshot.child("relationship").getValue().toString();

                userName.setText(username);
                userStatus.setText(userstatus);
                userProfieName.setText(userprofilename);
                userCountry.setText(usercountry);
                userDob.setText(userdob);
                userGender.setText(usergender);
                userRelationship.setText(userrealtion);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                startActivityForResult(galleryIntent,picgallery);
            }
        });


        update_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap userMap=new HashMap();
                userMap.put("userName",userName.getText().toString());
                userMap.put("fullName",userProfieName.getText().toString());
                userMap.put("country",userCountry.getText().toString());
                userMap.put("status",userStatus.getText().toString());
                userMap.put("gender",userGender.getText().toString());
                userMap.put("dob",userDob.getText().toString());
                userMap.put("relationship",userRelationship.getText().toString());

                usersettingref2.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getBaseContext(),"the update is successfully...",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


        usersettingref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("profilimage"))
                    {
                        String Image = dataSnapshot.child("profilimage").getValue().toString();

                        Picasso.with(getBaseContext()).load(Image).placeholder(R.drawable.backgroundprof).into(profileImage);
                    }else
                    {
                        Toast.makeText(getBaseContext(), "the image not exists", Toast.LENGTH_SHORT).show();

                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        lodingbar=new ProgressDialog(this);
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
                lodingbar.setTitle("changing the photo");
                lodingbar.setMessage("wait while changing the photo....");
                lodingbar.show();
                lodingbar.setCanceledOnTouchOutside(true);
                final StorageReference filePath=UserprofileImageStorge.child(current_user_id + ".jpg");

                filePath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){


                            final String downloadUrl=task.getResult().getDownloadUrl().toString();
                            Toast.makeText(SettingActivity.this, "the photo uploaded successfully.", Toast.LENGTH_SHORT).show();

                            usersettingref2.child("profilimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {


                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(SettingActivity.this, "the pic saved in database", Toast.LENGTH_SHORT).show();
                                                lodingbar.dismiss();
                                            }
                                            else
                                            {
                                                String message=task.getException().getMessage();
                                                Toast.makeText(SettingActivity.this, "error occurred"+message, Toast.LENGTH_SHORT).show();
                                                lodingbar.dismiss();

                                            }
                                        }

                                    });
                        }
                    }
                });



            }
        }


    }



}
