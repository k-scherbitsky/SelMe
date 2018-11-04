package com.selme.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Objects;

@IgnoreExtraProperties
public class UserEntity implements Parcelable {

    private String firstName;
    private String lastName;
    private String description;
    private String profilePhoto;

    public UserEntity(){

    }

    public UserEntity(String firstName, String lastName, String description, String profilePhoto) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.profilePhoto = profilePhoto;
    }

    protected UserEntity(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        description = in.readString();
        profilePhoto = in.readString();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public static final Creator<UserEntity> CREATOR = new Creator<UserEntity>() {
        @Override
        public UserEntity createFromParcel(Parcel in) {
            return new UserEntity(in);
        }

        @Override
        public UserEntity[] newArray(int size) {
            return new UserEntity[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(description, that.description) &&
                Objects.equals(profilePhoto, that.profilePhoto);
    }

    @Override
    public int hashCode() {

        return Objects.hash(firstName, lastName, description, profilePhoto);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(description);
        parcel.writeString(profilePhoto);
    }
}
