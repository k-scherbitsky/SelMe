package com.selme.presenter.service;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.StorageReference;
import com.selme.presenter.dao.PostDAO;
import com.selme.presenter.dao.UserDAO;
import com.selme.model.dto.CommentsDTO;
import com.selme.model.dto.PostDTO;
import com.selme.model.entity.PostEntity;
import com.selme.model.entity.UserEntity;
import com.selme.presenter.interfaces.CommentsDTOCallback;
import com.selme.presenter.interfaces.PictureLoaderCallback;
import com.selme.presenter.interfaces.PostDAOCallback;
import com.selme.presenter.interfaces.PostDTOCallback;
import com.selme.presenter.interfaces.UserDAOCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataMapper implements UserDAOCallback, PostDAOCallback, PictureLoaderCallback {

    private static final String TAG = "DataMapper";
    private static final int REQUEST_AVATAR_POST = 100;
    private static final int REQUEST_PICTURE_1 = 200;
    private static final int REQUEST_PICTURE_2 = 300;
    private static final int REQUEST_USERNAME_POST = 400;
    private static final int REQUEST_USERNAME_COMMENT = 500;
    private static final int REQUEST_AVATAR_COMMENT = 600;

    private StorageReference storageRef;
    private PostDTOCallback postDTOCallback;
    private CommentsDTOCallback commentsDTOCallback;
    private List<PostDTO> postDTOList;
    private List<CommentsDTO> commentsDTOList;
    private UserDAO userDAO;
    private int postListSize = -1;
    private int commentsListSize = -1;

    public DataMapper(){}

    public DataMapper(StorageReference storageRef) {
        this.storageRef = storageRef;
    }

    public void toPostDto(PostDTOCallback callback, boolean isProfile) {
        this.postDTOCallback = callback;

        userDAO = new UserDAO(this);

        PostDAO postDAO = new PostDAO(this);
        postDAO.getPost(isProfile);
    }

    public void toCommentsDto(Map<String, String> commentsMap, CommentsDTOCallback callback) {
        this.commentsDTOCallback = callback;
        userDAO = new UserDAO(this);
        commentsDTOList = new ArrayList<>();
        commentsListSize = commentsMap.size();

        int i = 0;
        for (Map.Entry entry : commentsMap.entrySet()) {
            CommentsDTO buf = new CommentsDTO();
            buf.setComment(entry.getValue().toString());

            getUser(entry.getKey().toString(), REQUEST_USERNAME_COMMENT, i);

            commentsDTOList.add(buf);
            i++;
        }
    }

    @Override
    public void onPostLoaded(List<PostEntity> postEntityList) {
        postDTOList = new ArrayList<>();

        if (postEntityList != null && !postEntityList.isEmpty()) {

            postListSize = postEntityList.size();

            for (int i = 0; i < postEntityList.size(); i++) {
                getUser(postEntityList.get(i).getUserId(), REQUEST_USERNAME_POST, i); // получение аватара, имени и фамилии по userId

                getPicture(postEntityList.get(i).getPhoto1(), REQUEST_PICTURE_1, i);
                getPicture(postEntityList.get(i).getPhoto2(), REQUEST_PICTURE_2, i);

                PostDTO dto = new PostDTO();

                dto.setDescription(postEntityList.get(i).getDescription());
                dto.setDocId(postEntityList.get(i).getDocId());
                dto.setCreatedDate(postEntityList.get(i).getCreatedDate());

                int pickPic1 = postEntityList.get(i).getPickPic1();
                int pickPic2 = postEntityList.get(i).getPickPic2();
                int amountPickPic = pickPic1 + pickPic2;

                dto.setPickPic1(pickPic1);
                dto.setPickPic2(pickPic2);
                dto.setAmountPickPic(amountPickPic);

                dto.setVotedUserIds(postEntityList.get(i).getVotedUserIds());

                dto.setComments(postEntityList.get(i).getComments());
                dto.setCommentsQuantity(postEntityList.get(i).getComments().size());

                Map<String, Boolean> likesMap = postEntityList.get(i).getLikes();
                int countLikes = 0;
                for (Map.Entry entry : likesMap.entrySet()) {
                    if(entry.getValue().equals(true)){
                        countLikes++;
                    }
                }
                dto.setLikes(postEntityList.get(i).getLikes());
                dto.setLikesQuantity(countLikes);

                postDTOList.add(dto);
            }
        } else {
            postDTOCallback.toDto(null);
        }
    }

    @Override
    public void onUserLoaded(UserEntity user, int requestCode, int pos) {
        String userNameText = user.getFirstName() + " " +user.getLastName();
        String fileName = user.getProfilePhoto();

        switch (requestCode){
            case REQUEST_USERNAME_POST:{
                postDTOList.get(pos).setUserName(userNameText);
                getProfilePhoto(fileName, REQUEST_AVATAR_POST, pos);
                doPostCallback(postListSize, postDTOList, postDTOCallback); // проверка можно ли делать postDTOCallback. Все ли данные уже получены из бд.
                break;
            }
            case REQUEST_USERNAME_COMMENT:{
                commentsDTOList.get(pos).setUserName(userNameText);
                getProfilePhoto(fileName, REQUEST_AVATAR_COMMENT, pos);
                doCommentCallback(commentsListSize, commentsDTOList, commentsDTOCallback);
                break;
            }
        }
    }

    @Override
    public void onPictureDownloaded(Uri uri, int requestCode, int pos) {

        switch (requestCode) {
            case REQUEST_AVATAR_POST:
                postDTOList.get(pos).setAvatar(uri);
                doPostCallback(postListSize, postDTOList, postDTOCallback); // проверка можно ли делать postDTOCallback. Все ли данные уже получены из бд.
                break;
            case REQUEST_PICTURE_1:
                postDTOList.get(pos).setPicture1(uri);
                doPostCallback(postListSize, postDTOList, postDTOCallback); // проверка можно ли делать postDTOCallback. Все ли данные уже получены из бд.
                break;
            case REQUEST_PICTURE_2:
                postDTOList.get(pos).setPicture2(uri);
                doPostCallback(postListSize, postDTOList, postDTOCallback); // проверка можно ли делать postDTOCallback. Все ли данные уже получены из бд.
                break;
            case REQUEST_AVATAR_COMMENT:
                commentsDTOList.get(pos).setAvatar(uri);
                doCommentCallback(commentsListSize, commentsDTOList, commentsDTOCallback);
            default:
                break;
        }

    }

    @Override
    public void onPostFailed(Exception error) {
        Log.w(TAG, "onPostFailed: post didn't get. Check log", error);

    }

    @Override
    public void onUserLoadFailed(Exception error) {
        Log.w(TAG, "onUserLoadFailed: data about user didn't get. Check log", error);
    }

    private void getProfilePhoto(String fileName, int requestCode, int pos) {
        Log.d(TAG, "getProfilePhoto: get photo from profile photo");
        String filePath = "profileImage/" + fileName;
        StorageReference riversRef = storageRef.child(filePath);

        PictureLoader pictureLoader = new PictureLoader(riversRef, this);
        pictureLoader.getPhotoUri(riversRef, requestCode, pos);
    }

    private void getPicture(String fileName, int requestCode, int pos) {
        Log.d(TAG, "getPicture: get picture from post folder");
        String filePath = "post/" + fileName;
        StorageReference riversRef = storageRef.child(filePath);

        PictureLoader pictureLoader = new PictureLoader(riversRef, this);
        pictureLoader.getPhotoUri(riversRef, requestCode, pos);
    }

    private void getUser(String userId, int requestCode, int pos) {
        userDAO.getUser(userId, requestCode, pos);
    }

    private void doPostCallback(int listSize, List<PostDTO> dtoList, PostDTOCallback callback) {
        int checkPos = listSize - 1;
        if(dtoList.get(checkPos).getAvatar() != null
                && dtoList.get(checkPos).getPicture1() != null
                && dtoList.get(checkPos).getPicture2() != null
                && dtoList.get(checkPos).getUserName() != null) {
            callback.toDto(dtoList);
        }
    }

    private void doCommentCallback(int listSize, List<CommentsDTO> dtoList, CommentsDTOCallback callback) {
        int checkPos = listSize - 1;
        if(dtoList.get(checkPos).getAvatar() != null
                && dtoList.get(checkPos).getUserName() != null) {
            callback.toCommentsDot(dtoList);
        }
    }
}
