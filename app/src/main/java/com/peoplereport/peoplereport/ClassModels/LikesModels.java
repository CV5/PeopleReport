package com.peoplereport.peoplereport.ClassModels;

/**
 * creado por  Christian en la fecha 2018-06-11.
 */

public class LikesModels {

    private String likeId;
    private String likesPhotoUrl;

    public LikesModels() {
    }

    public LikesModels(String likeId, String likesPhotoUrl) {
        this.likeId = likeId;
        this.likesPhotoUrl = likesPhotoUrl;
    }

    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    public String getLikesPhotoUrl() {
        return likesPhotoUrl;
    }

    public void setLikesPhotoUrl(String likesPhotoUrl) {
        this.likesPhotoUrl = likesPhotoUrl;
    }
}
