package com.example.shehab.socialnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.Guideline;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class SetupActivity extends AppCompatActivity {

    private EditText userName,UserFullname,countryNmae;
    private Button saveInformationButoon;
    private CircleImageView userProfileImage;
    private DatabaseReference UserRefer;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private ProgressDialog lodingbar;
    private final static int picgallery=1;
    private StorageReference UserprofileImageStorge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth= FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();

        UserRefer= FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);

        UserprofileImageStorge=FirebaseStorage.getInstance().getReference().child("profile image");

        userName=(EditText)findViewById(R.id.setup_userName);
        UserFullname=(EditText)findViewById(R.id.setup_fullName);
        countryNmae=(EditText)findViewById(R.id.setup_countryName);
        saveInformationButoon=(Button)findViewById(R.id.setup_information_button);
        userProfileImage=(CircleImageView)findViewById(R.id.setup_profile_image);

        saveInformationButoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SaveaccountsetupInformation();
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                startActivityForResult(galleryIntent,picgallery);
            }
        });

        UserRefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("profilimage"))
                    {
                        String Image = dataSnapshot.child("profilimage").getValue().toString();

                        Picasso.with(getBaseContext()).load(Image).placeholder(R.drawable.backgroundprof).into(userProfileImage);
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
                            Toast.makeText(SetupActivity.this, "the photo uploaded successfully.", Toast.LENGTH_SHORT).show();

                            UserRefer.child("profilimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {


                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(SetupActivity.this, "the pic saved in database", Toast.LENGTH_SHORT).show();
                                                lodingbar.dismiss();
                                            }
                                            else
                                                {
                                                    String message=task.getException().getMessage();
                                                    Toast.makeText(SetupActivity.this, "error occurred"+message, Toast.LENGTH_SHORT).show();
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

    private void SaveaccountsetupInformation()
    {
        String name=userName.getText().toString();
        String fullName=UserFullname.getText().toString();
        String country=countryNmae.getText().toString();


        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "please enter your name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(fullName))
        {
            Toast.makeText(this,"please enter the full name ",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(country))
        {
            Toast.makeText(this,"please enter your country",Toast.LENGTH_LONG).show();
        }
        else
            {
                lodingbar.setTitle("register your information");
                lodingbar.setMessage("wait while creating your account ....");
                lodingbar.show();
                lodingbar.setCanceledOnTouchOutside(true);
                HashMap userMap=new HashMap();
                userMap.put("userName",name);
                userMap.put("fullName",fullName);
                userMap.put("country",country);
                userMap.put("status","hey there , i am using the social network app ,developed by shehab osama");
                userMap.put("gender","none");
                userMap.put("dob","none");
                userMap.put("relationship","none");


                UserRefer.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {

                        if(task.isSuccessful())
                        {
                            senduserToMainActivity();
                            Toast.makeText(getBaseContext(), "the register is successfully", Toast.LENGTH_LONG).show();
                            lodingbar.dismiss();
                        }
                        else
                            {
                                String message=task.toString();
                                Toast.makeText(SetupActivity.this, "error occurred"+message, Toast.LENGTH_SHORT).show();
                                lodingbar.dismiss();
                            }
                    }
                });
            }

    }

    private void senduserToMainActivity()
    {
        Intent MainIntent=new Intent(SetupActivity.this,MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);

    }
}
