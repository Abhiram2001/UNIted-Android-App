package com.example.united.Utils;


public class Comment {
    private String comment, username, profileImageUri, time;
    public Comment(){

    }

    public Comment(String comment, String username, String profileImageUri, String time) {
        this.comment = comment;
        this.username = username;
        this.profileImageUri = profileImageUri;
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
