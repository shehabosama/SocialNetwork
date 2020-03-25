package com.example.shehab.socialnetwork;

public class Allcomments
{
    private String comment,date,time,uid,username,profilimage;

    public Allcomments(){

    }

    public Allcomments(String comment, String date, String time, String uid, String username,String profilimage) {
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.uid = uid;
        this.username = username;
        this.profilimage = profilimage;
    }

    public String getProfilimage() {
        return profilimage;
    }

    public void setProfilimage(String profilimage) {
        this.profilimage = profilimage;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
