package com.example.united.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserStatus {


    public UserStatus() {
    }

    public String tokenbefore="$";
    public void setUserStatus(String S)
    {
        FirebaseAuth  mAuth;
        FirebaseUser mUser;

        mAuth = FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        databaseReference.child("status").setValue(S);
    }

    public void setUsertoken(String S)
    {
        FirebaseAuth  mAuth;
        FirebaseUser mUser;

        mAuth = FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        databaseReference.child("Token").setValue(S);
    }


    public String getUid()
    {
        FirebaseAuth  mAuth;
        FirebaseUser mUser;

        mAuth = FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();

        return String.valueOf(mUser.getUid());

    }

}
