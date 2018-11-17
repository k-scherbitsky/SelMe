package com.selme.dto;

import android.net.Uri;

import java.util.Date;

public class PostDTO {

    private String userName;
    private Uri avatar;
    private String title;
    private String description;
    private Uri picture1;
    private Uri picture2;
    private Date createdDate;

    public PostDTO(){

    }

    public PostDTO(String userName, Uri avatar, String title, String description, Uri picture1, Uri picture2) {
        this.userName = userName;
        this.avatar = avatar;
        this.title = title;
        this.description = description;
        this.picture1 = picture1;
        this.picture2 = picture2;
        this.createdDate = createdDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Uri getAvatar() {
        return avatar;
    }

    public void setAvatar(Uri avatar) {
        this.avatar = avatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Uri getPicture1() {
        return picture1;
    }

    public void setPicture1(Uri picture1) {
        this.picture1 = picture1;
    }

    public Uri getPicture2() {
        return picture2;
    }

    public void setPicture2(Uri picture2) {
        this.picture2 = picture2;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
