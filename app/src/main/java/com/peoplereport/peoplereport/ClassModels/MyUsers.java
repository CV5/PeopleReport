package com.peoplereport.peoplereport.ClassModels;

/**
 * creado por  Christian en la fecha 2018-06-09.
 */

public class MyUsers {
    private String username;
    private String email;
    private String id;
    private String pictureProfile;
    private String coverPicture;

    public MyUsers() {
    }

    public MyUsers(String username, String email, String id, String pictureProfile, String coverPicture) {
        this.username = username;
        this.email = email;
        this.id = id;
        this.pictureProfile = pictureProfile;
        this.coverPicture = coverPicture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPictureProfile() {
        return pictureProfile;
    }

    public void setPictureProfile(String pictureProfile) {
        this.pictureProfile = pictureProfile;
    }

    public String getCoverPicture() {
        return coverPicture;
    }

    public void setCoverPicture(String coverPicture) {
        this.coverPicture = coverPicture;
    }
}
