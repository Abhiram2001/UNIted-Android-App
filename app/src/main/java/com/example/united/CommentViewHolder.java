package com.example.united;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    public static RecyclerView recyclerView;
    CircleImageView profileImage;
    TextView username,comment,time;
    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImage = itemView.findViewById(R.id.porofileImage_comment);
        username= itemView.findViewById(R.id.usernameComment);
        comment = itemView.findViewById(R.id.commentsTV);
        time = itemView.findViewById(R.id.CommentTime);
    }
}
