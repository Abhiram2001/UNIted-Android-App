package com.example.united;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.united.R;
import com.example.united.Utils.Incidents;
import com.example.united.Utils.UserStatus;
import com.example.united.Utils.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AllIncidents extends AppCompatActivity {

    FirebaseRecyclerOptions<Incidents> options;
    FirebaseRecyclerAdapter<Incidents,IncidentViewHolder> adapter;
    Toolbar toolbar;

    DatabaseReference mUserRef,pmref,mRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    RecyclerView recyclerView;

    FloatingActionButton fab;
    String myProfileImageLink,myUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_incidents);

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Protests");

        recyclerView = findViewById(R.id.findfriendsrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Protests");
        pmref = FirebaseDatabase.getInstance().getReference().child("PersonsProtests");
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");



        fab = findViewById(R.id.fab);

        LoadIncidents("");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists())
                        {
                            if(snapshot.child("role").getValue().toString().equals("Admin"))
                            {
                                startActivity(new Intent(getApplicationContext(), ApproveProtest.class));
                            }

                            else
                            {
                                startActivity(new Intent(getApplicationContext(), AddProtest.class));
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AllIncidents.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });


    }




    private void LoadIncidents(String s) {
        Query query= mUserRef.orderByChild("ProtestName").startAt(s).endAt(s+"\uf8ff");
        options= new FirebaseRecyclerOptions.Builder<Incidents>().setQuery(query,Incidents.class).build();
        adapter = new FirebaseRecyclerAdapter<Incidents, IncidentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull IncidentViewHolder holder, int position, @NonNull Incidents model) {

                
                holder.username.setText(model.getProtestName());
                holder.profession.setText(model.getDesc());
                holder.benifitTo.setText(model.getBenifitTo());
                holder.aganist.setText(model.getAganist());

                Glide
                        .with(AllIncidents.this)
                        .load(model.getProtestImage())
                        .centerCrop()
                        .placeholder(R.drawable.profile)
                        .into(holder.profileImage);

                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap hashMap = new HashMap();
                        hashMap.put("ProtestImage", model.getProtestImage());
                        hashMap.put("ProtestName",model.getProtestName());
                        hashMap.put("Desc",model.getDesc());
                        pmref.child(mUser.getUid()).child(getRef(position).getKey().toString()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                Toast.makeText(AllIncidents.this,"Succesfully Joined Protest",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public IncidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_protest , parent, false);
                return new IncidentViewHolder(view);
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
                LoadIncidents(newText);
                return false;
            }
        });
        return true;
    }


}