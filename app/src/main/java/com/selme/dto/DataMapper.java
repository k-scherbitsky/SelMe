package com.selme.dto;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.StorageReference;
import com.selme.dao.PostDAO;
import com.selme.dao.UserDAO;
import com.selme.entity.PostEntity;
import com.selme.entity.UserEntity;
import com.selme.interfaces.PictureLoaderCallback;
import com.selme.interfaces.PostDAOCallback;
import com.selme.interfaces.PostDTOCallback;
import com.selme.interfaces.UserDAOCallback;
import com.selme.modal.PictureLoader;

import java.util.ArrayList;
import java.util.List;

public class DataMapper implements UserDAOCallback, PostDAOCallback, PictureLoaderCallback {

    private static final String TAG = "DataMapper";
    private static final int REQUEST_AVATAR = 100;
    private static final int REQUEST_PICTURE_1 = 200;
    private static final int REQUEST_PICTURE_2 = 300;
    private static final int REQUEST_USERNAME = 400;

    private StorageReference storageRef;
    private PostDTOCallback callback;
    private List<PostDTO> dtoList;
    private UserDAO userDAO;
    private int listSize = -1;

    public DataMapper(StorageReference storageRef) {
        this.storageRef = storageRef;
    }

    public void toPostDto(PostDTOCallback callback) {
        this.callback = callback;

        userDAO = new UserDAO(this);

        PostDAO postDAO = new PostDAO(this);
        postDAO.getPost();
    }

    @Override
    public void onPostLoaded(List<PostEntity> postEntityList) {
        dtoList = new ArrayList<>();

        if (postEntityList != null) {

            listSize = postEntityList.size();

            for (int i = 0; i < postEntityList.size(); i++) {
                getUser(postEntityList.get(i).getUserId(), i); // получение аватара, имени и фамилии по userId

                getPicture(postEntityList.get(i).getPhoto1(), REQUEST_PICTURE_1, i);
                getPicture(postEntityList.get(i).getPhoto2(), REQUEST_PICTURE_2, i);

                PostDTO dto = new PostDTO();

                dto.setTitle(postEntityList.get(i).getTitle());
                dto.setDescription(postEntityList.get(i).getDescription());
                dto.setDocId(postEntityList.get(i).getDocId());
                dto.setCreatedDate(postEntityList.get(i).getCreatedDate());

                int pickPic1 = postEntityList.get(i).getPickPic1();
                int pickPic2 = postEntityList.get(i).getPickPic2();
                int amountPickPic = pickPic1 + pickPic2;

                dto.setPickPic1(pickPic1);
                dto.setPickPic2(pickPic2);
                dto.setAmountPickPic(amountPickPic);

                dtoList.add(dto);
            }
        }
    }

    @Override
    public void onUserLoaded(UserEntity user, int requestCode, int pos) {

        if(requestCode == REQUEST_USERNAME){
            String userNameText = user.getFirstName() + " " +user.getLastName();
            String fileName = user.getProfilePhoto();

            dtoList.get(pos).setUserName(userNameText);
            getProfilePhoto(fileName, pos);
        }

        doCallback(listSize, dtoList, callback);
    }

    @Override
    public void onPictureDownloaded(Uri uri, int requestCode, int pos) {

        switch (requestCode) {
            case REQUEST_AVATAR:
                dtoList.get(pos).setAvatar(uri);
                break;
            case REQUEST_PICTURE_1:
                dtoList.get(pos).setPicture1(uri);
                break;
            case REQUEST_PICTURE_2:
                dtoList.get(pos).setPicture2(uri);
                break;
            default:
                break;
        }

        doCallback(listSize, dtoList, callback);
    }


    @Override
    public void onPostFailed(Exception error) {
        Log.w(TAG, "onPostFailed: post didn't get. Check log", error);

    }

    @Override
    public void onUserLoadFailed(Exception error) {
        Log.w(TAG, "onUserLoadFailed: data about user didn't get. Check log", error);
    }

    private void getProfilePhoto(String fileName, int pos) {
        Log.d(TAG, "getProfilePhoto: get photo from profile photo");
        String filePath = "profileImage/" + fileName;
        StorageReference riversRef = storageRef.child(filePath);

        PictureLoader pictureLoader = new PictureLoader(riversRef, this);
        pictureLoader.getPhotoUri(riversRef, REQUEST_AVATAR, pos);
    }

    private void getPicture(String fileName, int requestCode, int pos) {
        Log.d(TAG, "getPicture: get picture from post folder");
        String filePath = "post/" + fileName;
        StorageReference riversRef = storageRef.child(filePath);

        PictureLoader pictureLoader = new PictureLoader(riversRef, this);
        pictureLoader.getPhotoUri(riversRef, requestCode, pos);
    }

    private void getUser(String userId, int pos) {
        userDAO.getUser(userId, REQUEST_USERNAME, pos);
    }

    private void doCallback(int listSize, List<PostDTO> dtoList, PostDTOCallback callback) {
        int checkPos = listSize - 1;
        if(dtoList.get(checkPos).getAvatar() != null
                && dtoList.get(checkPos).getPicture1() != null
                && dtoList.get(checkPos).getPicture2() != null
                && dtoList.get(checkPos).getUserName() != null) {
            callback.toDto(dtoList);
        }
    }
}
