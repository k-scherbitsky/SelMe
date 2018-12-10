package com.selme.presenter.interfaces;

import com.selme.model.entity.UserEntity;

public interface UserDAOCallback {

    void onUserLoaded(UserEntity user, int requestCode, int pos);

    void onUserLoadFailed(Exception error);

}
