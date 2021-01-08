package com.example.united.Utils;

public class Posts {

    private String Date,PostDesc,PostImageUri,username,userProfileImageUrl;
    public Posts(){

    }

    public Posts(String Date,String PostDesc,String PostImageUri,String userProfileImageUrl,String username){
        this.Date = Date;
        this.PostDesc = PostDesc;
        this.PostImageUri= PostImageUri;
        this.userProfileImageUrl = userProfileImageUrl;
        this.username=username;
    }

    public String getDate(){
        return Date;
    }
    public void setDate(String Date){
        this.Date = Date;
    }
    public String getPostDesc(){
        return PostDesc;
    }
    public void setPostDesc(String PostDesc){
        this.PostDesc = PostDesc;
    }
    public String getPostImageUri(){
        return PostImageUri;
    }
    public void setPostImageUri(String PostImageUri)
    {
        this.PostImageUri = PostImageUri;
    }
    public String getUserProfileImageUrl(){

        return userProfileImageUrl;
    }
    public void setUserProfileImageUrl(String userProfileImageUrl){
        this.userProfileImageUrl = userProfileImageUrl;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){

        this.username = username;
    }
}
