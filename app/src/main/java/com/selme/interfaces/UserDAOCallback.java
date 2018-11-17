package com.selme.interfaces;

import com.selme.entity.UserEntity;

public interface UserDAOCallback {

    void onUserLoaded(UserEntity user, int requestCode, int pos);

    void onUserLoadFailed(Exception error);

}
