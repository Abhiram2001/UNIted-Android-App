package com.example.united;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.united.Utils.UserStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_CODE=101;

    CircleImageView profileImageView;
    EditText inputUsername, inputcity, inputMobilenumber, inputJob;
    Button btnUpdate;

    Boolean picturechange;
    Uri imageUri;

    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    Dialog progressDialog;

    String Aadhar;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        profileImageView = findViewById(R.id.userImageView);
        inputUsername = findViewById(R.id.inputUsername);
        inputcity= findViewById(R.id.inputcity);
        inputMobilenumber = findViewById(R.id.inputMobilenumber);
        inputJob= findViewById(R.id.inputtJob);
        btnUpdate = findViewById(R.id.btnUpdate);
        picturechange = false;

        progressDialog = new ProgressDialog(ProfileActivity.this);


        mAuth = FirebaseAuth.getInstance();
        mUser =mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfileImages");


        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });


        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String profileImageUrl = snapshot.child("ProfileImage").getValue().toString();
                    String name = snapshot.child("Name").getValue().toString();
                    String city = snapshot.child("city").getValue().toString();
                    String job = snapshot.child("job").getValue().toString();
                    String mobilenumber = snapshot.child("MobileNumber").getValue().toString();
                    Aadhar = snapshot.child("Aadhar").getValue().toString();

                    //Picasso.get().load(profileImageUrl).into(profileImageView);

                    Glide
                            .with(ProfileActivity.this)
                            .load(profileImageUrl)
                            .centerCrop()
                            .placeholder(R.drawable.profile)
                            .into(profileImageView);

                    inputUsername.setText(name);
                    inputcity.setText(city);
                    inputJob.setText(job);
                    inputMobilenumber.setText(mobilenumber);
                }
                else {
                    Toast.makeText(ProfileActivity.this,"Data not Exsists",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this,""+error.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showError(EditText input, String msg)
    {
        input.setError(msg);
        input.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
            picturechange =true;
        }
    }



    public void saveData()
    {

        String nameData = inputUsername.getText().toString().trim();
        String phoneNumber = inputMobilenumber.getText().toString().trim();
        String citydata = inputcity.getText().toString().trim();
        String jobData = inputJob.getText().toString().trim();

        if(nameData.isEmpty()){
            showError(inputUsername, "Enter the Name");
        }
        else if(phoneNumber.isEmpty() || phoneNumber.length()!=10){
            showError(inputMobilenumber, "Enter the Valid mobile Number");
        }
        else if(citydata.isEmpty()){
            showError(inputcity, "Enter the City");
        }
        else if(jobData.isEmpty()){
            showError(inputJob, "Enter the Job");
        }
        else{

            progressDialog.setTitle("Updating Profile");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            databaseReference.child(mUser.getUid()).child("MobileNumber").setValue(phoneNumber);
            databaseReference.child(mUser.getUid()).child("city").setValue(citydata);
            databaseReference.child(mUser.getUid()).child("job").setValue(jobData);
            databaseReference.child(mUser.getUid()).child("Name").setValue(nameData);

            if(picturechange)
            {
                if(imageUri!=null)
                {
                    storageReference.child(mUser.getUid()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                storageReference.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        databaseReference.child(mUser.getUid()).child("ProfileImage").setValue(uri);
                                    }
                                });
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(ProfileActivity.this,"Please select the Image", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }





}