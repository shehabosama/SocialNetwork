package com.example.shehab.socialnetwork;

public class Allusers {
    public String fullName;
    public String profilimage;
    public String status;


    public Allusers(){

    }

    public Allusers(String user_name, String user_image, String user_status) {
        this.fullName = user_name;
        this.profilimage = user_image;
        this.status = user_status;
    }

    public String getUser_name() {
        return fullName;
    }

    public void setUser_name(String user_name) {
        this.fullName = user_name;
    }

    public String getUser_image() {
        return profilimage;
    }

    public void setUser_image(String user_image) {
        this.profilimage = user_image;
    }

    public String getUser_status() {
        return status;
    }

    public void setUser_status(String user_status) {
        this.status = user_status;
    }
}
