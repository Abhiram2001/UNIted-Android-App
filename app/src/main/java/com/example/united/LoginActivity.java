package com.example.united;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText email , password;
    FirebaseAuth firebaseAuth;
    TextView forgotPass;
    ProgressBar progressBar;
    private CheckBox checkBox;
    private SharedPreferences mPrefs;
    private static final String PREFS_NAME = "PrefsFile";
    ImageButton signin;
    Button signup;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference,updrefernce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView login = (TextView) findViewById(R.id.logintxt);
        login.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this,R.anim.textanim));


        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        checkBox = (CheckBox)findViewById(R.id.checkbox);
        forgotPass = (TextView) findViewById(R.id.forgotPass);
        progressBar = (ProgressBar)findViewById(R.id.indeterminateBar);
        progressBar.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, 0xFFFFFF));


        mPrefs = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        getPreferencesData();



        signup = (Button) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(LoginActivity.this,"SignUP Button Clicked",Toast.LENGTH_LONG).show();
                signUp();
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();


        signin =(ImageButton)findViewById(R.id.signin);

        signin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                AttemptLogin();
            }
        });


        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetMail =new EditText(view.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("  Reset Password  ");
                passwordResetDialog.setMessage("Enter your Email ID for Account Recovery.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String Mail= resetMail.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(Mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this,"Reset Link is Sent to your Mail",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this,"Error! Reset Link is not Sent"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(LoginActivity.this,"Try to sign in with correct Email and Password",Toast.LENGTH_SHORT).show();
                    }
                });
                passwordResetDialog.create().show();

            }
        });

    }

    private void getPreferencesData(){
        SharedPreferences sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if(sp.contains("pref_name")){
            String u = sp.getString("pref_name","not Found");
            email.setText(u.toString());
        }
        if(sp.contains("pref_pass")){
            String p = sp.getString("pref_pass","not Found");
            password.setText(p.toString());
        }
        if(sp.contains("pref_check")){
            Boolean b = sp.getBoolean("pref_check",false);
            checkBox.setChecked(b);
        }
    }

    public void signUp()
    {
        Intent intent =new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void AttemptLogin(){

        String mailData = email.getText().toString().trim();
        String passData = password.getText().toString().trim();
        signin.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        if(TextUtils.isEmpty(mailData)){
            email.setError("Required Email ID.");
            signin.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        else if(TextUtils.isEmpty(passData) || passData.length() < 5){
            password.setError("Required Password.");
            signin.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        else{


            if(checkBox.isChecked()){

                Boolean boolIsChecked = checkBox.isChecked();
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString("pref_name", mailData);
                editor.putString("pref_pass",passData);
                editor.putBoolean("pref_check",boolIsChecked);
                editor.apply();
                Toast.makeText(getApplicationContext(),"Details Remembered", Toast.LENGTH_SHORT).show();

            }else{
                mPrefs.edit().clear().apply();
            }

            firebaseAuth.signInWithEmailAndPassword(mailData, passData).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {

                        Toast.makeText(LoginActivity.this,"Logged in Successfully",Toast.LENGTH_SHORT).show();

                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                        firebaseUser = firebaseAuth.getCurrentUser();
                        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    signin.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    email.getText().clear();
                                    password.getText().clear();
                                    finish();
                                }
                                else
                                {
                                    Intent intent = new Intent(LoginActivity.this, SetupActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    signin.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    email.getText().clear();
                                    password.getText().clear();
                                    finish();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,"Error"+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        signin.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });

        }
    }

}