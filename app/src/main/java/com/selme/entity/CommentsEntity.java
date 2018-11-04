package com.selme.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.Objects;

@IgnoreExtraProperties
public class CommentsEntity implements Parcelable {

    private String comment;
    private String commentUser;
    private @ServerTimestamp Date createdDate;

    public CommentsEntity(String comment, String commentUser, Date createdDate) {
        this.comment = comment;
        this.commentUser = commentUser;
        this.createdDate = createdDate;
    }

    protected CommentsEntity(Parcel in) {
        comment = in.readString();
        commentUser = in.readString();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(String commentUser) {
        this.commentUser = commentUser;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentsEntity that = (CommentsEntity) o;
        return Objects.equals(comment, that.comment) &&
                Objects.equals(commentUser, that.commentUser) &&
                Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {

        return Objects.hash(comment, commentUser, createdDate);
    }

    public static final Creator<CommentsEntity> CREATOR = new Creator<CommentsEntity>() {
        @Override
        public CommentsEntity createFromParcel(Parcel in) {
            return new CommentsEntity(in);
        }

        @Override
        public CommentsEntity[] newArray(int size) {
            return new CommentsEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(comment);
        parcel.writeString(commentUser);
    }
}
