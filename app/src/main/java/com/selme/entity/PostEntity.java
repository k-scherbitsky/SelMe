package com.selme.entity;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@IgnoreExtraProperties
public class PostEntity{

    private String title;
    private String description;
    private @ServerTimestamp Date createdDate;
    private String photo1;
    private String photo2;
    private String userId;
    private String docId;
    private int pickPic1;
    private int pickPic2;
    private List<String> votedUserIds;
    private Map<String, String> comments;

    public PostEntity(){

    }

    public PostEntity(String title, String description, String photo1, String photo2, String docId, int pickPic1, int pickPic2, List<String> votedUserIds, Map<String, String> comments){
        this.title = title;
        this.description = description;
        this.photo1 = photo1;
        this.photo2 = photo2;
        this.docId = docId;
        this.pickPic1 = pickPic1;
        this.pickPic2 = pickPic2;
        this.votedUserIds = votedUserIds;
        this.comments = comments;
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

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public int getPickPic1() {
        return pickPic1;
    }

    public void setPickPic1(int pickPic1) {
        this.pickPic1 = pickPic1;
    }

    public int getPickPic2() {
        return pickPic2;
    }

    public void setPickPic2(int pickPic2) {
        this.pickPic2 = pickPic2;
    }

    public List<String> getVotedUserIds() {
        return votedUserIds;
    }

    public void setVotedUserIds(List<String> votedUserIds) {
        this.votedUserIds = votedUserIds;
    }

    public Map<String, String> getComments() {
        return comments;
    }

    public void setComments(Map<String, String> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostEntity that = (PostEntity) o;
        return pickPic1 == that.pickPic1 &&
                pickPic2 == that.pickPic2 &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(createdDate, that.createdDate) &&
                Objects.equals(photo1, that.photo1) &&
                Objects.equals(photo2, that.photo2) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(docId, that.docId) &&
                Objects.equals(votedUserIds, that.votedUserIds) &&
                Objects.equals(comments, that.comments);
    }

    @Override
    public int hashCode() {

        return Objects.hash(title, description, createdDate, photo1, photo2, userId, docId, pickPic1, pickPic2, votedUserIds, comments);
    }
}
