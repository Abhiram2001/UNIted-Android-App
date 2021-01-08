package com.example.united;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.united.Utils.UserStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    CircleImageView profileImageView;
    EditText name, mNumber, aadhar, city, job;
    Button submit;
    Uri imageUri;
    private static final int REQUEST_CODE=101;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    ProgressDialog progressDialog;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);


        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Setup Profile");


        profileImageView = findViewById(R.id.profile_image);
        name = findViewById(R.id.PersonName);
        mNumber = findViewById(R.id.PhoneNumber);
        aadhar = findViewById(R.id.AadharNumber);
        city = findViewById(R.id.inputCity);
        job = findViewById(R.id.jobtext);
        submit = findViewById(R.id.submit);

        progressDialog = new ProgressDialog(SetupActivity.this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
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



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

    }

    public void saveData()
    {


        String nameData = name.getText().toString().trim();
        String phoneNumber = mNumber.getText().toString().trim();
        String Aadhar = aadhar.getText().toString().trim();
        String citydata = city.getText().toString().trim();
        String jobData = job.getText().toString().trim();

        if(nameData.isEmpty()){
            showError(name, "Enter the Name");
        }
        else if(phoneNumber.isEmpty() || phoneNumber.length()!=10){
            showError(mNumber, "Enter the Valid mobile Number");
        }
        else if(citydata.isEmpty()){
            showError(city, "Enter the City");
        }
        else if(jobData.isEmpty()){
            showError(job, "Enter the Job");
        }
        else if(Aadhar.isEmpty()){
            showError(aadhar, "Enter the Aadhar Number");
        }
        else if(!Verhoeff.validateVerhoeff(Aadhar)){
                showError(aadhar, "Enter the Valid Aadhar number");
        }
        else if(imageUri==null){
            Toast.makeText(SetupActivity.this,"Please select the Image", Toast.LENGTH_SHORT).show();
        }
        else{

            progressDialog.setTitle("Setting Up Profile");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            storageReference.child(firebaseUser.getUid()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        storageReference.child(firebaseUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap hashMap = new HashMap();
                                hashMap.put("Name", nameData);
                                hashMap.put("MobileNumber", phoneNumber);
                                hashMap.put("Aadhar", Aadhar);
                                hashMap.put("city", citydata);
                                hashMap.put("job",jobData);
                                hashMap.put("ProfileImage", uri.toString());
                                hashMap.put("status","offline");
                                hashMap.put("role","user");

                                databaseReference.child(firebaseUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        Toast.makeText(SetupActivity.this,"SetUp Profile Completed",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(SetupActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
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
        }
    }

}