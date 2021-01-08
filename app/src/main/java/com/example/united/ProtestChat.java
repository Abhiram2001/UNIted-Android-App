package com.example.united;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.united.Utils.Chat;
import com.example.united.Utils.UserStatus;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProtestChat extends AppCompatActivity {

    Toolbar chattoolbar;
    RecyclerView chatrecyclerView;
    EditText inputsms;
    ImageView btnsendchat;
    CircleImageView ProfileImageAppbar;
    TextView usernameAppbar,status;
    String ProtestID;
    DatabaseReference mUserref,smsRef,pref;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseRecyclerOptions<Chat> options;
    FirebaseRecyclerAdapter<Chat,ChatMyViewHolder> adapter;

    String myProfileImageLink,myUsername;

    String URL = "https://fcm.googleapis.com/fcm/send";
    RequestQueue requestQueue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protest_chat);


        chattoolbar=findViewById(R.id.app_bar_chat);
        setSupportActionBar(chattoolbar);

        ProtestID = getIntent().getStringExtra("ProtestID");
        requestQueue = Volley.newRequestQueue(this);


        chatrecyclerView= findViewById(R.id.chatrecyclerview);
        inputsms = findViewById(R.id.inputsms);
        btnsendchat = findViewById(R.id.btnsendchat);
        ProfileImageAppbar = findViewById(R.id.ProfileImageAppbar);

        usernameAppbar = findViewById(R.id.usernameAppbar);
        status = findViewById(R.id.status);
        mAuth = FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();
        mUserref = FirebaseDatabase.getInstance().getReference().child("Users");
        smsRef = FirebaseDatabase.getInstance().getReference().child("ProtestMessages");
        pref = FirebaseDatabase.getInstance().getReference().child("Protests");


        pref.child(ProtestID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Picasso.get().load(snapshot.child("ProtestImage").getValue().toString()).into(ProfileImageAppbar);
                    usernameAppbar.setText(snapshot.child("ProtestName").getValue().toString());
                    status.setText(snapshot.child("Desc").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        chatrecyclerView.setLayoutManager(new LinearLayoutManager(this));


        btnsendchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });
        LoadSMS();
        LoadProfile();

    }


    private void LoadProfile() {

        mUserref.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    myProfileImageLink = snapshot.child("ProfileImage").getValue().toString();
                    myUsername = snapshot.child("Name").getValue().toString();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProtestChat.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void LoadSMS() {
        options = new FirebaseRecyclerOptions.Builder<Chat>().setQuery(smsRef.child(ProtestID),Chat.class).build();
        adapter = new FirebaseRecyclerAdapter<Chat, ChatMyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatMyViewHolder holder, int position, @NonNull Chat model) {
                if(model.getUserID().equals(mUser.getUid()))
                {

                    holder.firstUserProfile.setVisibility(View.GONE);
                    holder.firstUserText.setVisibility(View.GONE);
                    holder.secondUserText.setVisibility(View.VISIBLE);
                    holder.secondUserProfile.setVisibility(View.VISIBLE);

                    holder.secondUserText.setText(model.getSms());
                    Glide
                            .with(ProtestChat.this)
                            .load(myProfileImageLink)
                            .centerCrop()
                            .placeholder(R.drawable.profile)
                            .into(holder.secondUserProfile);
                }

                else
                {
                    String[] otherImage = new String[1];;


                    holder.firstUserProfile.setVisibility(View.VISIBLE);
                    holder.firstUserText.setVisibility(View.VISIBLE);
                    holder.secondUserText.setVisibility(View.GONE);
                    holder.secondUserProfile.setVisibility(View.GONE);

                    holder.firstUserText.setText(model.getSms());



                    mUserref.child(model.getUserID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                 otherImage[0] = snapshot.child("ProfileImage").getValue().toString();
                                Log.d("Tag",""+ otherImage[0]);

                                Glide
                                        .with(ProtestChat.this)
                                        .load(otherImage[0])
                                        .centerCrop()
                                        .placeholder(R.drawable.profile)
                                        .into(holder.firstUserProfile);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ProtestChat.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

                    holder.firstUserProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProtestChat.this,ViewFriendActivity.class);
                            intent.putExtra("userKey",model.getUserID());
                            startActivity(intent);
                        }
                    });

                }

            }

            @NonNull
            @Override
            public ChatMyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleview_sms,parent,false);
                return new ChatMyViewHolder(view);
            }
        };
        adapter.startListening();
        chatrecyclerView.setAdapter(adapter);

    }

    private void sendSMS() {
        String SMS = inputsms.getText().toString();
        if(SMS.isEmpty())
        {
            Toast.makeText(ProtestChat.this,"Please Enter Something",Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap hashMap = new HashMap();
            hashMap.put("sms",SMS);
            hashMap.put("status","unseen");
            hashMap.put("userID", mUser.getUid());
            smsRef.child(ProtestID).push().updateChildren(hashMap);
        }
    }

    private void sendNotification(String sms) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("to","/topics");
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("title","Message from"+myUsername);
            jsonObject1.put("body",sms);
            jsonObject.put("notification",jsonObject1);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String , String>map = new HashMap<>();
                    map.put("content-type","application/json");
                    map.put("authorization","key=AAAAKGPAh9w:APA91bEVbO3zDhrV9kfjSkG4AQcyFHLHR6O22_hbNBfpcmyskQFmC0L7gSxqNR10Y1ZiHEXRaQEXpGhqOJBZIFCkDJqwQTWjGZ46NYu4lKwnGTYIovOnQKr0yjkGTSSSfol65pWRIJgR");

                    return map;
                }
            };
        }

        catch (JSONException e) {
            e.printStackTrace();
        }
    }

}