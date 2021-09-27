package com.example.tripa;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    EditText editname,editemail,editpassward,editphone;
Button registerBtn;
FirebaseAuth firebaseAuth;
ImageView googlebutton;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user!=null) {



        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();
        createRequest();
        changeStatusBarColor();
        initComponent();
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verfiyAuth();

            }
        });
    googlebutton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            signIngoogle();
            System.out.println("Clicked");
        }
    });
    }

    private void createRequest() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }
    public void onLoginClick(View view){
        startActivity(new Intent(this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
    public void initComponent(){
        editname=findViewById(R.id.editTextName);
        editemail=findViewById(R.id.editTextEmail);
        editpassward=findViewById(R.id.editTextPassword);
        editphone=findViewById(R.id.editTextMobile);
        registerBtn=findViewById(R.id.cirRegisterButton);
        firebaseAuth=FirebaseAuth.getInstance();
        googlebutton=findViewById(R.id.google);
    }
    public void verfiyAuth(){
        String name=editname.getText().toString();
        String email=editemail.getText().toString();
        String password=editpassward.getText().toString();
        String phone=editphone.getText().toString();
        if (name.isEmpty())
        {
            editname.setError("Name is Required");
            editname.requestFocus();
            return;
        }if (email.isEmpty())
        {
            editemail.setError("Email is Required");
            editemail.requestFocus();
            return;
        }if (phone.isEmpty())
        {
            editphone.setError("Phone is Required");
            editphone.requestFocus();
            return;
        }if (password.isEmpty())
        {
            editpassward.setError("Password is Required");
            editpassward.requestFocus();
            return;
        }if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editemail.setError("Please provide valid email");
            editemail.requestFocus();
            return;
        }if (password.length()<6){
            editpassward.setError("Your password should be more than 6 character");
            editpassward.requestFocus();
            return; }
        Users user=new Users(name,phone,email);
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseDatabase.getInstance().getReference("Users").
                        child(firebaseAuth.getCurrentUser().getUid()).setValue(user);
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

    }


    private void signIngoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                System.out.println("firebaseAuthWithGoogle"+account.getId());

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                System.out.println("exception"+e);
System.out.println("failed here");
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                            System.out.println("signInWithCredential:success");
                      //      onStart();
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
System.out.println("Failed");
                        }
                    }
                });
    }


}