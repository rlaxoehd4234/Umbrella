package com.umbrella.dto.comment;

import lombok.Getter;

@Getter
public class CommentRequestDto {

    private String content;
    private String nickName;
    private int pageNumber;

}
