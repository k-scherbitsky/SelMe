package com.selme.interfaces;

import com.selme.dto.CommentsDTO;

import java.util.List;

public interface CommentsDTOCallback {

    void toCommentsDot(List<CommentsDTO> commentsDTOList);

}
