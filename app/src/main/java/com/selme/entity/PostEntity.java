package com.selme.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.Objects;

@IgnoreExtraProperties
public class PostEntity implements Parcelable {

    private String title;
    private String description;
    private @ServerTimestamp Date createdDate;
    private String photo1;
    private String photo2;
    private String userId;

    public PostEntity(String title, String description, Date createdDate, String photo1, String photo2, String userId, LikesEntity likesEntity, CommentsEntity commentsEntity) {
        this.title = title;
        this.description = description;
        this.createdDate = createdDate;
        this.photo1 = photo1;
        this.photo2 = photo2;
        this.userId = userId;
    }

    protected PostEntity(Parcel in) {
        title = in.readString();
        description = in.readString();
        photo1 = in.readString();
        photo2 = in.readString();
        userId = in.readString();
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getPhoto1() {
        return photo1;
    }

    public void setPhoto1(String photo1) {
        this.photo1 = photo1;
    }

    public String getPhoto2() {
        return photo2;
    }

    public void setPhoto2(String photo2) {
        this.photo2 = photo2;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostEntity that = (PostEntity) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(createdDate, that.createdDate) &&
                Objects.equals(photo1, that.photo1) &&
                Objects.equals(photo2, that.photo2) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(title, description, createdDate, photo1, photo2, userId);
    }

    public static final Creator<PostEntity> CREATOR = new Creator<PostEntity>() {
        @Override
        public PostEntity createFromParcel(Parcel in) {
            return new PostEntity(in);
        }

        @Override
        public PostEntity[] newArray(int size) {
            return new PostEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(photo1);
        parcel.writeString(photo2);
        parcel.writeString(userId);
    }
}
