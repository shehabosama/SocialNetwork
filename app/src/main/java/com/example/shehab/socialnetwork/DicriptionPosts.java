package com.example.shehab.socialnetwork;

import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DicriptionPosts extends AppCompatActivity {

    private String idpost;
    private String imagepost;
    private DatabaseReference postref;
    private EditText postDecription;
    private ImageView postimage;
    private Button sharepost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dicription_posts);

        idpost = getIntent().getExtras().getString("idposts").toString();
        imagepost = getIntent().getExtras().getString("postimage").toString();

        postref = FirebaseDatabase.getInstance().getReference().child("posts");

        postDecription = (EditText)findViewById(R.id.decriptionpost);
        postimage = (ImageView)findViewById(R.id.imagepost);
        sharepost = (Button)findViewById(R.id.sharepostdecription);


        Picasso.with(getBaseContext()).load(imagepost).into(postimage);

        sharepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String decription = postDecription.getText().toString();
                postref.child(idpost).child("description").setValue(decription)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                    finish();
                                }
                            }
                        });

            }
        });

    }
}
