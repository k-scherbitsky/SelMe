package com.selme.interfaces;

import com.selme.entity.UserEntity;

public interface UserDAOCallback {

    void onLoaded(UserEntity user);

    void onFailed(Exception error);

}
