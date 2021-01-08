package com.example.united;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private static final int REQUEST_CODE=101;

    String type;
    Toolbar chattoolbar;
    RecyclerView chatrecyclerView;
    EditText inputsms;
    ImageView btnsendchat;
    CircleImageView ProfileImageAppbar;
    TextView usernameAppbar,status;
    String OtherUserID;
    DatabaseReference mUserref,smsRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseRecyclerOptions<Chat>options;
    FirebaseRecyclerAdapter<Chat,ChatMyViewHolder>adapter;


    String OtherUsername,OtherUserProfileImageLink,OtherUserStatus;
    String myProfileImageLink,myUsername;
    RequestQueue requestQueue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);





        chattoolbar=findViewById(R.id.app_bar_chat);
        setSupportActionBar(chattoolbar);

        OtherUserID = getIntent().getStringExtra("OtherUserID");
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
        smsRef = FirebaseDatabase.getInstance().getReference().child("Message");

        chatrecyclerView.setLayoutManager(new LinearLayoutManager(this));


        type= "Text";

        LoadOtherUser();
        LoadSMS();
        LoadProfile();

        btnsendchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS(type);
            }
        });


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
                Toast.makeText(ChatActivity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void LoadSMS() {
        Log.d("tag","function called");
        options = new FirebaseRecyclerOptions.Builder<Chat>().setQuery(smsRef.child(mUser.getUid()).child(OtherUserID),Chat.class).build();
        adapter = new FirebaseRecyclerAdapter<Chat, ChatMyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatMyViewHolder holder, int position, @NonNull Chat model) {
                if(model.getUserID().equals(mUser.getUid()))
                {

                    Log.d("tag","bind view holder called");
                    holder.firstUserProfile.setVisibility(View.GONE);
                    holder.firstUserText.setVisibility(View.GONE);
                    holder.secondUserText.setVisibility(View.VISIBLE);
                    holder.secondUserProfile.setVisibility(View.VISIBLE);

                    holder.secondUserText.setText(model.getSms());
                    Glide
                            .with(ChatActivity.this)
                            .load(myProfileImageLink)
                            .centerCrop()
                            .placeholder(R.drawable.profile)
                            .into(holder.secondUserProfile);

//                    Picasso.get().load(myProfileImageLink).into(holder.secondUserProfile);

                }

                else
                {
                    Log.d("tag","bind view holder called");
                    holder.firstUserProfile.setVisibility(View.VISIBLE);
                    holder.firstUserText.setVisibility(View.VISIBLE);
                    holder.secondUserText.setVisibility(View.GONE);
                    holder.secondUserProfile.setVisibility(View.GONE);

                    holder.firstUserText.setText(model.getSms());

                    Glide
                            .with(ChatActivity.this)
                            .load(OtherUserProfileImageLink)
                            .centerCrop()
                            .placeholder(R.drawable.profile)
                            .into(holder.firstUserProfile);

//                    Picasso.get().load(OtherUserProfileImageLink).into(holder.firstUserProfile);

                }

            }


            @NonNull
            @Override
            public ChatMyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleview_sms,parent,false);
                Log.d("tag","view holder called");
                return new ChatMyViewHolder(view);
            }
        };
        adapter.startListening();
        chatrecyclerView.setAdapter(adapter);

    }

    private void sendSMS(String type) {
        String SMS = inputsms.getText().toString();
        if(SMS.isEmpty())
        {
            Toast.makeText(ChatActivity.this,"Please Enter Something",Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap hashMap = new HashMap();
            hashMap.put("sms",SMS);
            hashMap.put("status","unseen");
            hashMap.put("userID", mUser.getUid());
            smsRef.child(OtherUserID).child(mUser.getUid()).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        smsRef.child(mUser.getUid()).child(OtherUserID).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful())
                                {

                                    inputsms.setText(null);
                                    Toast.makeText(ChatActivity.this,"SMS Sent",Toast.LENGTH_SHORT).show();
                                    getToken(SMS,OtherUserID,"chat","UNIted");
                                }
                            }
                        });
                    }
                }
            });

        }
    }

    private void LoadOtherUser() {
        mUserref.child(OtherUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    OtherUsername = snapshot.child("Name").getValue().toString();
                    OtherUserProfileImageLink = snapshot.child("ProfileImage").getValue().toString();
                    OtherUserStatus = snapshot.child("status").getValue().toString();
                    //Picasso.get().load(OtherUserProfileImageLink).into(ProfileImageAppbar);

                    Glide
                            .with(ChatActivity.this)
                            .load(OtherUserProfileImageLink)
                            .centerCrop()
                            .placeholder(R.drawable.profile)
                            .into(ProfileImageAppbar);

                    usernameAppbar.setText(OtherUsername);
                    status.setText(OtherUserStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void getToken(String message, String hisID, String hisImage, String ChatId)
    {
        Log.d("notification",hisID);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(hisID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("Token").getValue().toString();
                String name = snapshot.child("Name").getValue().toString();

                JSONObject to = new JSONObject();
                JSONObject data = new JSONObject();
                try {
                    data.put("title",myUsername);
                    data.put("message",message);
                    data.put("hisId",hisID);
                    data.put("hisImage",hisImage);
                    data.put("chatId",ChatId);

                    to.put("to",token);
                    to.put("data",data);

                    SendNotification(to);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendNotification(JSONObject to) {
        String FCMURL = "https://fcm.googleapis.com/fcm/send";
        final String Serverkey = "AAAAKGPAh9w:APA91bG7jdXuxoHqXw8XSrMs_IreaE8YW30ee0wLjlG_ZQ8VRZbyos9ZY-bPkRCOnLrDKWOUErtzfFDZqlp6gWelnjdvRTZHsGKXkg1ezKc9KXQZSXpsi62toy6Dzuri602qztvvmNjm";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,FCMURL,to,response -> {
            Log.d("notification", "SendNotification R"+response);
        }, error -> {
            Log.d("notification", "SendNotification R"+error);
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> map= new HashMap<>();
                map.put("Authorization","key="+Serverkey);
                map.put("Content-Type","application/json");
                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }


}