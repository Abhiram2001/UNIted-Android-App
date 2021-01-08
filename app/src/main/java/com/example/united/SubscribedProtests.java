package com.example.united;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.united.Utils.Friends;
import com.example.united.Utils.Incidents;
import com.example.united.Utils.MyIncidents;
import com.example.united.Utils.UserStatus;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class SubscribedProtests extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView friendRecyclerView;

    FirebaseRecyclerOptions<MyIncidents> options;
    FirebaseRecyclerAdapter<MyIncidents,FriendMyViewHolder> adapter;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_protests);

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Andolans");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        friendRecyclerView = findViewById(R.id.friendrecyclerview);

        friendRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("PersonsProtests");


        LoadMyProtests("");

    }




    private void LoadMyProtests(String s) {

        Query query = mUserRef.child(mUser.getUid()).orderByChild("ProtestName").startAt(s).endAt(s + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<MyIncidents>().setQuery(query, MyIncidents.class).build();
        adapter = new FirebaseRecyclerAdapter<MyIncidents, FriendMyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendMyViewHolder holder, int position, @NonNull MyIncidents model) {
                Glide
                        .with(SubscribedProtests.this)
                        .load(model.getProtestImage())
                        .centerCrop()
                        .placeholder(R.drawable.profile)
                        .into(holder.profileImage);

                holder.username.setText(model.getProtestName());


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SubscribedProtests.this, ProtestChat.class);
                        intent.putExtra("ProtestID", getRef(position).getKey().toString());
                        startActivity(intent);
                    }
                });


            }

            @NonNull
            @Override
            public FriendMyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_friend, parent, false);
                return new FriendMyViewHolder(view);
            }


        };
        adapter.startListening();
        friendRecyclerView.setAdapter(adapter);
    }

}