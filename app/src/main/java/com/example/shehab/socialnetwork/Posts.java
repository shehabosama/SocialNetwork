package com.example.shehab.socialnetwork;

public class Posts {
    private String uid,fullname,postprofileimag,postimage,description,time,date;

    public Posts()
    {

    }
    public Posts(String uid,String fullname, String postprofileimag, String postimage, String description, String time, String date) {
        this.fullname = fullname;
        this.postprofileimag = postprofileimag;
        this.postimage = postimage;
        this.description = description;
        this.time = time;
        this.date = date;
        this.uid =uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPostprofileimag() {
        return postprofileimag;
    }

    public void setPostprofileimag(String postprofileimag) {
        this.postprofileimag = postprofileimag;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
