package com.peoplereport.peoplereport.ClassModels;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * creado por  Christian en la fecha 2018-06-11.
 */

public class CommentsModels {

    private String commentsId;
    private String commentsName;
    private String commentsBody;
    private String commentsPictureProfile;

    public CommentsModels() {
    }

    public CommentsModels(String commentsId, String commentsName, String commentsBody, String commentsPictureProfile) {
        this.commentsId = commentsId;
        this.commentsName = commentsName;
        this.commentsBody = commentsBody;
        this.commentsPictureProfile = commentsPictureProfile;
    }

    public String getCommentsId() {
        return commentsId;
    }

    public void setCommentsId(String commentsId) {
        this.commentsId = commentsId;
    }

    public String getCommentsName() {
        return commentsName;
    }

    public void setCommentsName(String commentsName) {
        this.commentsName = commentsName;
    }

    public String getCommentsBody() {
        return commentsBody;
    }

    public void setCommentsBody(String commentsBody) {
        this.commentsBody = commentsBody;
    }

    public String getCommentsPictureProfile() {
        return commentsPictureProfile;
    }

    public void setCommentsPictureProfile(String commentsPictureProfile) {
        this.commentsPictureProfile = commentsPictureProfile;
    }

}
