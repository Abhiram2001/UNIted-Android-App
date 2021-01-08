package com.example.united;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class AddProtest extends AppCompatActivity {

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
        setContentView(R.layout.activity_add_protest);


        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Request Protest");


        profileImageView = findViewById(R.id.profile_image);
        name = findViewById(R.id.PersonName);
        mNumber = findViewById(R.id.PhoneNumber);
        aadhar = findViewById(R.id.AadharNumber);
        city = findViewById(R.id.inputCity);
        job = findViewById(R.id.jobtext);
        submit = findViewById(R.id.submit);

        progressDialog = new ProgressDialog(AddProtest.this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ProtestRequest");
        storageReference = FirebaseStorage.getInstance().getReference().child("ProtestImages");


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


        String protestName = name.getText().toString().trim();
        String protestTopic = mNumber.getText().toString().trim();
        String benifitTo = aadhar.getText().toString().trim();
        String aganist = city.getText().toString().trim();
        String hashtag = job.getText().toString().trim();

        if(protestName.isEmpty()){
            showError(name, "Enter the Name");
        }
        else if(protestTopic.isEmpty()){
            showError(mNumber, "Enter the Valid Topic");
        }
        else if(benifitTo.isEmpty()){
            showError(aadhar, "Enter the Proper persons benifited");
        }
        else if(aganist.isEmpty()){
            showError(city, "Aganist Whose the Protest is going");
        }
        else if(imageUri==null){
            Toast.makeText(AddProtest.this,"Please select the Image", Toast.LENGTH_SHORT).show();
        }
        else{

            progressDialog.setTitle("Requesting Protest");
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
                                hashMap.put("ProtestName", protestName);
                                hashMap.put("Desc", protestTopic);
                                hashMap.put("ProtestImage", uri.toString());
                                hashMap.put("benifitTo", benifitTo);
                                hashMap.put("aganist",aganist);
                                hashMap.put("hashtag",hashtag);
                                hashMap.put("status","Going");

                                databaseReference.push().updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(AddProtest.this, MainActivity.class);
                                        startActivity(intent);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        Toast.makeText(AddProtest.this,"Request Completed",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(AddProtest.this,e.toString(),Toast.LENGTH_SHORT).show();
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