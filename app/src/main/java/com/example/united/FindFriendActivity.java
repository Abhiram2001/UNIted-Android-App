package com.example.united;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.bumptech.glide.Glide;
import com.example.united.Utils.UserStatus;
import com.example.united.Utils.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class FindFriendActivity extends AppCompatActivity {

    FirebaseRecyclerOptions<Users>options;
    FirebaseRecyclerAdapter<Users,FindFriendViewHolder>adapter;
    Toolbar toolbar;

    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    RecyclerView recyclerView;
    String dusername, dimageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        dusername = getIntent().getStringExtra("dusername");
        dimageuri = getIntent().getStringExtra("dimageuri");

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Friend");

        recyclerView = findViewById(R.id.findfriendsrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");


        LoadUsers("");
    }

    private void LoadUsers(String s) {
        Query query= mUserRef.orderByChild("Name").startAt(s).endAt(s+"\uf8ff");
        options= new FirebaseRecyclerOptions.Builder<Users>().setQuery(query,Users.class).build();
        adapter = new FirebaseRecyclerAdapter<Users, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, int position, @NonNull Users model) {

                if(!mUser.getUid().equals(getRef(position).getKey().toString()))
                {
//                    Picasso.get().load(model.getProfileImage()).rotate(getCameraPhotoOrientation(model.getProfileImage())).into(holder.profileImage);
                    holder.username.setText(model.getName());
                    holder.profession.setText(model.getJob());

                    Glide
                            .with(FindFriendActivity.this)
                            .load(model.getProfileImage())
                            .centerCrop()
                            .placeholder(R.drawable.profile)
                            .into(holder.profileImage);


                }
                else
                {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FindFriendActivity.this,ViewFriendActivity.class);
                        intent.putExtra("userKey", getRef(position).getKey().toString());
                        intent.putExtra("dusername",dusername);
                        intent.putExtra("dimageuri",dimageuri);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_find_friend , parent, false);
                return new FindFriendViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LoadUsers(newText);
                return false;
            }
        });

        return true;
    }

}