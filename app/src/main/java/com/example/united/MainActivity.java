package com.example.united;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.united.Utils.Comment;
import com.example.united.Utils.InternetDialog;
import com.example.united.Utils.Posts;
import com.example.united.Utils.UserStatus;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {





    UserStatus userStatus = new UserStatus();


    private static final int REQUEST_CODE=101;
    Uri imageUri;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference, PostRef,likeRef,commentRef;
    StorageReference postImageRef;

    String dusername, dimageuri;
    CircleImageView profileImageViewHeader;
    TextView usernameHeader;

    ImageView addImagePost, sendImagePost;
    EditText inputPostDesc;

    ProgressDialog mLoadingBar;

    FirebaseRecyclerAdapter<Posts,MyViewHolder>adapter;
    FirebaseRecyclerOptions<Posts>options;
    RecyclerView recyclerView;

    FirebaseRecyclerOptions<Comment>CommentOption;
    FirebaseRecyclerAdapter<Comment, CommentViewHolder>CommentAdapter;


    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadPost(); // your code
                pullToRefresh.setRefreshing(false);
            }
        });

        if(new InternetDialog(this).getInternetStatus()){
            Toast.makeText(this, "INTERNET VALIDATION PASSED", Toast.LENGTH_SHORT).show();
        }


        if(userStatus.tokenbefore.length()!=1){
            userStatus.tokenbefore = userStatus.tokenbefore.substring(1, userStatus.tokenbefore.length()+0);
            userStatus.setUsertoken(userStatus.tokenbefore);
        }


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        commentRef = FirebaseDatabase.getInstance().getReference().child("Comments");
        postImageRef = FirebaseStorage.getInstance().getReference().child("PostImages");

        FirebaseMessaging.getInstance().subscribeToTopic(firebaseUser.getUid());

        mLoadingBar  =new ProgressDialog(MainActivity.this);

        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);


        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("UNIted");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);


        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        View view = navigationView.inflateHeaderView(R.layout.drawer_header);
        profileImageViewHeader = view.findViewById(R.id.profile_image_header);
        usernameHeader = view.findViewById(R.id.username_header);

        navigationView.setNavigationItemSelectedListener(this);


        addImagePost = findViewById(R.id.addImagepost);
        sendImagePost = findViewById(R.id.send_post_imageView);
        inputPostDesc = findViewById(R.id.inputAddpost);

        sendImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPost();
            }
        });

        addImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        LoadPost();


        mFusedLocationClient
                = LocationServices
                .getFusedLocationProviderClient(this);

        // method to get the location
        getLastLocation();

    }

    private void LoadPost() {
        options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(PostRef,Posts.class).build();
        adapter = new FirebaseRecyclerAdapter<Posts, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Posts model) {
                String postKey = getRef(position).getKey();
                holder.postDesc.setText(model.getPostDesc());
                holder.username.setText(model.getUsername());
                String timeAgo = calculateTimeAgo(model.getDate());
                holder.timeAgo.setText(timeAgo);

                Glide
                        .with(MainActivity.this)
                        .load(model.getPostImageUri())
                        .centerCrop()
                        .fitCenter()
                        .placeholder(R.drawable.logo)
                        .into(holder.postImage);

                Glide
                        .with(MainActivity.this)
                        .load(model.getUserProfileImageUrl())
                        .centerCrop()
                        .fitCenter()
                        .placeholder(R.drawable.profile)
                        .into(holder.profileImage);

//                Picasso.get().load(model.getPostImageUri()).into(holder.postImage);
//                Picasso.get().load(model.getUserProfileImageUrl()).into(holder.profileImage);
                holder.countLikes(postKey,firebaseUser.getUid(),likeRef);
                holder.countComments(postKey,firebaseUser.getUid(),commentRef);
                holder.likeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeRef.child(postKey).child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    likeRef.child(postKey).child(firebaseUser.getUid()).removeValue();
                                    holder.likeImage.setColorFilter(Color.GRAY);
                                    notifyDataSetChanged();

                                } else {
                                    likeRef.child(postKey).child(firebaseUser.getUid()).setValue("Like");
                                    holder.likeImage.setColorFilter(Color.BLUE);
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                holder.commentImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        BottomCommentSheet bottomCommentSheet = new BottomCommentSheet();
                        Bundle bundle = new Bundle();
                        bundle.putString("postKey",postKey);
                        bundle.putString("username",dusername);
                        bundle.putString("userProfileUri",dimageuri);
                        bottomCommentSheet.setArguments(bundle);
                        bottomCommentSheet.show(getSupportFragmentManager(),"TAG");
                        adapter.notifyDataSetChanged();
                    }
                });

                holder.postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, ImageViewActivity.class);
                        intent.putExtra("url", model.getPostImageUri());
                        intent.putExtra("postedby", model.getUsername());
                        intent.putExtra("posttime", model.getDate());

                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_post,parent,false);
                return new MyViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }


    private String calculateTimeAgo(String datePost)
    {

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            long time = sdf.parse(datePost).getTime();
            long now = System.currentTimeMillis();
            Log.d("Logger",""+time+","+now);
            CharSequence ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            Log.d("Logger",""+ago);
            return ago+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            addImagePost.setImageURI(imageUri);
        }
    }

    public void AddPost(){
        String postDesc = inputPostDesc.getText().toString();
        if(postDesc.isEmpty()){
            inputPostDesc.setError("Please Write Description");
        }
        else if (imageUri==null){
            Toast.makeText(MainActivity.this,"Please Select the Image", Toast.LENGTH_SHORT).show();
        }
        else{
            mLoadingBar.setTitle("Adding Post");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");
            String strDate = formatter.format(date);

            postImageRef.child(strDate+firebaseUser.getUid()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        postImageRef.child(strDate+firebaseUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                HashMap hashMap = new HashMap();
                                hashMap.put("Date", strDate);
                                hashMap.put("PostImageUri", uri.toString());
                                hashMap.put("PostDesc", postDesc);
                                hashMap.put("userProfileImageUrl", dimageuri);
                                hashMap.put("username", dusername);

                                PostRef.child(strDate+firebaseUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()){
                                            mLoadingBar.dismiss();
                                            Toast.makeText(MainActivity.this,"Post  Added", Toast.LENGTH_SHORT).show();
                                            addImagePost.setImageResource(R.drawable.ic_add_post_image);
                                            inputPostDesc.setText("");
                                        }
                                        else {
                                            mLoadingBar.dismiss();
                                            Toast.makeText(MainActivity.this,""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });
                    }
                    else {
                        mLoadingBar.dismiss();
                        Toast.makeText(MainActivity.this,""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseUser==null){
            sendUserToLoginActivity();
        }
        else{
            databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        dimageuri = dataSnapshot.child("ProfileImage").getValue().toString();
                        dusername = dataSnapshot.child("Name").getValue().toString();

                        Glide
                                .with(MainActivity.this)
                                .load(dimageuri)
                                .centerCrop()
                                .placeholder(R.drawable.profile)
                                .into(profileImageViewHeader);

//                        Picasso.get().load(dimageuri).into(profileImageViewHeader);
                        usernameHeader.setText(dusername);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendUserToLoginActivity() {
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch ( menuItem.getItemId() )
        {
            case R.id.home:
                Toast.makeText(this,"Home",Toast.LENGTH_SHORT).show();
                break;
            case R.id.profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;
            case R.id.friend:
                startActivity(new Intent(getApplicationContext(), FriendActivity.class));
                break;
            case R.id.findfriend:
                Intent intent = new Intent(getApplicationContext(), FindFriendActivity.class);
                intent.putExtra("dusername",dusername);
                intent.putExtra("dimageuri",dimageuri);
                startActivity(intent);
                break;
            case R.id.chat:
                Toast.makeText(this,"chat",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), CHatUsersActivity.class));
                break;
            case R.id.Unite:
                startActivity(new Intent(getApplicationContext(), SubscribedProtests.class));
                break;
            case R.id.FindIncidents:
                startActivity(new Intent(getApplicationContext(), AllIncidents.class));
                break;
            case R.id.locations:
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                break;
            case R.id.logout:
                Toast.makeText(this,"logout",Toast.LENGTH_SHORT).show();
                userStatus.tokenbefore="$";
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                break;
        }
        return true;
    }


//    @Override
//    protected void onResume() {
//        userStatus.setUserStatus("Online");
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        userStatus.setUserStatus(String.valueOf(System.currentTimeMillis()));
//        super.onPause();
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation()
    {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient
                        .getLastLocation()
                        .addOnCompleteListener(
                                new OnCompleteListener<Location>() {

                                    @Override
                                    public void onComplete(
                                            @NonNull Task<Location> task)
                                    {
                                        Location location = task.getResult();
                                        if (location == null) {
                                            requestNewLocationData();
                                        }
                                        else {
                                            databaseReference.child(firebaseUser.getUid()).child("latitude").setValue(location.getLatitude()+"");
                                            databaseReference.child(firebaseUser.getUid()).child("longitude").setValue(location.getLongitude()+"");
//
                                        }
                                    }
                                });
            }

            else {
                Toast
                        .makeText(
                                this,
                                "Please turn on"
                                        + " your location...",
                                Toast.LENGTH_LONG)
                        .show();

                Intent intent
                        = new Intent(
                        Settings
                                .ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }
        else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData()
    {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest
                = new LocationRequest();
        mLocationRequest.setPriority(
                LocationRequest
                        .PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient
                = LocationServices
                .getFusedLocationProviderClient(this);

        mFusedLocationClient
                .requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback,
                        Looper.myLooper());
    }

    private LocationCallback
            mLocationCallback
            = new LocationCallback() {

        @Override
        public void onLocationResult(
                LocationResult locationResult)
        {
            Location mLastLocation
                    = locationResult
                    .getLastLocation();
            databaseReference.child(firebaseUser.getUid()).child("latitude").setValue(mLastLocation.getLatitude()+"");
            databaseReference.child(firebaseUser.getUid()).child("longitude").setValue(mLastLocation.getLongitude()+"");

        }
    };

    // method to check for permissions
    private boolean checkPermissions()
    {
        return ActivityCompat
                .checkSelfPermission(
                        this,
                        Manifest.permission
                                .ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED

                && ActivityCompat
                .checkSelfPermission(
                        this,
                        Manifest.permission
                                .ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        /* ActivityCompat
                .checkSelfPermission(
                    this,
                    Manifest.permission
                        .ACCESS_BACKGROUND_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        */
    }

    // method to requestfor permissions
    private void requestPermissions()
    {
        ActivityCompat.requestPermissions(
                this,
                new String[] {
                        Manifest.permission
                                .ACCESS_COARSE_LOCATION,
                        Manifest.permission
                                .ACCESS_FINE_LOCATION },
                PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled()
    {
        LocationManager
                locationManager
                = (LocationManager)getSystemService(
                Context.LOCATION_SERVICE);

        return locationManager
                .isProviderEnabled(
                        LocationManager.GPS_PROVIDER)
                || locationManager
                .isProviderEnabled(
                        LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults)
    {
        super
                .onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager
                    .PERMISSION_GRANTED) {

                getLastLocation();
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

}