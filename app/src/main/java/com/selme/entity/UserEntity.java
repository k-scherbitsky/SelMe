package com.selme.entity;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Objects;

@IgnoreExtraProperties
public class UserEntity {

    private String firstName;
    private String lastName;
    private String description;
    private String profilePhoto;
    private String userId;

    public UserEntity(){

    }

    public UserEntity(String firstName, String lastName, String description, String profilePhoto, String userId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.profilePhoto = profilePhoto;
        this.userId = userId;
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
        UserEntity that = (UserEntity) o;
        return Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(description, that.description) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(profilePhoto, that.profilePhoto);
    }

    @Override
    public int hashCode() {

        return Objects.hash(firstName, lastName, description, profilePhoto, userId);
    }
}
