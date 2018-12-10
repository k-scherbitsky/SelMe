package com.selme.presenter.interfaces;

import com.selme.model.dto.CommentsDTO;

import java.util.List;

public interface CommentsDTOCallback {

    void toCommentsDot(List<CommentsDTO> commentsDTOList);

}
