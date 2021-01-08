package com.example.united;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendMyViewHolder extends RecyclerView.ViewHolder {

    CircleImageView profileImage;
    TextView username;
    public FriendMyViewHolder(@NonNull View itemView) {

        super(itemView);
        profileImage = itemView.findViewById(R.id.profileImage);
        username = itemView.findViewById(R.id.username);
    }
}
