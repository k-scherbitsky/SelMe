package com.selme.interfaces;

import com.selme.dto.PostDTO;

import java.util.List;

public interface PostDTOCallback {

    void toDto(List<PostDTO> dto);
}
