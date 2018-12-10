package com.selme.model.dto;

import android.net.Uri;

public class CommentsDTO {

    private String userName;
    private String comment;
    private Uri avatar;

    public CommentsDTO(String userName, String comment, Uri avatar) {
        this.userName = userName;
        this.comment = comment;
        this.avatar = avatar;
    }

    public CommentsDTO() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Uri getAvatar() {
        return avatar;
    }

    public void setAvatar(Uri avatar) {
        this.avatar = avatar;
    }
}
