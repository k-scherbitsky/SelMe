package com.selme.interfaces;

import com.selme.entity.PostEntity;

import java.util.List;

public interface PostDAOCallback {
    void onPostLoaded(List<PostEntity> postEntityList);

    void onPostFailed(Exception error);
}
