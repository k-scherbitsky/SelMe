package com.selme.presenter.interfaces;

import com.selme.model.entity.PostEntity;

import java.util.List;

public interface PostDAOCallback {
    void onPostLoaded(List<PostEntity> postEntityList);

    void onPostFailed(Exception error);
}
