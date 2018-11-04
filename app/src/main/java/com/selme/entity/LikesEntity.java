package com.selme.entity;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class LikesEntity {

    private Boolean like;

    public LikesEntity(boolean like) {
        this.like = like;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

}
