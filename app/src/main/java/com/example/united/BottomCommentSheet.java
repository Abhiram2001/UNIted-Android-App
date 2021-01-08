package com.example.united;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.united.Utils.Comment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class BottomCommentSheet extends BottomSheetDialogFragment {

    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference, PostRef,likeRef,commentRef;
    StorageReference postImageRef;

    ImageView commentSend;
    EditText inputComment;

    String username,postKey,profileImageUri;


    FirebaseRecyclerOptions<Comment> CommentOption;
    FirebaseRecyclerAdapter<Comment, CommentViewHolder> CommentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_bottom_comment,container,false);

        Bundle bundle = getArguments();
        postKey = bundle.getString("postKey");
        username = bundle.getString("username");
        profileImageUri = bundle.getString("userProfileUri");

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        commentRef = FirebaseDatabase.getInstance().getReference().child("Comments");
        postImageRef = FirebaseStorage.getInstance().getReference().child("PostImages");

        recyclerView = view.findViewById(R.id.recyclerViewCommentsBottom);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        LoadComment(postKey);

        commentSend = view.findViewById(R.id.sendComment);
        inputComment = view.findViewById(R.id.inputComments);

        commentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = inputComment.getText().toString();
                if(comment.isEmpty())
                {
                    Toast.makeText(getContext(),"Please Write the Comment",Toast.LENGTH_SHORT).show();
                }
                else {
                    AddComment(postKey, commentRef,firebaseUser.getUid(),comment);
                }
            }
        });

        return view;
    }


    private void LoadComment(String postKey) { ;
        CommentOption = new FirebaseRecyclerOptions.Builder<Comment>().setQuery(commentRef.child(postKey),Comment.class).build();
        CommentAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(CommentOption) {
            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment model) {
//                Picasso.get().load(model.getProfileImageUri()).into(holder.profileImage);

                Glide
                        .with(BottomCommentSheet.this)
                        .load(model.getProfileImageUri())
                        .centerCrop()
                        .placeholder(R.drawable.profile)
                        .into(holder.profileImage);

                holder.username.setText(model.getUsername());
                holder.comment.setText(model.getComment());
                String timeAgo = calculateTimeAgo(model.getTime());
                holder.time.setText(timeAgo);
            }

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_comment,parent,false);
                return new CommentViewHolder(view);
            }
        };
        CommentAdapter.startListening();
        recyclerView.setAdapter(CommentAdapter);
    }

    private void AddComment(String postKey, DatabaseReference commentRef, String uid, String comment) {
        HashMap hashMap = new HashMap();
        hashMap.put("username",username);
        hashMap.put("profileImageUri", profileImageUri);
        hashMap.put("comment",comment);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");
        String strDate = formatter.format(date);

        hashMap.put("time", strDate);

        commentRef.child(postKey).child(strDate+uid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(),"Commented Succesfully",Toast.LENGTH_SHORT).show();
                    inputComment.setText(null);
                }
                else{
                    Toast.makeText(getContext(),""+task.getException().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
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

}
