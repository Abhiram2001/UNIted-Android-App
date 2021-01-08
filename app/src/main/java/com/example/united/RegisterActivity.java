package com.example.united;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText email, password, cPassword;
    CheckBox checkBox;
    ImageButton signup;
    Button signin;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView login = (TextView) findViewById(R.id.logintxt);
        login.startAnimation(AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.textanim));

        progressBar = (ProgressBar) findViewById(R.id.indeterminateBar);
        progressBar.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, 0xFFFFFF));

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        cPassword = (EditText) findViewById(R.id.confirmPassword);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        signup = (ImageButton) findViewById(R.id.signup);
        signin = (Button) findViewById(R.id.signin);

        firebaseAuth = FirebaseAuth.getInstance();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AttemptingRegistration();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

    }

    public void AttemptingRegistration() {

        signup.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        String mailData = email.getText().toString().trim();
        String passData = password.getText().toString().trim();
        String confirmpassData = cPassword.getText().toString().trim();

        if (TextUtils.isEmpty(mailData)) {
            email.setError("Required Email ID.");
            email.requestFocus();
            signup.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            return;
        } else if (TextUtils.isEmpty(passData) || passData.length() < 5) {
            password.setError("Required Password.");
            password.requestFocus();
            signup.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }else if (!checkBox.isChecked()) {
            checkBox.setError("Please Agree Terms & Conditions");
            checkBox.requestFocus();
            signup.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            return;
        } else if (TextUtils.isEmpty(confirmpassData) || !confirmpassData.equals(passData)) {
            cPassword.setError("Password Not Matching");
            cPassword.requestFocus();
            signup.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            return;
        } else {

            firebaseAuth.createUserWithEmailAndPassword(mailData, passData).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(RegisterActivity.this, SetupActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                    else{
                        signup.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });

        }


    }
}