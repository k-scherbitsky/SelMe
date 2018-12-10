package com.selme.presenter.interfaces;

import com.selme.model.dto.PostDTO;

import java.util.List;

public interface PostDTOCallback {

    void toDto(List<PostDTO> dto);
}
