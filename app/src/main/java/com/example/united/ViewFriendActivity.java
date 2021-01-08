package com.example.united;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.united.Utils.UserStatus;
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
import java.util.HashSet;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewFriendActivity extends AppCompatActivity {

    DatabaseReference mUserRef,requestRef,friendRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    CircleImageView circleImageView;
    TextView username,city;
    Button btnPerform, btnDecline;

    String sprofileImage , susername, scity;
    String CurentState= "NotFriend";
    String dusername, dimageuri;
    String userID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);
        userID = getIntent().getStringExtra("userKey");
        dusername = getIntent().getStringExtra("dusername");
        dimageuri = getIntent().getStringExtra("dimageuri");
        Toast.makeText(this,""+userID,Toast.LENGTH_SHORT).show();


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        circleImageView = findViewById(R.id.circleImageView);
        username= findViewById(R.id.viewfriendUsername);
        city = findViewById(R.id.viewfriendAddress);


        btnDecline = findViewById(R.id.btnDecline);
        btnPerform = findViewById(R.id.btnPerform);


        LoadUser();

        btnPerform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformAction(userID);
            }
        });

        checkUserExsistence(userID);

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Unfriend(userID);
            }
        });
    }

    private void Unfriend(String userID) {

        if(CurentState.equals("friend"))
        {
            friendRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        friendRef.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener((new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(ViewFriendActivity.this,"Succesfuly Un-Friended", Toast.LENGTH_SHORT).show();
                                    CurentState = "NotFriend";
                                    btnPerform.setText("Send Request");
                                    btnDecline.setVisibility(View.GONE);
                                }
                            }
                        }));
                    }
                }
            });
        }

        if(CurentState.equals("ReceivedRequest"))
        {
            HashMap hashMap = new HashMap();
            hashMap.put("status","decline");
            requestRef.child(userID).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ViewFriendActivity.this,"Request Declined", Toast.LENGTH_SHORT).show();
                        CurentState = "RequestDeclined";
                        btnPerform.setVisibility(View.GONE);
                        btnDecline.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void checkUserExsistence(String userID) {
        friendRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    CurentState="friend";
                    btnPerform.setText("Ping Friend");
                    btnDecline.setText("Un Friend");
                    btnDecline.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        friendRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    CurentState="friend";
                    btnPerform.setText("Ping Friend");
                    btnDecline.setText("Un Friend");
                    btnDecline.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    if(snapshot.child("status").getValue().toString().equals("pending"))
                    {
                        CurentState="SentRequest";
                        btnPerform.setText("Cancel Request");
                        btnDecline.setVisibility(View.GONE);
                    }

                    if(snapshot.child("status").getValue().toString().equals("decline"))
                    {
                        CurentState="Declined";
                        btnPerform.setText("Cancel Friend Request");
                        btnDecline.setVisibility(View.GONE);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        requestRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.child("status").getValue().toString().equals("pending"))
                    {
                        CurentState="ReceivedRequest";
                        btnPerform.setText("Accept Request");
                        btnDecline.setText("Decline Friend");
                        btnDecline.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(CurentState.equals("NotFriend"))
        {
            CurentState="NotFriend";
            btnPerform.setText("Send Friend Request");
            btnDecline.setVisibility(View.GONE);
        }
    }

    private void PerformAction(String userID) {
        if(CurentState.equals("NotFriend"))
        {
            HashMap hashMap = new HashMap();
            hashMap.put("status","pending");
            requestRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ViewFriendActivity.this,"Request Sent",Toast.LENGTH_SHORT).show();
                        btnDecline.setVisibility(View.GONE);
                        CurentState = "SentRequest";
                        btnPerform.setText("Cancel Request");
                        getToken("New Friend request",userID,"friend request","UNIted");
                    }

                    else
                    {
                        Toast.makeText(ViewFriendActivity.this,"Request Not Sent Due To" +task.getException().toString() ,Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }

        if(CurentState.equals("SentRequest") || CurentState.equals("Declined"))
        {
            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ViewFriendActivity.this,"Request Cancelled",Toast.LENGTH_SHORT).show();
                        CurentState="NotFriend";
                        btnPerform.setText("Send Request");
                        btnDecline.setVisibility(View.GONE);
                    }
                    else
                    {
                        Toast.makeText(ViewFriendActivity.this,"Request Not Cancelled Due To" +task.getException().toString() ,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if(CurentState.equals("ReceivedRequest"))
        {
            requestRef.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        HashMap hashMap = new HashMap();
                        hashMap.put("status","friend");
                        hashMap.put("username",susername);
                        hashMap.put("profileImageUrl",sprofileImage);
                        hashMap.put("UserID",userID);

                        friendRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful())
                                {
                                    HashMap nhashMap = new HashMap();
                                    nhashMap.put("status","friend");
                                    nhashMap.put("username",dusername);
                                    nhashMap.put("profileImageUrl",dimageuri);
                                    nhashMap.put("UserID",mUser.getUid());

                                    friendRef.child(userID).child(mUser.getUid()).updateChildren(nhashMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            Toast.makeText(ViewFriendActivity.this,"You Became Friends",Toast.LENGTH_SHORT).show();
                                            CurentState="friend";
                                            btnPerform.setText("Ping Friend");
                                            btnDecline.setText("Un Friend");
                                            btnDecline.setVisibility(View.VISIBLE);
                                            getToken("Friend request Accepted",userID,"friends","UNIted");
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }

        if(CurentState.equals("friend"))
        {
            Intent intent = new Intent(ViewFriendActivity.this,ChatActivity.class);
            intent.putExtra("OtherUserID",userID);
            startActivity(intent);
        }
    }

    private void LoadUser() {

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    sprofileImage = snapshot.child("ProfileImage").getValue().toString();
                    susername = snapshot.child("Name").getValue().toString();
                    scity = snapshot.child("city").getValue().toString();

                    Glide
                            .with(ViewFriendActivity.this)
                            .load(sprofileImage)
                            .centerCrop()
                            .placeholder(R.drawable.profile)
                            .into(circleImageView);

//                    Picasso.get().load(sprofileImage).into(circleImageView);
                    username.setText(susername);
                    city.setText(scity);
                }

                else
                {
                    Toast.makeText(ViewFriendActivity.this,"Data Not Found",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFriendActivity.this,""+error.getMessage().toString(),Toast.LENGTH_SHORT).show();
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
                    data.put("title",dusername);
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
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,FCMURL,to, response -> {
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