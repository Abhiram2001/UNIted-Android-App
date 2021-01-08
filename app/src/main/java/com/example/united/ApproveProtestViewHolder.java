package com.example.united;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ApproveProtestViewHolder extends RecyclerView.ViewHolder {

    CircleImageView profileImage;
    TextView username,profession,benifit, aganist;
    Button button,button1;

    public ApproveProtestViewHolder(@NonNull View itemView) {
        super(itemView);

        profileImage = itemView.findViewById(R.id.profileImage);
        username = itemView.findViewById(R.id.username);
        profession= itemView.findViewById(R.id.profession);
        benifit= itemView.findViewById(R.id.benfitTo);
        aganist = itemView.findViewById(R.id.aganist);
        button = itemView.findViewById(R.id.button);
        button1 = itemView.findViewById(R.id.button2);
    }
}
