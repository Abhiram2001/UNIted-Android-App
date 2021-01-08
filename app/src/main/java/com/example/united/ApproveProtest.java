package com.example.united;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.united.Utils.PendingProtests;
import com.example.united.Utils.UserStatus;
import com.example.united.Utils.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class ApproveProtest extends AppCompatActivity {

    FirebaseRecyclerOptions<PendingProtests> options;
    FirebaseRecyclerAdapter<PendingProtests,ApproveProtestViewHolder> adapter;
    Toolbar toolbar;

    DatabaseReference mUserRef,requestRef, approveRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_protest);


        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Approve Protest");

        recyclerView = findViewById(R.id.approveprotestrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("ProtestRequest");
        requestRef = FirebaseDatabase.getInstance().getReference().child("ProtestRequest");
        approveRef = FirebaseDatabase.getInstance().getReference().child("Protests");


        LoadPendingProtests("");

    }

    private void LoadPendingProtests(String s) {
        Query query= mUserRef.orderByChild("ProtestName").startAt(s).endAt(s+"\uf8ff");
        options= new FirebaseRecyclerOptions.Builder<PendingProtests>().setQuery(query,PendingProtests.class).build();
        adapter = new FirebaseRecyclerAdapter<PendingProtests, ApproveProtestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ApproveProtestViewHolder holder, int position, @NonNull PendingProtests model) {


                holder.username.setText(model.getProtestName());
                holder.profession.setText(model.getDesc());
                holder.benifit.setText(model.getBenifitTo());
                holder.aganist.setText(model.getAganist());

                Glide
                        .with(ApproveProtest.this)
                        .load(model.getProtestImage())
                        .centerCrop()
                        .placeholder(R.drawable.profile)
                        .into(holder.profileImage);



                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestRef.child(getRef(position).getKey().toString()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(ApproveProtest.this,"Request Cancelled",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(ApproveProtest.this,"Request Not Cancelled Due To" +task.getException().toString() ,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });


                holder.button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestRef.child(getRef(position).getKey().toString()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(ApproveProtest.this,"Request Approved",Toast.LENGTH_SHORT).show();


                                    HashMap hashMap = new HashMap();
                                    hashMap.put("ProtestName", model.getProtestName());
                                    hashMap.put("Desc", model.getDesc());
                                    hashMap.put("ProtestImage", model.getProtestImage());
                                    hashMap.put("benifitTo", model.getBenifitTo());
                                    hashMap.put("aganist",model.getAganist());
                                    hashMap.put("hashtag",model.getHashtag());
                                    hashMap.put("status",model.getStatus());

                                    approveRef.push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if(task.isSuccessful())
                                            {
                                                notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }
                                else
                                {
                                    Toast.makeText(ApproveProtest.this,"Request Not Approved Due To" +task.getException().toString() ,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });








            }

            @NonNull
            @Override
            public ApproveProtestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_approve_protest , parent, false);
                return new ApproveProtestViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

}