package com.selme.dto;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostDTO implements Parcelable {

    private String userName;
    private String docId;
    private Uri avatar;
    private String title;
    private String description;
    private Uri picture1;
    private Uri picture2;
    private Date createdDate;
    private int pickPic1;
    private int pickPic2;
    private int amountPickPic;
    private List<String> votedUserIds;
    private Map<String, String> comments;

    public PostDTO(){

    }

    public PostDTO(String userName, String docId, Uri avatar, String title, String description, Uri picture1, Uri picture2, int pickPic1, int pickPic2, int amountPickPic, List<String> votedUserIds, Map<String, String> comments) {
        this.userName = userName;
        this.docId = docId;
        this.avatar = avatar;
        this.title = title;
        this.description = description;
        this.picture1 = picture1;
        this.picture2 = picture2;
        this.pickPic1 = pickPic1;
        this.pickPic2 = pickPic2;
        this.amountPickPic = amountPickPic;
        this.votedUserIds = votedUserIds;
        this.comments = comments;
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

    public int getAmountPickPic() {
        return amountPickPic;
    }

    public void setAmountPickPic(int amountPickPic) {
        this.amountPickPic = amountPickPic;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.docId);
        dest.writeParcelable(this.avatar, flags);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeParcelable(this.picture1, flags);
        dest.writeParcelable(this.picture2, flags);
        dest.writeLong(this.createdDate != null ? this.createdDate.getTime() : -1);
        dest.writeInt(this.pickPic1);
        dest.writeInt(this.pickPic2);
        dest.writeInt(this.amountPickPic);
        dest.writeStringList(this.votedUserIds);
        dest.writeInt(this.comments.size());
        for (Map.Entry<String, String> entry : this.comments.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    protected PostDTO(Parcel in) {
        this.userName = in.readString();
        this.docId = in.readString();
        this.avatar = in.readParcelable(Uri.class.getClassLoader());
        this.title = in.readString();
        this.description = in.readString();
        this.picture1 = in.readParcelable(Uri.class.getClassLoader());
        this.picture2 = in.readParcelable(Uri.class.getClassLoader());
        long tmpCreatedDate = in.readLong();
        this.createdDate = tmpCreatedDate == -1 ? null : new Date(tmpCreatedDate);
        this.pickPic1 = in.readInt();
        this.pickPic2 = in.readInt();
        this.amountPickPic = in.readInt();
        this.votedUserIds = in.createStringArrayList();
        int commentsSize = in.readInt();
        this.comments = new HashMap<String, String>(commentsSize);
        for (int i = 0; i < commentsSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.comments.put(key, value);
        }
    }

    public static final Parcelable.Creator<PostDTO> CREATOR = new Parcelable.Creator<PostDTO>() {
        @Override
        public PostDTO createFromParcel(Parcel source) {
            return new PostDTO(source);
        }

        @Override
        public PostDTO[] newArray(int size) {
            return new PostDTO[size];
        }
    };
}
