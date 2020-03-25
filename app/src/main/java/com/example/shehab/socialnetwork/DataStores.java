package com.example.shehab.socialnetwork;

public class DataStores
{
    String profilimage,fullName;

    public DataStores()
    {
    }

    public DataStores(String profilimage, String fullName) {

        this.profilimage = profilimage;
        this.fullName = fullName;
    }

    public String getProfilimage() {
        return profilimage;
    }

    public void setProfilimage(String profilimage) {
        this.profilimage = profilimage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
