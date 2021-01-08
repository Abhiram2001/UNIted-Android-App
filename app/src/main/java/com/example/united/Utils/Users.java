package com.example.united.Utils;

public class Users {

    private String Name,city,job,ProfileImage,status;

    public Users() {
    }

    public Users(String name, String city, String job, String profileImage, String status) {
        Name = name;
        this.city = city;
        this.job = job;
        ProfileImage = profileImage;
        this.status = status;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
