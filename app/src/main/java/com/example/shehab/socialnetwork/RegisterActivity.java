package com.example.shehab.socialnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {
    private ProgressDialog loadingbar;

    private DatabaseReference storUserDefaultDataReference;

    private Toolbar mtoolbar;

    private EditText userEmail;
    private EditText userPassword;
    private EditText userConfirmPassword;
    private Button createaccountbutton;
    private FirebaseAuth mAuth;
    private ProgressDialog mprogress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        mAuth=FirebaseAuth.getInstance();

        userEmail=(EditText)findViewById(R.id.Email);
        userPassword=(EditText)findViewById(R.id.password);
        userConfirmPassword=(EditText)findViewById(R.id.confirmPassword);

        loadingbar=new ProgressDialog(this);

        createaccountbutton=(Button)findViewById(R.id.createaccount);

        createaccountbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });

        mprogress=new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currnetuser=mAuth.getCurrentUser();

        if(currnetuser!=null)
        {
            sendUsertomainActivity();
        }

    }

    private void sendUsertomainActivity() {
        Intent MainIntent=new Intent(RegisterActivity.this,MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();
    }
    private void CreateNewAccount() {
        String Email=userEmail.getText().toString();
        String password=userPassword.getText().toString();
        String confirmPassword=userConfirmPassword.getText().toString();

        if(TextUtils.isEmpty(Email)){
            Toast.makeText(getApplicationContext(),"please enter you Email",Toast.LENGTH_LONG).show();

        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"please enter your password", Toast.LENGTH_LONG).show();

        }else if(TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(getApplicationContext(),"please confirm your password",Toast.LENGTH_LONG).show();

        }else if(!password.equals(confirmPassword)){
            Toast.makeText(getApplicationContext(),"the password don't match please write again",Toast.LENGTH_LONG).show();
        }else{
            mprogress.setTitle("Creating account");
            mprogress.setMessage("wait while Creating account");
            mprogress.show();
            mprogress.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(Email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if(task.isSuccessful()){
                                sendusertosetupactivity();
                                Toast.makeText(RegisterActivity.this, "this Authentication successfully ", Toast.LENGTH_SHORT).show();
                                mprogress.dismiss();
                            }else {
                                String message=task.toString();
                                Toast.makeText(getApplicationContext(),"Error occurred "+message,Toast.LENGTH_LONG).show();
                                mprogress.dismiss();
                            }
                        }
                    });
        }
    }

    private void sendusertosetupactivity() {
        Intent setupuserintent=new Intent(getApplicationContext(),SetupActivity.class);
        setupuserintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupuserintent);
    }

}

















