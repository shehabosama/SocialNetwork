package com.example.shehab.socialnetwork;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String message_reseiver_id;
    private String message_reseiver_name;
    private String message_sender_id;
    private String savecurrentdate;
    private String savecurrenttime;
    private Toolbar chatoolbar;
    private TextView userNmaeTitle;
    private FirebaseAuth mAuth;
    private TextView userLastseen;
    private CircleImageView userchatprofileimage;
    private ImageButton SendMessageButton;
    private ImageButton SelectImageButton;
    private EditText InputMessageText;
    private DatabaseReference RootRef,UserRef;
    private RecyclerView userMessageslist;
    private final List<Messages> messagesList=new ArrayList<>();
    private MessagesAdapter messagesAdapter;
    private LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAuth=FirebaseAuth.getInstance();



        RootRef= FirebaseDatabase.getInstance().getReference();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        message_reseiver_id=getIntent().getExtras().getString("Visituserid").toString();
        message_reseiver_name=getIntent().getExtras().getString("userName").toString();
        message_sender_id=mAuth.getCurrentUser().getUid();


        InitializeFailds();

        DisplaydataofUsers();

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


        FatchMessags();

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

        UserRef.child(message_sender_id).child("user_status")
                .updateChildren(currentuserstate);


    }

    private void FatchMessags()
    {
        RootRef.child("Messages").child(message_sender_id).child(message_reseiver_id)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {

                        if(dataSnapshot.exists())
                        {
                            Messages messages=dataSnapshot.getValue(Messages.class);
                            messagesList.add(messages);
                            messagesAdapter.notifyDataSetChanged();
                        }

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
    }

    @Override
    protected void onStart()
    {
        super.onStart();


    }

    private void sendMessage()
    {


        String messageText=InputMessageText.getText().toString();

        if(TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "please write message...", Toast.LENGTH_SHORT).show();

        }
        else
        {

            String message_sender_ref= "Messages/"+message_sender_id+"/"+message_reseiver_id;
            String message_reseiver_ref="Messages/"+message_reseiver_id+"/"+message_sender_id;

            DatabaseReference userMessagekey=RootRef.child("Messages").child(message_sender_id)
                    .child(message_reseiver_id).push();

            String messagepushid=userMessagekey.getKey();



            Calendar callForDate=Calendar.getInstance();
            SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
            savecurrentdate=currentdate.format(callForDate.getTime());


            Calendar callForTime=Calendar.getInstance();
            SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm aa");
            savecurrenttime=currentTime.format(callForTime.getTime());



            Map messageTextBody=new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("time",savecurrenttime);
            messageTextBody.put("date",savecurrentdate);
            messageTextBody.put("type","text");
            messageTextBody.put("from",message_sender_id);


            Map messageDetailsBody=new HashMap();
            messageDetailsBody.put(message_sender_ref +"/" + messagepushid ,messageTextBody);
            messageDetailsBody.put(message_reseiver_ref +"/" + messagepushid ,messageTextBody);


            RootRef.updateChildren(messageDetailsBody)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ChatActivity.this, "message saved successfully...", Toast.LENGTH_SHORT).show();

                                InputMessageText.setText("");
                            }
                            else
                            {
                                String messageerror=task.getException().toString();
                                Toast.makeText(ChatActivity.this, "Error occurred .."+messageerror, Toast.LENGTH_SHORT).show();
                                InputMessageText.setText("");

                            }

                        }
                    });



        }
    }

    private void DisplaydataofUsers()

    {
        userNmaeTitle.setText(message_reseiver_name);

        RootRef.child("Users").child(message_reseiver_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        String image=dataSnapshot.child("profilimage").getValue().toString();

                        Picasso.with(ChatActivity.this).load(image).placeholder(R.drawable.backgroundprof).into(userchatprofileimage);



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void InitializeFailds()
    {
        chatoolbar=(Toolbar) findViewById(R.id.chat_bar_layout);
        setSupportActionBar(chatoolbar);


        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View action_bar_view=layoutInflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);

        userNmaeTitle=(TextView)findViewById(R.id.custom_user_name);
        userLastseen=(TextView)findViewById(R.id.custom_user_last_seen);
        userchatprofileimage=(CircleImageView)findViewById(R.id.custom_profile_image);


        SendMessageButton=(ImageButton)findViewById(R.id.send_message);
        SelectImageButton=(ImageButton)findViewById(R.id.image_send);
        InputMessageText=(EditText)findViewById(R.id.input_message);

        messagesAdapter=new MessagesAdapter(messagesList);
        userMessageslist=(RecyclerView)findViewById(R.id.messages_list_of_users);

        linearLayoutManager=new LinearLayoutManager(this);
        userMessageslist.setHasFixedSize(true);
        userMessageslist.setLayoutManager(linearLayoutManager);
        userMessageslist.setAdapter(messagesAdapter);

    }
}
