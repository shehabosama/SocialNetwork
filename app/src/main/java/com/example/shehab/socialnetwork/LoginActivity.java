package com.example.shehab.socialnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private FirebaseAuth mAuth;
    private Button Loginbutton;
    private EditText LoginEmail;
    private EditText LoginPassword;
    private ProgressDialog Loginbar;
    private DatabaseReference usresReference;
    private TextView linkRegister;
    private static final int RC_SIGN_IN=1;
    private GoogleApiClient mGoogleSignInClient;
    private static final String TAG="LoginActivity";
    private TextView text_forgotten;

    private ImageView google_sgin_in_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        mAuth=FirebaseAuth.getInstance();


        LoginEmail=(EditText)findViewById(R.id.loginEmail);
        LoginPassword=(EditText)findViewById(R.id.loginPassword);
        Loginbutton=(Button)findViewById(R.id.loginButton);
        linkRegister=(TextView)findViewById(R.id.register_account_link);
        google_sgin_in_button=(ImageView)findViewById(R.id.google_sign_button);
        text_forgotten=(TextView)findViewById(R.id.Forgotten_password);
        linkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               sendUsretoRegisteActivity();
            }
        });



        text_forgotten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ResetPasswordIntent=new Intent(getApplicationContext(),ResetpasswordActivity.class);
                startActivity(ResetPasswordIntent);
            }
        });

        Loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=LoginEmail.getText().toString();
                String password=LoginPassword.getText().toString();
                LoginuserAccount(email,password);
            }
        });

        Loginbar=new ProgressDialog(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();



        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(LoginActivity.this, "connection to google sign in failed ..", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();



        google_sgin_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


    }

    private void signIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
        {
            Loginbar.setTitle("Login google account");
            Loginbar.setMessage("Now will done login the account ");
            Loginbar.show();
            Loginbar.setCanceledOnTouchOutside(true);


            GoogleSignInResult result =Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess())
            {
                GoogleSignInAccount account=result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(this, "please wait while , getting your auth account.. ", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "can't get Auth result", Toast.LENGTH_SHORT).show();
                Loginbar.dismiss();
            }


        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");

                            sendUsertomainActivity();
                            Loginbar.dismiss();


                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            String message = task.getException().getMessage();
                            Toast.makeText(getBaseContext(), "error occurred" + message, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            Loginbar.dismiss();

                        }
                    }
                });
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
        Intent MainIntent=new Intent(LoginActivity.this,MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();
    }

    private void sendUsretoRegisteActivity() {
        Intent intenttoRegister=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intenttoRegister);
    }


    public void LoginuserAccount(String email, String password)
    {

        if(TextUtils.isEmpty(email))
        {

            Toast.makeText(getBaseContext(),"enter your email", Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(password))
        {

            Toast.makeText(getBaseContext(),"enter your password", Toast.LENGTH_LONG).show();
        }else
            {
            Loginbar.setTitle("Login Account");
            Loginbar.setMessage("Now will done login the account ");
            Loginbar.show();
            mAuth.signInWithEmailAndPassword(email,password)
                 .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                 {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task)
                     {

                         if (task.isSuccessful())
                         {
                             Intent mainIntent=new Intent(getApplicationContext(),MainActivity.class);
                             mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                             startActivity(mainIntent);
                             finish();
                         }
                         else
                             {
                                 String messgae=task.toString();
                                 Toast.makeText(getBaseContext(),"make sure the email or password is corrected"+messgae, Toast.LENGTH_LONG).show();
                             }
                         Loginbar.dismiss();
                     }
                 });

        }
    }
}
