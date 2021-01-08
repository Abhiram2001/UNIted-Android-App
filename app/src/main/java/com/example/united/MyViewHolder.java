package com.example.united;

import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    CircleImageView profileImage;
    ImageView postImage,likeImage,commentImage,commentSend;
    TextView username,timeAgo,postDesc,likeCounter,commentsCounter;
    EditText inputComments;
    public static RecyclerView recyclerView;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImage = itemView.findViewById(R.id.profileImagePost);
        postImage = itemView.findViewById(R.id.imageView);
        postDesc = itemView.findViewById(R.id.postDesc);
        timeAgo = itemView.findViewById(R.id.timeAgo);
        username = itemView.findViewById(R.id.profileUsernamePost);
        likeCounter = itemView.findViewById(R.id.likeCounter);
        likeImage = itemView.findViewById(R.id.likeImage);
        commentsCounter = itemView.findViewById(R.id.commentsCounter);
        commentImage = itemView.findViewById(R.id.commentsImage);
        inputComments = itemView.findViewById(R.id.inputComments);
        commentSend = itemView.findViewById(R.id.sendComment);
//        recyclerView = itemView.findViewById(R.id.recyclerViewComments);
    }

    public void countLikes(String postKey, String uid, DatabaseReference likeRef) {
        likeRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int totalLikes = (int) snapshot.getChildrenCount();
                    likeCounter.setText(totalLikes+"");
                }
                else{
                    likeCounter.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        likeRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(uid).exists()){
                    likeImage.setColorFilter(Color.BLUE);
                }
                else
                {
                    likeImage.setColorFilter(Color.GRAY);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void countComments(String postKey, String uid, DatabaseReference commentRef) {

        commentRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int totalComments = (int) snapshot.getChildrenCount();
                    commentsCounter.setText(totalComments+"");
                }
                else{
                    commentsCounter.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
